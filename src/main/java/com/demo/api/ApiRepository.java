package com.demo.api;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.context.annotation.Configuration;

import com.demo.dao.generationData.GenerationInput;
import com.demo.dao.generationData.GenerationMix;
import com.demo.dao.generationData.GenerationOutput;
import com.demo.dao.generationData.ResponseGetIntervaOfEnnergyMix;
import com.demo.dao.generationData.ResponseProcessedIntervalOfEnergyMix;
import com.demo.dao.windonData.IntervalData;
import com.demo.dao.windonData.WindonDataOutput;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
@Configuration
@AllArgsConstructor
public  class  ApiRepository  {

    private static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    
    List<ResponseGetIntervaOfEnnergyMix> getIntervalOfEnergyMix(String from , String to) throws IOException{
        // URL obj = new URL("https://api.carbonintensity.org.uk/generation/"+ from + "/" + to);
        // HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // con.setRequestMethod("GET");
        // int responseCode = con.getResponseCode();
        // BufferedReader in = new BufferedReader(
        //     new InputStreamReader(con.getInputStream()));
        // String inputLine;
        // StringBuffer response = new StringBuffer();
        // while ((inputLine = in.readLine()) != null) {
        //     response.append(inputLine);
        // }
        // in.close();
        // System.out.println(response.toString());
        // return response.toString();
        
        
        /*I dont know why but upper code downloaded from https://carbon-intensity.github.io/api-definitions/?java#get-generation works but down one doesnt. 
        Why?
        // DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        
        // wr.flush();

        // wr.close();

        this lines couse problems

        Why have I even write output stream to api?

        Oh, now i remember

        
        
         */
      
        System.out.println("Execution : getIntervalOfEnergyMix");
        String url = "https://api.carbonintensity.org.uk/generation/"+ from + "/" + to;// maybe use DateTimeFormatter?
        URL objUrl = new URL(url);
        
        HttpsURLConnection con = (HttpsURLConnection) objUrl.openConnection();
        con.setRequestMethod("GET");       
        con.setRequestProperty("Accept", "application/json");
        
        
        con.setDoOutput(true);
        // DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        
        // wr.flush();

        // wr.close();
        
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

        //System.out.println(response.toString());
        
        GenerationInput result = objectMapper.readValue(response.toString(),new TypeReference<GenerationInput>(){});
         System.out.println("dupa");
       
        
        return result.data();

    }
    
    private Map<String,List<ResponseGetIntervaOfEnnergyMix>> groupIntervalsByDate(List<ResponseGetIntervaOfEnnergyMix> intervalListInput){
        
        System.out.println("Execution : groupIntervalsByDate");
        return intervalListInput.stream().collect(Collectors.groupingBy(
            interval->Integer.toString(ZonedDateTime.parse(interval.getFrom()).getDayOfYear()),Collectors.toList()));

    }
    
    private Map<String,Double> calculateAverageValues​(List<ResponseGetIntervaOfEnnergyMix> intervalsGroupedForDay){
        System.out.println("Execution : calculateAverageValues");
        return intervalsGroupedForDay.stream().map(x->x.getGenerationmix()).flatMap(x->x.stream()).collect(Collectors.groupingBy(x->x.fuel(),Collectors.mapping(x->x.perc(),Collectors.averagingDouble(x->x.doubleValue()))));
    }
    
    private float calculateCleanEnergyPercent(Map<String,Double> averageDayValues){
        System.out.println("Execution : calculateCleanEnergyPercent");
        List.of("biomass", "nuclear", "hydro", "wind", "solar");
        float result=0;
        for (String key : averageDayValues.keySet()){
            if(List.of("biomass", "nuclear", "hydro", "wind", "solar").contains(key)){
                result+=averageDayValues.get(key);
            }
        }
        return result;
    }
    
    public GenerationOutput calculateAverageSharesForDays(ZonedDateTime from,  ZonedDateTime to){
        
        System.out.println("Execution : calculateAverageSharesForDays");
        DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        List<ResponseProcessedIntervalOfEnergyMix> returnObject =new ArrayList<ResponseProcessedIntervalOfEnergyMix>();
        try{
            List<ResponseGetIntervaOfEnnergyMix> apiCallResponse = this.getIntervalOfEnergyMix(from.format(format),to.format(format));
            
            Map<String,List<ResponseGetIntervaOfEnnergyMix>> apiCallResponseGroupedByday = groupIntervalsByDate(apiCallResponse);
            List<GenerationMix> resultGenerationMixs;
            ResponseProcessedIntervalOfEnergyMix outputData;
            System.out.println("keyset: "+apiCallResponseGroupedByday.keySet());
            for (String dayKey :apiCallResponseGroupedByday.keySet()){

                resultGenerationMixs=new ArrayList<>();
                System.out.println("daykey: "+dayKey);
                outputData = new ResponseProcessedIntervalOfEnergyMix();
                Map<String,Double> dayAverageMix = this.calculateAverageValues​(apiCallResponseGroupedByday.get(dayKey));
                
                for (String fuel : dayAverageMix.keySet()){
                    
                    resultGenerationMixs.add(new GenerationMix(fuel,dayAverageMix.get(fuel).floatValue()));
                }
                for(var x :resultGenerationMixs ){
                    System.out.println(x.fuel()+":"+x.perc());
                }

                outputData.setFrom(from.format(format));
                from = from.plusDays(1);
                outputData.setTo(from.format(format));
                
                outputData.setGenerationmix(resultGenerationMixs);
                outputData.setCleanEnergyPercent(this.calculateCleanEnergyPercent(dayAverageMix));
                returnObject.add(outputData);
                
                
            }
            System.out.println("Createed output ogject");
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return new GenerationOutput(returnObject);
    }

    //Endpoint 2 
    private List<IntervalData> calculateClearEnergyForEveryIntervals(List<ResponseGetIntervaOfEnnergyMix>intervalListInput){
        System.out.println("Execution : calculateClearEnergyForEveryIntervals");
        System.out.println("input size: "+intervalListInput.size());
        List<IntervalData> result = new ArrayList<IntervalData>();
         float greenEnergyPercent=0;
        
        for(ResponseGetIntervaOfEnnergyMix interval : intervalListInput){
            for(GenerationMix intervalGenerationMix :interval.getGenerationmix()){
                if(List.of("biomass", "nuclear", "hydro", "wind", "solar").contains(intervalGenerationMix.fuel())){
                    
                    greenEnergyPercent+=intervalGenerationMix.perc();
                    
                }
                else{
                    continue;
                }
            }
            System.out.println("GreenEnergyPercentToResult: "+greenEnergyPercent);
            result.add(new IntervalData(interval.getFrom(),interval.getTo(),greenEnergyPercent));
            greenEnergyPercent=0;
        }
        return result;

    }
      private float[] generateSumArray(List<IntervalData> intervalDatas){
        System.out.println("Execution : generateSumArray");
        float[] validateArray = new float[intervalDatas.size()];
        validateArray[0]=intervalDatas.get(0).cleanEnergyPercent();

            for(int i=1; i<validateArray.length ;i++){
                
                validateArray[i] = intervalDatas.get(i).cleanEnergyPercent()+intervalDatas.get(i-1).cleanEnergyPercent();
                System.out.println("ValidateArray: "+i+validateArray[i]);
            }
            return validateArray;
        }
        
        
       


    public WindonDataOutput calculateOptimalChargingWindow(ZonedDateTime from,  ZonedDateTime to, int windowDurationHours){
        System.out.println("Input Date From: "+from.toString());
        System.out.println("Input Date to: "+to.toString());
        System.out.println("Execution : calculateOptimalChargingWindow");
        DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        WindonDataOutput result = new WindonDataOutput();
        try{
            List<ResponseGetIntervaOfEnnergyMix> apiCallResponse = this.getIntervalOfEnergyMix(from.format(format),to.format(format));
            System.out.println("apiCallResponse size: "+ apiCallResponse.size());
            //Process count for every interval percento of green energy 
            List<IntervalData> intervalsWithcleenEnergyPercent = this.calculateClearEnergyForEveryIntervals(apiCallResponse);
            System.out.println("intervalsWithcleenEnergyPercent size: "+intervalsWithcleenEnergyPercent.size());
            float[] sumArray = this.generateSumArray(intervalsWithcleenEnergyPercent);
            

            //count how many intervals there is 
            //int intervalsWindowrange =(int)TimeUnit.SECONDS.toMinutes(to.toEpochSecond() - from.toEpochSecond())/30;
            int intervalsWindowrange =(int) TimeUnit.HOURS.toMinutes((long)windowDurationHours)/30;
            System.out.println("intervalswindowsrange "+intervalsWindowrange);
            //find the best window
            float theBestResult=0f;
            int theBestIndexStart=0;
            int theBestIndexEnd=intervalsWindowrange;
            
            for (int start = 0;start+intervalsWindowrange<sumArray.length; start++){
                // System.out.println("start Index: " + start);
                // System.out.println("start End: " + (start+intervalsWindowrange));
                // System.out.println("result : "+ (sumArray[start+intervalsWindowrange] - (start!=0 ? 0f:sumArray[start])));
                if( ((sumArray[start+intervalsWindowrange] - (start!=0 ? 0f:sumArray[start]))/intervalsWindowrange) >theBestResult){
                    theBestResult = sumArray[start+intervalsWindowrange]/intervalsWindowrange;
                    theBestIndexStart = start;
                    theBestIndexEnd = start+intervalsWindowrange;
                    System.out.println("theBestResult: "+theBestResult);
                }

            }
            //i have range of intevals, not wime to send them to result object
            System.out.println("the best index start: "+theBestIndexStart);
            System.out.println("the best index end: "+theBestIndexEnd);
            System.out.println("the best result: "+theBestResult);
            System.out.println("Input hour Window: "+windowDurationHours);
            
            result.setCleanEnergyPercent(theBestResult);
            result.setFrom(intervalsWithcleenEnergyPercent.get(theBestIndexStart).to().formatted(format));
            result.setTo(intervalsWithcleenEnergyPercent.get(theBestIndexEnd).to().formatted(format));
            
            
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return result;
        //return new GenerationOutput(returnObject);
    }
    // testing repository method
    // compile exec:java -Dexec.mainClass="com.demo.api.ApiRepository"
//     public void main(){
//         ApiRepository test = new ApiRepository();
//         try {
//             //test.getIntervalOfEnergyMix("2025-01-20T12:00Z","2025-01-20T13:30Z").forEach(e->System.out.println("Result: "+e.getGenerationmix()));
//             // var test2=test.groupIntervalsByDate(test.getIntervalOfEnergyMix("2025-01-20T12:00Z","2025-01-23T13:30Z"));

//             // var test3=test.calculateAverageValues​(test2.entrySet().stream().findAny().get().getValue());
//             // System.out.println(test3.values());
//             System.out.println("Createed output ogject");
//             //Side note if you send https://api.carbonintensity.org.uk/generation/2025-10-05T12:00Z/2025-10-05T12:00Z to api you will get error, maybe it's not recognize noon from midnight?
//             ZonedDateTime from = ZonedDateTime.of(2025, 10, 5 ,0 , 0, 0, 0, ZoneId.of("Z"));
//             ZonedDateTime to = ZonedDateTime.of(2025, 10, 5 ,12 , 0, 0, 0, ZoneId.of("Z"));
           
//             WindonDataOutput test5 = test.calculateOptimalChargingWindow(from, to,6);
//             System.out.println(test5.getCleanEnergyPercent());
        
//         }
//         catch (Exception e ){
//             System.out.println("Exception "+e.getMessage());
//         }
//     }
// }
}