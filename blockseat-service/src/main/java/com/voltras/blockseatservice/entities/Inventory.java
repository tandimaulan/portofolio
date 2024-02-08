package com.voltras.blockseatservice.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseat.api.models.FlightSummary;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Inventory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private UUID inventoryId;
	private String airlineName;
	private Integer seatAllot;
	private Integer seatCapacity;
	private String notes;
	private Integer minSeatBooking;
	private String searchTag;
	private String title;
	private Boolean isAvailable;
	private String subClass;
	private String createBy;
	private Date createdAt;
	private String updatedBy;
	private Date updatedAt;
	private Double downPaymentPrice;
	private Double price;

	@Enumerated(EnumType.STRING)
	private CabinClass cabinClass;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventoryId", referencedColumnName = "inventoryId")
	private List<FlightData> flightData;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventoryId", referencedColumnName = "inventoryId")
	private List<FareData> fareData;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventoryId", referencedColumnName = "inventoryId")
	private List<TimelimitData> timelimitData;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventoryId", referencedColumnName = "inventoryId")
	private List<SupplierData> supplierData;

	@Column(columnDefinition = "json")
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JdbcTypeCode(SqlTypes.JSON)
	@JsonProperty("flightSummary")
	private FlightSummary flightSummary;

	@Column(columnDefinition = "json")
	@JsonProperty("segmentDetails")
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JdbcTypeCode(SqlTypes.JSON)
	private List<FlightSegment> segmentDetails;

	// editInventory
	public Inventory(UUID inventoryId, String airlineName, Integer seatAllot, Integer seatCapacity, String notes,
			Integer minSeatBooking, String searchTag, String title, Boolean isAvailable, Double downPaymentPrice,
			String createBy, Date createdAt, String updatedBy, Date updatedAt, CabinClass cabinClass,
			List<FlightData> flightData, List<FareData> fareData, List<TimelimitData> timelimitData) {
		super();
		this.inventoryId = inventoryId;
		this.airlineName = airlineName;
		this.seatAllot = seatAllot;
		this.seatCapacity = seatCapacity;
		this.notes = notes;
		this.minSeatBooking = minSeatBooking;
		this.searchTag = searchTag;
		this.title = title;
		this.isAvailable = isAvailable;
		this.downPaymentPrice = downPaymentPrice;
		this.createBy = createBy;
		this.createdAt = createdAt;
		this.updatedBy = updatedBy;
		this.updatedAt = updatedAt;
		this.cabinClass = cabinClass;
		this.flightData = flightData;
		this.fareData = fareData;
		this.timelimitData = timelimitData;

	}

	// createInventory
	public Inventory(Integer seatAllot, String airlineName, Integer seatCapacity, String notes, Integer minSeatBooking,
			String searchTag, String title, Double downPaymentPrice, String createBy, Date createdAt, String updatedBy,
			Date updatedAt, CabinClass cabinClass) {
		super();
		this.seatAllot = seatAllot;
		this.airlineName = airlineName;
		this.seatCapacity = seatCapacity;
		this.notes = notes;
		this.minSeatBooking = minSeatBooking;
		this.searchTag = searchTag;
		this.title = title;
		this.isAvailable = true;
		this.downPaymentPrice = downPaymentPrice;
		this.createBy = createBy;
		this.createdAt = createdAt;
		this.updatedBy = updatedBy;
		this.updatedAt = updatedAt;
		this.cabinClass = cabinClass;
	}

}
