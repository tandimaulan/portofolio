package com.voltras.blockseat.api.models;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public record LocationInfo(@NotBlank String iata, @NotBlank String name, @NotBlank String city,
		@NotBlank String country, @NotNull LocalDateTime time) {
}
/**
 * @param iata    iata of airport
 * @param name    name of airport
 * @param city    city of airport
 * @param country country of airport
 * @param time    time of flight date
 */