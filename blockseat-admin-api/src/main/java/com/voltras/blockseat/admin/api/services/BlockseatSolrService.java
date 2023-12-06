package com.voltras.blockseat.admin.api.services;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.voltras.blockseat.admin.api.models.BlockseatSolrRequest;
import com.voltras.blockseat.api.models.Airport;

/**
 * BlockseatSolrService
 */
@Validated
public interface BlockseatSolrService {
	/**
	 * @param request            Eequest parameter for searchAirport
	 * 
	 * @return {@link List} <{@link Airport> searchAirport Airport
	 * 
	 */
	List<Airport> searchAirport(@Valid BlockseatSolrRequest request);

}
