package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.TimelimitData;

public interface TimelimitDataRepository extends JpaRepository<TimelimitData, UUID> {
	List<TimelimitData> findByInventoryId(UUID inventoryId);
}