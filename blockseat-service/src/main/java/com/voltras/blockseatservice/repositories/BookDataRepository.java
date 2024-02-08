package com.voltras.blockseatservice.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voltras.blockseatservice.entities.BookData;
import com.voltras.core.common.api.enums.BookingStatus;

import jakarta.persistence.LockModeType;

public interface BookDataRepository extends JpaRepository<BookData, UUID> {

	@Query("SELECT i FROM BookData i WHERE i.inventoryId = :inventoryId")
	List<BookData> findByInventoryId(UUID inventoryId);

	@Query("SELECT b FROM BookData b WHERE b.bookDate BETWEEN :from AND :to"
			+ " AND (b.status = 'NEW' OR b.status = 'RESERVED' OR b.status = 'CONFIRMED')")
	List<BookData> findByBookDate(LocalDateTime from, LocalDateTime to);

	@Query("SELECT i FROM BookData i WHERE i.inventoryId = :inventoryId AND i.bookDate BETWEEN :from AND :to AND (i.status = 'NEW' OR i.status = 'RESERVED' OR i.status = 'CONFIRMED')")
	List<BookData> findByInventoryIdAndBookDateAndStatus(UUID inventoryId, LocalDateTime from, LocalDateTime to);

	Optional<BookData> findByBookingCodeAndOfficeCode(String bookingCode, String officeCode);

	Optional<BookData> getByBookingCode(String bookingCode);

	List<BookData> findByOfficeCode(String officeCode);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM BookData b WHERE b.status = :status AND b.isChecking = false")
	List<BookData> findAllByStatus(BookingStatus status);

	@Query("SELECT b FROM BookData b LEFT JOIN FETCH b.contactData cd "
			+ "WHERE b.domain = :domain AND b.bookingCode = :bookingCode AND cd.customerEmail = :email")
	Optional<BookData> getSpecificWithCommonParam(String domain, String bookingCode, String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("FROM BookData b WHERE (b.status = 'NEW' OR b.status = 'RESERVED' OR b.status = 'PENDING')"
			+ "AND b.isChecking = false AND b.timelimit < :date")
	List<BookData> findByPassingTimelimit(LocalDateTime date);
	
	
	@Query("SELECT b FROM BookData b JOIN b.statement s "
	        + "WHERE s.createdAt >= :startOfDay AND s.createdAt <= :endOfDay "
	        + "AND b.status = 'CONFIRMED' AND (:officeCode IS NULL OR b.officeCode = :officeCode) "
	        + "ORDER BY s.createdAt DESC")
	Page<BookData> findByCreatedAtAndBookingStatusAndOfficeCode(
	        @Param("startOfDay") LocalDateTime startOfDay,
	        @Param("endOfDay") LocalDateTime endOfDay,
	        String officeCode,
	        Pageable pageable);

}
