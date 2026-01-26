package com.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.demo.dao.V2.DownloadData;
import com.demo.dao.V2.Endpoint1;
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
              DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        //then
          
            try{
            DownloadData inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            Assertions.assertEquals(Boolean.FALSE, inputData.data().isEmpty());
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
            
    }
    @Test
    @Description("testing grouping generation based on day of the year")
    public void testGroupIntervalsByDate(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(3).withZoneSameInstant(ZoneId.of("Z"));
            
        //when
             DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        //then
           
            try{
            DownloadData inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            
            Map<Integer,List<Generation>> testGrouping =  mockApiImpl.groupIntervalsByDate(inputData);
            Assertions.assertEquals(Boolean.FALSE, testGrouping.keySet().isEmpty());
            
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
            
    }
    @Test
    @Description("testing the reduction of generation array so single generation element")
    public void testgenerationReduction(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(2).withZoneSameInstant(ZoneId.of("Z"));
            
        //when
            DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        //then
            
            try{
            DownloadData inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            
            Map<Integer,List<Generation>> testGrouping =  mockApiImpl.groupIntervalsByDate(inputData);
            Generation testAvarage = mockApiImpl.calculateAverageValues(testGrouping.get(testGrouping.keySet().toArray()[0]));
            Assertions.assertEquals(Boolean.TRUE, testAvarage.getGenerationmix().getFirst().getPerc()<100);
            
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
            
    }
    @Test
    @Description("testing the calculation of percent of green energy ")
    public void testCalculateCleanEnergyPercent(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(2).withZoneSameInstant(ZoneId.of("Z"));
            
        //when
             DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
        //then
           
            try{
            DownloadData inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            
            Map<Integer,List<Generation>> testGrouping =  mockApiImpl.groupIntervalsByDate(inputData);
            Generation testAvarage = mockApiImpl.calculateAverageValues(testGrouping.get(testGrouping.keySet().toArray()[0]));
            
            Assertions.assertInstanceOf(Float.class, mockApiImpl.calculateCleanEnergyPercent(testAvarage));
            
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
            
    }
    @Test
    @Description("testing endpoint 1 response")
    public void testEndpiont1(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(2).withZoneSameInstant(ZoneId.of("Z"));
        //when
            List<Endpoint1> testEndpoint1 = mockApiImpl.calculateAverageSharesForDays(now, then);
        //then

            Assertions.assertNotNull(testEndpoint1);;
            

        
    }
     @Test
    @Description("testing generating sumarry")
    public void testGenerateSumArray(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(2).withZoneSameInstant(ZoneId.of("Z"));
            
        //when
            DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
            try{
            DownloadData inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            
            
            List<Generation> testSumArray = mockApiImpl.generateSumArray(inputData);
        //then
            
            
            Assertions.assertEquals(Boolean.FALSE, testSumArray.isEmpty());
            
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    @Test
    @Description("testing generating sumarry")
    public void testFindBestWindow(){
        //get
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z"));
            ZonedDateTime then = ZonedDateTime.now().plusDays(2).withZoneSameInstant(ZoneId.of("Z"));
            
        //when
            DateTimeFormatter format =DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mmz");
            try{
            DownloadData inputData = mockApiImpl.getIntervalOfEnergyMix(now.format(format).toString(), then.format(format).toString());
            
            
            List<Generation> testSumArray = mockApiImpl.generateSumArray(inputData);
            Generation testBestWindow = mockApiImpl.findBestWindow(testSumArray, 2);
        //then
            
            
            Assertions.assertEquals(Boolean.FALSE, testBestWindow.getFrom()==null);
            
        }catch(IOException e){
                System.out.println(e.getMessage());
            }
            

        
    }
    @Test
    @Description("testing generating sumarry")
    public void testEndpoint2(){
        //get
            int hours = 2;
            
        //when
            
        //then
            Endpoint1 endpoint2 = mockApiImpl.calculateOptimalLoadingWindow(hours);
            
            Assertions.assertEquals(Boolean.FALSE, endpoint2.getGeneration().getFrom()==null);
            
        
        }
    
}
