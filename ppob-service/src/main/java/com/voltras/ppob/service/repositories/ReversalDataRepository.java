package com.voltras.ppob.service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voltras.ppob.service.entities.ReversalData;

public interface ReversalDataRepository extends JpaRepository<ReversalData, Long> {
	@Query("FROM ReversalData WHERE actionDate IS NULL")
	List<ReversalData> findAllNullActionDate();
}
