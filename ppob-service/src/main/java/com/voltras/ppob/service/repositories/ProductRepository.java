package com.voltras.ppob.service.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voltras.ppob.service.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

	Product findByCode(String code);
	
	List<Product> getByCode(String code);

	List<Product> findByGroup(String type);

	List<Product> findByType(String type);
}