package com.demo.service.serviceV2;

import org.springframework.stereotype.Service;

import com.demo.api.ApiRepositoryV2.ApiImpl;
import com.demo.dao.V2.Endpoint1;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OptimalLoadingWindow {
     private final ApiImpl apiRepository;
     public Endpoint1 getDataLoadingWindow(int windowDurationHours){
        if(windowDurationHours>=1 && windowDurationHours<=6){
        return (apiRepository.calculateOptimalLoadingWindow(windowDurationHours));
        }
        else{
            return new Endpoint1();
        }
    }
}
