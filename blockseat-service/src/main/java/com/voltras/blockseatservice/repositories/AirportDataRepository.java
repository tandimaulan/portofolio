package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voltras.blockseatservice.entities.AirportData;

public interface AirportDataRepository extends JpaRepository<AirportData, UUID> {
	List<AirportData> findByIataCode(String iataCode);

	@Query("SELECT a.timezone FROM AirportData a WHERE a.iataCode = :iataCode")
	Integer getTimezonefindByIataCode(String iataCode);
}