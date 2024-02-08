package com.voltras.ppob.service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.ppob.service.entities.SystemParameter;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, String> {
	Optional<SystemParameter> findByKey(String key);
}
