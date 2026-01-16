package com.demo.api.ApiRepositoryV2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import com.demo.dao.V2.DownloadData;
import com.demo.dao.V2.Generation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiImpl implements SimpleApi {
    
    private static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
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
}
