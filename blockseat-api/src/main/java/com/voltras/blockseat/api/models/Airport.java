package com.voltras.blockseat.api.models;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;


@Validated
public record Airport(@NotBlank String iata, @NotBlank String name, @NotBlank String city, @NotBlank String country) {
}
/**
 *@param iata		iata of airport
 *@param name		name of airport
 *@param city		city of airport
 *@param country 	country of airport
 */