package com.demo.dao.V2;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Generation {
    @NotNull
    private String from;
    @NotNull
    private String to;
    private List<GenerationMix> generationmix;
}
