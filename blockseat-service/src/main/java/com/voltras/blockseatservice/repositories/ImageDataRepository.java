package com.voltras.blockseatservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voltras.blockseatservice.entities.ImageData;

public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
	@Query("SELECT id.image FROM ImageData id WHERE id.description = :description")
	Optional<String> findByDescription(String description);
}
