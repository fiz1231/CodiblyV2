package com.demo.api.ApiRepositoryV2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.demo.dao.V2.DownloadData;
import com.demo.dao.V2.Generation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiImpl implements SimpleApi {
    
    private static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    public List<Generation> getIntervalOfEnergyMix(String from , String to){
        
        
        System.out.println("Execution : getIntervalOfEnergyMix");
        String url = "https://api.carbonintensity.org.uk/generation/"+ from + "/" + to;// maybe use DateTimeFormatter?
        DownloadData result = null;
        StringBuffer response = new StringBuffer();
        try{
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
        
 
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        result = objectMapper.readValue(response.toString(),new TypeReference<DownloadData>(){});
        }
        catch(IOException e){
            e.printStackTrace();
        }
       
       
        
        return result.generation();

    }
}
