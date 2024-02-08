package com.voltras.blockseatservice.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.SupplierData;

public interface SupplierDataRepository extends JpaRepository<SupplierData, UUID> {

	List<SupplierData> findBySupplierName(String supplierName);

}
