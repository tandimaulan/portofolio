package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseatservice.entities.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

	public Inventory getByInventoryId(UUID inventoryId);

	List<Inventory> findBySupplierDataSupplierEmail(String supplierEmail);

	public Optional<Inventory> findByInventoryId(UUID inventoryId);

	public List<Inventory> findAllByInventoryId(UUID inventoryId);

	@Query("SELECT i FROM Inventory i JOIN i.supplierData s WHERE s.supplierEmail = :supplierEmail AND i.id = :inventoryId")
	Optional<Inventory> findBySupplierDataSupplierEmailAndInventoryId(String supplierEmail, UUID inventoryId);

	@Query("SELECT a FROM Inventory a WHERE LOWER(a.searchTag) LIKE CONCAT('%', LOWER(:searchTag), '%') AND a.cabinClass = :cabinClass")
	public List<Inventory> findBySearchTagAndCabinClass(@RequestParam String searchTag, CabinClass cabinClass);

	List<Inventory> findByCabinClass(CabinClass cabinClass);

}