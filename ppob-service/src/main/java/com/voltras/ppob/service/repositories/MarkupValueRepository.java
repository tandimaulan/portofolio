package com.voltras.ppob.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.ppob.service.entities.MarkupValue;

public interface MarkupValueRepository extends JpaRepository<MarkupValue, Long>{
	MarkupValue findByProductCodeAndPackageName (String productCode, String packageName);

}
