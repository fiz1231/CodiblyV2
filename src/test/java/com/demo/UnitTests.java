package com.demo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import com.demo.api.InMemoryApiRepository;
import com.demo.dao.generationData.GenerationMix;
import com.demo.dao.generationData.GenerationOutput;
import com.demo.dao.generationData.ResponseGetIntervaOfEnnergyMix;
import com.demo.dao.windonData.WindonDataOutput;
import com.demo.service.FacadeConfiguration;
import com.demo.service.Facade;
public class UnitTests {
    Facade testFacade;
    {
        System.out.println("Initializing test API repository");
        DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        ZonedDateTime from = ZonedDateTime.now().withHour(1).withMinute(0).withNano(0).withZoneSameInstant(ZoneId.of("Z"));
        
        
        ResponseGetIntervaOfEnnergyMix input;

        List<GenerationMix> generationMixList = new ArrayList<>();
        InMemoryApiRepository testRepo = new InMemoryApiRepository();

        int intervalsAmmount=3000;

        String [] fuels = new String[]{"biomass","coal","imports","gas","nuclear","other","hydro","solar","wind"};
         
        for(String fuel : fuels){
                generationMixList.add(new GenerationMix(fuel,0.1f));
            }

        for (int i =0;i<intervalsAmmount;i++){
            
            input = new ResponseGetIntervaOfEnnergyMix();
            input.setFrom(from.format(format));
            input.setTo(from.format(format));
            input.setGenerationmix(generationMixList);
            
            testRepo.save(input);

            from = from.plusMinutes(30);
            
            
        }
        
        
       
        testFacade = FacadeConfiguration.createCodiblyFacade(testRepo);
    }
    @Test
    @Description("should return data for three days with calculated average shares of individual energy sources and the percentage of clean energy. ")
    public void testCalculateAverageSharesForDays(){
        //get
        float expectedOutput = 0.5f;
        GenerationOutput testOutput;
        //when
        testOutput = testFacade.getData();
        //then
        
        Assertions.assertThat(testOutput.data().getFirst().getCleanEnergyPercent()).isEqualTo(expectedOutput);
    }
    @Test
    @Description("should return data for three days with calculated average shares of individual energy sources and the percentage of clean energy. ")
    public void testGetLoadingWindow(){
        //get
        int testInput = 1;

        WindonDataOutput testOutput;
        System.out.println();
        //when
        testOutput = testFacade.getLoadinfWindown(testInput);
        //then
       
        Assertions.assertThat(TimeUnit.SECONDS.toHours(ZonedDateTime.parse(testOutput.getTo()).toEpochSecond() - ZonedDateTime.parse(testOutput.getFrom()).toEpochSecond())).isEqualTo(testInput);

    }
}
