package com.voltras.blockseatservice.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.DataReport;

public interface BlockseatDataReportRepository extends JpaRepository<DataReport, UUID> {

}
