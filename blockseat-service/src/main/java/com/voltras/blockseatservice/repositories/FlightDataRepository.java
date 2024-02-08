package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voltras.blockseatservice.entities.FlightData;

public interface FlightDataRepository extends JpaRepository<FlightData, UUID> {

	List<FlightData> findByInventoryId(UUID inventoryId);

	List<FlightData> findByInventoryIdOrderBySegmentNumDesc(UUID inventoryId);

	List<FlightData> findByInventoryIdOrderBySegmentNumAsc(UUID inventoryId);

	@Query("SELECT f FROM FlightData f WHERE f.inventoryId = :inventoryId ORDER BY f.segmentNum DESC")
	Optional<FlightData> findTopByInventoryIdOrderBySegmentNumDesc(@Param("inventoryId") UUID inventoryId);

	FlightData findBySegmentId(String segmentId);

	FlightData findByInventoryIdAndSegmentNum(UUID inventoryId, Integer segmentNum);
}
