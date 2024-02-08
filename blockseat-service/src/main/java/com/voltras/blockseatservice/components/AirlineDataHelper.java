package com.voltras.blockseatservice.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseatservice.entities.AirlineData;
import com.voltras.blockseatservice.repositories.AirlineDataRepository;

@Service
public class AirlineDataHelper {
	@Autowired
	private AirlineDataRepository airlineDataRepo;

	public AirlineData getFromOperatingCode(String operatingCode) {
		return airlineDataRepo.findByOperatingCode(operatingCode).get();
	}
}
