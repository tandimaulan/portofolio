package com.voltras.blockseat.api.models;

import org.springframework.validation.annotation.Validated;

@Validated
public record Fare(String subClass, Double price, Integer seatCapacity, Boolean isActive) {

}
/**
 * @param subClass 			subClass of journey
 * @param price 			price of journey based of subClass
 * @param seatCapacity		seat capacity of journey
 * @param isActive			condition fare active or not active
 */