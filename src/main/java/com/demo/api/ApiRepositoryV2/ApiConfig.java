package com.demo.api.ApiRepositoryV2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ApiConfig {
    @Bean
    SimpleApi simpleBeanApi(){
        return new ApiImpl();
    }    
}
