package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseatservice.entities.FareData;

public interface FareDataRepository extends JpaRepository<FareData, UUID> {

	public List<FareData> findByCabinClass(CabinClass cabinClass);

	List<FareData> findByInventoryId(UUID inventoryId);

	FareData findByInventoryIdAndSubClass(UUID inventoryId, String subClass);
}
