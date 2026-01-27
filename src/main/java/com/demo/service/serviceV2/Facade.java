package com.demo.service.serviceV2;

import com.demo.dao.V2.Endpoint1;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class Facade {
    private final AverageSharesForDays averageSharesForDays;
    private final OptimalLoadingWindow optimalLoadingWindow;

    public List<Endpoint1> getaverageSharesForDays(){
        return averageSharesForDays.calculateAverageSharesForDays();
    }
    public Endpoint1 getLoadinfWindown(int windowDurationHours){
        return optimalLoadingWindow.getDataLoadingWindow(windowDurationHours);
        
    }
}
