package com.demo.dao.windonData;

import java.util.List;

import com.demo.dao.generationData.GenerationMix;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WindonDataOutput {
    private String from;
    private String to;
    private float cleanEnergyPercent; 
}
