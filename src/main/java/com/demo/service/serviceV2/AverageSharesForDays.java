package com.demo.service.serviceV2;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import com.demo.api.ApiRepositoryV2.ApiImpl;
import com.demo.dao.V2.Endpoint1;

@Service
@Qualifier("apiImpl")
@AllArgsConstructor
@ComponentScan("com.demo.api.ApiRepositoryV2")
public class AverageSharesForDays {
    @Autowired
    private final ApiImpl apiImpl;
    public List<Endpoint1> calculateAverageSharesForDays(){
        ZonedDateTime from = ZonedDateTime.now().withHour(0).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z"));
        
        ZonedDateTime to =from.plusDays(2);
        return apiImpl.calculateAverageSharesForDays(from, to);
        
    }
}
