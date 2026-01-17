package com.demo.api.ApiRepositoryV2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.demo.dao.V2.DownloadData;
import com.demo.dao.V2.Generation;
import com.demo.dao.generationData.GenerationMix;



public interface SimpleApi {
    DownloadData getIntervalOfEnergyMix(String from , String to) throws IOException ;
    Map<Integer,List<Generation>> groupIntervalsByDate(DownloadData input);
    Generation calculateAverageValues(List<Generation>input);
}
