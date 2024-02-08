package com.voltras.ppob.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.ppob.service.entities.DomainData;

public interface DomainDataRepository extends JpaRepository<DomainData, Integer> {
	DomainData findByName(String name);
	DomainData findByCode(String code);
}
