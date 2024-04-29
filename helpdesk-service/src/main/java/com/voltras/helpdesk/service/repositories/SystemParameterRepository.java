package com.voltras.helpdesk.service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voltras.helpdesk.service.entities.SystemParameter;

@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
	Optional<SystemParameter> findByKey(String key);
}
