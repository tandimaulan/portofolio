package com.voltras.blockseat.api.services;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.JourneyOption;

@Validated
public interface BlockseatAvailabilityService {
	
	/**
	 * @param param            Journey param
	 * @param availableFrom    Journey available from
	 * @param availableTo      Journey available to
	 * @param cabinClass       Journey cabin class
	 * 
	 * @return {@link List} <{@link JourneyOption> search Availability
	 * 
	 */
	List<JourneyOption> search(String param, @NotNull LocalDate availableFrom, @NotNull LocalDate availableTo,
			CabinClass cabinClass);
}