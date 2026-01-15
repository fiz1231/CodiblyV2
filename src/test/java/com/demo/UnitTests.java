package com.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Description;
import org.junit.*;

import com.demo.api.ApiRepository;
import com.demo.api.ApiRepositoryV2.*;
import com.demo.api.InMemoryApiRepository;
import com.demo.dao.V2.Generation;
import com.demo.dao.generationData.GenerationMix;
import com.demo.dao.generationData.GenerationOutput;
import com.demo.dao.generationData.ResponseGetIntervaOfEnnergyMix;
import com.demo.dao.windonData.WindonDataOutput;
import com.demo.service.FacadeConfiguration;
import com.demo.service.Facade;



import org.springframework.context.ApplicationContext;

import org.springframework.context.ConfigurableApplicationContext;
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
    // @Test
    // @Description("should return data for three days with calculated average shares of individual energy sources and the percentage of clean energy. ")
    // public void testCalculateAverageSharesForDays(){
    //     //get
    //     float expectedOutput = 0.5f;
    //     GenerationOutput testOutput;
    //     //when
    //     testOutput = testFacade.getData();
    //     //then
        
    //     Assertions.assertThat(testOutput.data().getFirst().getCleanEnergyPercent()).isEqualTo(expectedOutput);
    // }
    // @Test
    // @Description("should return data for three days with calculated average shares of individual energy sources and the percentage of clean energy. ")
    // public void testGetLoadingWindow(){
    //     //get
    //     int testInput = 1;

    //     WindonDataOutput testOutput;
    //     System.out.println();
    //     //when
    //     testOutput = testFacade.getLoadinfWindown(testInput);
    //     //then
       
    //     Assertions.assertThat(TimeUnit.SECONDS.toHours(ZonedDateTime.parse(testOutput.getTo()).toEpochSecond() - ZonedDateTime.parse(testOutput.getFrom()).toEpochSecond())).isEqualTo(testInput);

    // }
    
    @Test
    @Description("checking context for ApiRepository bean ")
    public void testApiBeanGenerating(){
        //get
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ApiConfig.class);
        //when
        ApiConfig bean1 = ctx.getBean(ApiConfig.class);
        ApiConfig bean2 = ctx.getBean(ApiConfig.class);
        //then
        Assertions.assertEquals(bean1,bean2);
    }
    private ApiImpl mockApiImpl;
    @BeforeEach public void setUp(){
        mockApiImpl= new ApiImpl();
    }
    @Test
    @Description("testing data downloading from external api https://api.carbonintensity.org.uk/generation/")
    public void testGetCarboniteData(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(1).withZoneSameInstant(ZoneId.of("Z"));
            
        //when
            
        //then
            DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
            try{
            List <Generation> inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            inputData.forEach(s->System.out.println(s));
            Assertions.assertInstanceOf(List.class, inputData);
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
            
    }
}
