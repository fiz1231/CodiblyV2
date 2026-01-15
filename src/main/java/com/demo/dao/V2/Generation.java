package com.demo.dao.V2;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Generation {
    private String from;
    private String to;
    private List<GenerationMix> generationmix;
}
