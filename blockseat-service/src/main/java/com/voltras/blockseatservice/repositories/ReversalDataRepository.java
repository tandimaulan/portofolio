package com.voltras.blockseatservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voltras.blockseatservice.entities.ReversalData;

public interface ReversalDataRepository extends JpaRepository<ReversalData, Long> {
	@Query("FROM ReversalData WHERE actionDate IS NULL")
	List<ReversalData> findAllNullActionDate();
}
