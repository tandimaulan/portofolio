package com.voltras.ppob.service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.ppob.service.entities.Office;

public interface OfficeRepository extends JpaRepository<Office, Long> {
	Optional<Office> findByCode(String code);
}
