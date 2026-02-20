package com.demo.api.ApiRepositoryV2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



@Configuration
@PropertySource("classpath:application.properties")
public class ApiConfig {
    @Value("${url}")
    private String url;
    @Bean
    public SimpleApi simpleBeanApi(){
        System.out.println("Property url:"+this.url);
        var bean = new ApiImpl();
        bean.setUrl(url);
        return bean;
    }    
}
