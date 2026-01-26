package com.demo.api.ApiRepositoryV2;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.demo.dao.V2.DownloadData;
import com.demo.dao.V2.Endpoint1;
import com.demo.dao.V2.Generation;
import com.demo.dao.generationData.ResponseGetIntervaOfEnnergyMix;
import com.demo.dao.windonData.IntervalData;





public interface SimpleApi {
    List<Endpoint1> calculateAverageSharesForDays(ZonedDateTime from,  ZonedDateTime to);
    float calculateCleanEnergyPercent(Generation input);
    Generation calculateAverageValues(List<Generation>input);
    Map<Integer,List<Generation>> groupIntervalsByDate(DownloadData input);
    DownloadData getIntervalOfEnergyMix(String from , String to) throws IOException ;

    List<Generation> generateSumArray(DownloadData intervalDatas);
    Generation findBestWindow(List<Generation> generations, int intervals);

}
