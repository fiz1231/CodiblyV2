package com.demo.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import com.demo.api.ApiRepository;

import com.demo.dao.windonData.WindonDataOutput;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DataLoadingWindowDeterminer {
    private final ApiRepository apiRepository;
    public WindonDataOutput getDataLoadingWindow(int windowDurationHours){
        /*For some reason when execute ZonedDateTime.now().withHour(0).withMinute(0).withNano().withZoneSameInstant(ZoneId.of("Z")) return date with one day before 
        but when execute ZonedDateTime.now().withHour(1).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z")) the day is  ok
        */
        System.out.println("ZondeDateTime now with zondeif.of Z"+ZonedDateTime.now().withMinute(0).withNano(1).withZoneSameInstant(ZoneId.of("Z")).plusDays(1));
        ZonedDateTime from =ZonedDateTime.now().withHour(1).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z")).plusDays(1);
        ZonedDateTime to =ZonedDateTime.now().withHour(1).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z")).plusDays(2);
        if(windowDurationHours>=1 && windowDurationHours<=6){
        return (apiRepository.calculateOptimalChargingWindow(from, to,windowDurationHours));
        }
        else{
            return new WindonDataOutput();
        }
    }
}
