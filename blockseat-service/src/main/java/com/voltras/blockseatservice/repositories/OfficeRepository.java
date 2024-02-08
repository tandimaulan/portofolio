package com.voltras.blockseatservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.Office;

public interface OfficeRepository extends JpaRepository<Office, Long>{
	Optional<Office> findByCode (String code);
}
