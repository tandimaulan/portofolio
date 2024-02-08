package com.voltras.blockseatservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.DomainData;

public interface DomainDataRepository extends JpaRepository<DomainData, Integer> {
	DomainData findByName(String name);
	DomainData findByCode(String code);
	
}
