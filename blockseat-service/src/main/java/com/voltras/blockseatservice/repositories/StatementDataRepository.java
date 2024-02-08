package com.voltras.blockseatservice.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.voltras.blockseatservice.entities.StatementData;
import com.voltras.payment.common.api.enums.PaymentStatus;

import jakarta.persistence.LockModeType;

@EnableJpaRepositories
public interface StatementDataRepository extends JpaRepository<StatementData, UUID> {

	List<StatementData> findByInventoryId(String inventryId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("FROM StatementData b WHERE b.paymentStatus = :status AND b.createdAt < :date")
	List<StatementData> findByPassingTimelimitAt(PaymentStatus status, LocalDateTime date);

//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	@Query("FROM StatementData WHERE status = 'WAITING' AND isChecking = false AND createdAt < :date")
//	List<StatementData> findWaitingStatusPassingCreatedAt(LocalDateTime date);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT sd FROM StatementData sd WHERE sd.paymentStatus = 'WAITING' AND sd.isChecking = false AND sd.createdAt < :date")
	List<StatementData> findWaitingStatusPassingCreatedAt(@Param("date") LocalDateTime date);

}
