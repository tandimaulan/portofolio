package com.voltras.ppob.service.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.ppob.api.models.ProductCode;
import com.voltras.ppob.service.entities.BookData;

import jakarta.persistence.LockModeType;

@Repository
public interface BookDataRepository extends JpaRepository<BookData, UUID> {
	@Query("SELECT b FROM BookData b " + "WHERE b.officeCode = :officeCode AND "
			+ "b.customerProductNumber = :customerNumber AND " + "b.productCode = :productCode AND "
			+ "b.bookDate = (SELECT MAX(b2.bookDate) FROM BookData b2 " + "WHERE b2.officeCode = :officeCode AND "
			+ "b2.customerProductNumber = :customerNumber AND " + "b2.productCode = :productCode)")
	Optional<BookData> findByProductCodeAndCustomerNumberAndOffice(@Param("productCode") String productCode,
			@Param("customerNumber") String customerNumber, @Param("officeCode") String officeCode);

	Optional<BookData> findFirstByCustomerProductNumberAndProductCodeAndStatusOrderByBookDateDesc(String number,
			String code, BookingStatus status);

	Optional<BookData> findFirstByOrderByBookDateDesc();

	@Query("SELECT b FROM BookData b WHERE (b.officeCode = :officeCode)")
	List<BookData> findAllByOfficeCode(String officeCode, Pageable pageable);

	@Query("SELECT b FROM BookData b WHERE (b.officeCode = :officeCode)")
	List<BookData> findAllByOfficeCodeNoPage(String officeCode);

	@Query("SELECT b FROM BookData b WHERE (b.bookingCode = :bookingCode)")
	List<BookData> findByBookingCode(String bookingCode);

	@Query("SELECT b FROM BookData b WHERE (b.officeCode = :officeCode) AND bookDate BETWEEN :fromDate "
			+ "AND :toDate AND(:stat IS NULL OR status = :stat)")
	List<BookData> findAllByOfficeCodeWithFilter(String officeCode, Date fromDate, Date toDate, BookingStatus stat,
			Pageable pageable);

	@Query("SELECT b FROM BookData b WHERE (b.officeCode = :officeCode) AND "
			+ "(:status IS NULL OR b.status = :status) AND (b.bookDate BETWEEN :from AND :to) AND "
			+ "(:productType IS NULL OR b.productType = :productType)")
	List<BookData> findAllByOfficeCodeAndBookDateAndStatusAndProductType(String officeCode, Date from, Date to,
			BookingStatus status, Pageable pageable, ProductCode productType);

	@Query("SELECT b FROM BookData b WHERE b.officeCode = :officeCode AND "
			+ "b.status = 'CONFIRMED' AND b.bookDate BETWEEN :from AND :to")
	List<BookData> getAllByOfficeCodeAndBookDateAndStatusAndProductType(@Param("officeCode") String officeCode,
			@Param("from") Date from, @Param("to") Date to, Sort sort);

	Optional<BookData> findByBookingCodeAndOfficeCode(String bookingCode, String officeCode);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM BookData b WHERE b.status = :status AND b.isChecking = false")
	List<BookData> findAllByStatus(BookingStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("FROM BookData b WHERE b.paymentStatus = :status AND b.bookDate < :date")
	List<BookData> findByPassingTimelimitAt(PaymentStatus status, Date date);

	@Query("SELECT b FROM BookData b LEFT JOIN FETCH b.contactData cd "
			+ "WHERE b.domain = :domain AND b.bookingCode = :bookingCode AND cd.email = :email")
	Optional<BookData> getSpecificWithCommonParam(String domain, String bookingCode, String email);

	@Query("SELECT b FROM BookData b WHERE (b.officeCode = :code) AND "
			+ "((:bookingCode IS NULL) OR b.bookingCode = :bookingCode) AND (b.bookDate BETWEEN :from AND :to)")
	List<BookData> listAllByOfficeCodeAndBookingDateAndBookingCode(String code, Date from, Date to, String bookingCode);

	@Query("SELECT b FROM BookData b WHERE b.bookingCode = :bookingCode AND b.officeCode = :officeCode")
	Optional<BookData> findByOfficeCodeAndBookingCode(String officeCode, String bookingCode);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("FROM BookData b WHERE (b.status = 'NEW' OR b.status = 'RESERVED' OR b.status = 'PENDING')"
			+ "AND b.isChecking = false AND b.timelimit < :date")
	List<BookData> findByPassingTimelimit(Date date);

	@Query("SELECT b FROM BookData b WHERE b.status = 'CONFIRMED' AND "
			+ "((:officeCode IS NULL) OR b.officeCode = :officeCode) AND (b.paymentDate BETWEEN :from AND :to)")
	Page<BookData> findByPaymentDateAndBookingStatusAndOfficeCode(Date from, Date to, String officeCode,
			Pageable pageable);
}
