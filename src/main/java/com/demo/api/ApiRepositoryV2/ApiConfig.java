package com.demo.api.ApiRepositoryV2;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



@Configuration
@PropertySource("classpath:Data_en_GB.properties")
public class ApiConfig {
    @Value("${url}")
    private String url;

    @Value("${date.format}")
    private String dateFormat;

    @Bean
    public SimpleApi simpleBeanApi(){
        System.out.println("Property url:"+this.url);
        var bean = new ApiImpl();
        bean.setUrl(url);
        bean.setDateFormat(DateTimeFormatter.ofPattern(this.dateFormat));
        return bean;
    }    
}
