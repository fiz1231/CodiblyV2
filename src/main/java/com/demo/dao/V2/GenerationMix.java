package com.demo.dao.V2;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GenerationMix {
    @NotNull
    String fuel;
    @NotNull
    @Positive(message = "Percent must be positive")
    @Min(0)
    @Max(100)
    float perc;
}
