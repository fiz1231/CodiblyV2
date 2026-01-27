package com.demo.service.serviceV2;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import com.demo.api.ApiRepositoryV2.ApiImpl;
import com.demo.dao.V2.Endpoint1;

@Service
@AllArgsConstructor
public class AverageSharesForDays {
    private final ApiImpl apiRepository;
    public List<Endpoint1> calculateAverageSharesForDays(){
        ZonedDateTime from = ZonedDateTime.now().withHour(0).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z"));
        
        ZonedDateTime to =from.plusDays(2);
        return apiRepository.calculateAverageSharesForDays(from, to);
        
    }
}
