package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voltras.blockseatservice.entities.AirlineData;

public interface AirlineDataRepository extends JpaRepository<AirlineData, UUID> {
	AirlineData getByOperatingCode(String operatingCode);

	Optional<AirlineData> findByOperatingCode(String operatingCode);

	@Query("SELECT a FROM AirlineData a WHERE a.operatingCode IN (:operatingCodes)")
	List<AirlineData> findByOperatingCodes(List<String> operatingCodes);
}