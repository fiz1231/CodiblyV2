package com.demo.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataLoadingWindowRequest {
    private Integer hourWindow;

    public DataLoadingWindowRequest(Integer hourWindow){
        this.hourWindow=hourWindow;
    }
}
