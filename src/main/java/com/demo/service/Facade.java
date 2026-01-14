package com.demo.service;

import com.demo.dao.generationData.GenerationOutput;
import com.demo.dao.windonData.WindonDataOutput;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class Facade {
    private final DataGetterExternalApi dataGetterExternalApi;
    private final DataLoadingWindowDeterminer dataLoadingWindowDeterminer;

    public GenerationOutput getData(){
        return dataGetterExternalApi.calculateAverageSharesForDays();
    }
    public WindonDataOutput getLoadinfWindown(int windowDurationHours){
        return dataLoadingWindowDeterminer.getDataLoadingWindow(windowDurationHours);
        
    }
}
