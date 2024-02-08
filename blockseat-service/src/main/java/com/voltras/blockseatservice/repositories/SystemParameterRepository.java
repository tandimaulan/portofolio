package com.voltras.blockseatservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.SystemParameter;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, String> {
	Optional<SystemParameter> findByKey(String key);
}
