package com.voltras.blockseatservice.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voltras.blockseat.admin.api.enums.TimelimitCondition;
import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseat.api.models.FlightSummary;
import com.voltras.core.common.api.enums.BookingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = { @Index(name = "book_data_idx", columnList = "id") })
public class BookData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue()
	private UUID id;

	@Enumerated(EnumType.STRING)
	private CabinClass cabinClass;

	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	@Enumerated(EnumType.STRING)
	private TimelimitCondition timelimitCondition;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "bookid", referencedColumnName = "id")
	private List<PassengerData> passengers;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "bookid", referencedColumnName = "id")
	private List<StatementData> statement;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "contactid", referencedColumnName = "id")
	private ContactData contactData;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "financialid", referencedColumnName = "id")
	private FinancialStatement financialStatement;

	@Column(columnDefinition = "json")
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JdbcTypeCode(SqlTypes.JSON)
	@JsonProperty("flightSummary")
	private FlightSummary flightSummary;

	@Column(columnDefinition = "json")
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JdbcTypeCode(SqlTypes.JSON)
	@JsonProperty("segmentDetails")
	private List<FlightSegment> segmentDetails;

	private UUID inventoryId;
	private String blockseatName;
	private String bookedBy;
	private String bookingCode;
	private LocalDateTime bookDate;
	private Double pricePerSeat;
	private Double totalPrice;
	private Double downPaymentPrice;
	private LocalDateTime timelimit;
	private String timelimitDescription;
	private Integer seatCount;
	private String remarks;
	private String subClass;
	private Double totalPayment;
	private Double outstanding;
	private Boolean isChecking = false;
	private Boolean isInternational;
	private String domain;
	private String goblinAccountId;
	private String officeCode;
	private String officeName;
	private String officePackage;
	private String cancelBy;
	private LocalDateTime cancelAt;
	private String paymentChannel;
	private String transactionId;
	private String reversalTransactionId;
	private String inventoryNotes;
	private String inventoryTitle;
	private String notes;
	private String title;

	public BookData(UUID inventoryId, String bookingCode, LocalDateTime bookDate, String bookedBy, Double totalPrice,
			LocalDateTime timelimit, String timelimitDescription, Integer seatCount, String remarks,
			CabinClass cabinClass, String subClass, List<PassengerData> passengers, ContactData contactData) {
		super();
		this.inventoryId = inventoryId;
		this.bookingCode = bookingCode;
		this.bookDate = bookDate;
		this.bookedBy = bookedBy;
		this.totalPrice = totalPrice;
		this.timelimit = timelimit;
		this.timelimitDescription = timelimitDescription;
		this.seatCount = seatCount;
		this.remarks = remarks;
		this.cabinClass = cabinClass;
		this.subClass = subClass;
		this.passengers = passengers;
		this.contactData = contactData;
	}

	public BookData(UUID inventoryId, String blockseatName, String bookingCode, LocalDateTime bookDate, String bookedBy,
			List<StatementData> statement, Double totalPrice, LocalDateTime timelimit, String timelimitDescription,
			Integer seatCount, String remarks, String subClass, Double outstanding, CabinClass cabinClass,
			BookingStatus status, List<PassengerData> passengers, ContactData contactData,
			FlightSummary flightSummary) {
		super();
		this.inventoryId = inventoryId;
		this.blockseatName = blockseatName;
		this.bookingCode = bookingCode;
		this.bookDate = bookDate;
		this.bookedBy = bookedBy;
		this.statement = statement;
		this.totalPrice = totalPrice;
		this.timelimit = timelimit;
		this.timelimitDescription = timelimitDescription;
		this.seatCount = seatCount;
		this.remarks = remarks;
		this.subClass = subClass;
		this.outstanding = outstanding;
		this.cabinClass = cabinClass;
		this.status = status;
		this.passengers = passengers;
		this.contactData = contactData;
		this.flightSummary = flightSummary;
	}

}
