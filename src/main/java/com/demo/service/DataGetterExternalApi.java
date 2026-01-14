package com.demo.service;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import com.demo.api.ApiRepository;
import com.demo.dao.generationData.GenerationOutput;
@Service
@AllArgsConstructor
public class DataGetterExternalApi {
    private final ApiRepository apiRepository;
    public GenerationOutput calculateAverageSharesForDays(){
        ZonedDateTime from = ZonedDateTime.now().withHour(0).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z"));
        
        ZonedDateTime to =from.plusDays(2);
        return apiRepository.calculateAverageSharesForDays(from, to);
        
    }
}
