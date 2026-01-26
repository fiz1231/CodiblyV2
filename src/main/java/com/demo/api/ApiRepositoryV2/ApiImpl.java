package com.demo.api.ApiRepositoryV2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import com.demo.dao.V2.DownloadData;
import com.demo.dao.V2.Endpoint1;
import com.demo.dao.V2.Generation;
import com.demo.dao.V2.GenerationMix;
import com.demo.dao.generationData.ResponseGetIntervaOfEnnergyMix;
import com.demo.dao.windonData.IntervalData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiImpl implements SimpleApi {
    
    private static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    //Endpoint 1 
    public List<Endpoint1> calculateAverageSharesForDays(ZonedDateTime from,  ZonedDateTime to){
        DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        DownloadData downloadData = null;
        try{
            downloadData = this.getIntervalOfEnergyMix(from.format(format), to.format(format));

        }
        catch (IOException e){
            System.out.println(e.getStackTrace());
        }

        Map<Integer,List<Generation>> groupedIntervals = null;

        if(downloadData != null){
            groupedIntervals = this.groupIntervalsByDate(downloadData);
        }
       List<Endpoint1> results = new ArrayList<Endpoint1>(); 
        if(groupedIntervals != null){
            groupedIntervals.forEach((K,V)-> this.calculateAverageValues(V));
            for (Integer key : groupedIntervals.keySet().stream().sorted().toList()){
                Generation input = groupedIntervals.get(key).getFirst();
                results.addLast(new Endpoint1(input ,this.calculateCleanEnergyPercent(input)));
            }
        }
        return results;
    }

    public DownloadData getIntervalOfEnergyMix(String from , String to) throws IOException{
        
        
       System.out.println("Execution : getIntervalOfEnergyMix");
        String url = "https://api.carbonintensity.org.uk/generation/"+ from + "/" + to;// maybe use DateTimeFormatter?
        URL objUrl = new URL(url);
        
        HttpsURLConnection con = (HttpsURLConnection) objUrl.openConnection();
        con.setRequestMethod("GET");       
        con.setRequestProperty("Accept", "application/json");
        
        
        con.setDoOutput(true);
        
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in;
        if(responseCode ==200){
        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        else{
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
       
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        DownloadData result = objectMapper.readValue(response.toString(),new TypeReference<DownloadData>(){});
       
        
        return result;
    }

    public Map<Integer,List<Generation>> groupIntervalsByDate(DownloadData input){
        
        int current = ZonedDateTime.parse(input.data().getFirst().getFrom()).getDayOfYear();        
        Map<Integer,List<Generation>> result = new HashMap<>();
        int checked = 0;
        result.putIfAbsent(current, new ArrayList<Generation>());

        for (Generation generation :input.data()){
            checked = ZonedDateTime.parse(generation.getFrom()).getDayOfYear();
            if (current == checked){
                result.get(checked).add(generation);
            }
            else{
                current = ZonedDateTime.parse(generation.getFrom()).getDayOfYear();
                result.putIfAbsent(checked, new ArrayList<Generation>());
                
            }
        }
        return result;
    }

    public Generation calculateAverageValues(List<Generation>input){
            Generation result = input.getFirst();
            for(int i=1;i<input.size();i++){
                result.setTo(input.get(i).getTo());
                List<GenerationMix> next = input.get(i).getGenerationmix();
                
                for(int j=0; j<next.size(); j++){
                    if(next.get(j).getFuel().equals(result.getGenerationmix().get(j).getFuel())){
                        result.getGenerationmix().get(j).setPerc(next.get(j).getPerc() + result.getGenerationmix().get(j).getPerc());
                    }
                }
            }
            result.getGenerationmix().forEach(x->x.setPerc(x.getPerc()/input.size()));

            return result;
    }

    public float calculateCleanEnergyPercent(Generation input){
        float result=0;
        for (GenerationMix key : input.getGenerationmix()){
            if(List.of("biomass", "nuclear", "hydro", "wind", "solar").contains(key.getFuel())){
                result+=key.getPerc();
            }
        }
        return result;
    }

    //EndPoint 2
    public Endpoint1 calculateOptimalLoadingWindow(int hours){
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
        ZonedDateTime then = ZonedDateTime.now().plusDays(2).withZoneSameInstant(ZoneId.of("Z"));
        Endpoint1 result = new Endpoint1();
        DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        DownloadData downloadData = null;
        try{
            downloadData = this.getIntervalOfEnergyMix(now.format(format), then.format(format));

        }
        catch (IOException e){
            System.out.println(e.getStackTrace());
        }
        List<Generation> sumArray = this.generateSumArray(downloadData);
        Generation interval = findBestWindow(sumArray, hours*2);
        try{
            downloadData = this.getIntervalOfEnergyMix(interval.getFrom(), interval.getTo());
        }
        catch (IOException e){
            System.out.println(e.getStackTrace());
        }
        if (downloadData!= null){
            result.setGeneration(this.calculateAverageValues(downloadData.data()));
            result.setGreenEnergyPercent(this.calculateCleanEnergyPercent(result.getGeneration()));
        }
        return result;
        
        
    }

    public List<Generation> generateSumArray(DownloadData input){
        List<Generation> data = input.data();
        Generation gen0,gen1 = null;
        for(int index0=0, index1=1;index1<data.size(); index0++,index1++){
            gen0 =data.get(index0);
            gen1 =data.get(index1);

            for(int genMix=0; genMix<gen0.getGenerationmix().size(); genMix++){
                gen1.getGenerationmix().get(genMix).setPerc(
                    gen1.getGenerationmix().get(genMix).getPerc() + gen0.getGenerationmix().get(genMix).getPerc()
                );
            }
            
        }
        return data;
    }

    public Generation findBestWindow(List<Generation> sumaArray, int intervals){
        Generation result = new Generation();
        float bestResult = 0;
        float averageValue = 0;
        for(int from=0, to=intervals; to<sumaArray.size(); from++, to++){
            Generation genFrom = sumaArray.get(from);
            Generation genTo = sumaArray.get(to);    
            for(int genMix = 0; genMix<genFrom.getGenerationmix().size(); genMix++){

                if(genFrom.getGenerationmix().get(genMix).getFuel().equals(genTo.getGenerationmix().get(genMix).getFuel()) && List.of("biomass", "nuclear", "hydro", "wind", "solar").contains(genFrom.getGenerationmix().get(genMix).getFuel())){
                    averageValue += (genTo.getGenerationmix().get(genMix).getPerc() - genFrom.getGenerationmix().get(genMix).getPerc())/intervals;
                }
            }
            if(bestResult<averageValue){
                    result.setFrom(genFrom.getFrom());
                    result.setTo(genTo.getTo());
                    bestResult = averageValue;
                }
                averageValue = 0;
            
        }
        return result;

    }



    
    

    


   
}
