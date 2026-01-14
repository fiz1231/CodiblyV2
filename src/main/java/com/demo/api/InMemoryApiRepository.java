package com.demo.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.demo.api.ApiRepository;
import com.demo.dao.generationData.ResponseGetIntervaOfEnnergyMix;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public  class InMemoryApiRepository extends ApiRepository  {
    Map<Long,ResponseGetIntervaOfEnnergyMix>db = new HashMap<>();
    AtomicInteger id =new AtomicInteger();
    public ResponseGetIntervaOfEnnergyMix save(ResponseGetIntervaOfEnnergyMix inputInterval) {
       
        db.put(Long.valueOf(this.id.getAndIncrement()),inputInterval);
            return inputInterval;
    }
    @Override
    public List<ResponseGetIntervaOfEnnergyMix> getIntervalOfEnergyMix(String from , String to) throws IOException{
        List<ResponseGetIntervaOfEnnergyMix> dataResult = new ArrayList<>();
        int start=0,end = 0;
       
        for (Long intervalId : db.keySet()){
            System.out.println("End::"+db.get(intervalId).getTo()+"==="+to+":"+db.get(intervalId).getTo().equals(to));
           
            if(db.get(intervalId).getFrom().equals(from)){
                start = intervalId.intValue();
                System.out.println("Start::"+start);
            }
            if(db.get(intervalId).getTo().equals(to)){
                end = intervalId.intValue();
                
            }
        }
        for (;start<=end;start++){
            dataResult.add(db.get((long)start));
        }
        return dataResult;

    }
}
