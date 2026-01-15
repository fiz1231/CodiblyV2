package com.demo.api.ApiRepositoryV2;

import java.util.List;

import com.demo.dao.V2.Generation;


public interface SimpleApi {
    List<Generation> getIntervalOfEnergyMix(String from , String to) ;
}
