package com.voltras.blockseat.admin.api.models;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public record BlockseatSolrRequest(@NotBlank String keyword, Double latitude, Double longitude) {

}
/**
 * @param keyword 		keyword for search airport
 * @param latitude		is a coordinate position airport
 * @param longitude		is a coordiante positon airport
 */