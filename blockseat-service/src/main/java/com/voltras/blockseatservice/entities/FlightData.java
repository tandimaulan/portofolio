package com.voltras.blockseatservice.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voltras.blockseat.api.models.FlightSegment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = { @Index(name = "flight_data_idx", columnList = "inventoryId") })
public class FlightData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private UUID id;

	private UUID inventoryId;
	private String segmentId;
	private Integer segmentNum;
	private String createdBy;
	private Date createdAt;
	private String updatedBy;
	private Date updatedAt;

	@Column(columnDefinition = "json")
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JdbcTypeCode(SqlTypes.JSON)
	@JsonProperty("segmentDetails")
	private FlightSegment segmentDetails;

	public FlightData(UUID inventoryId, Integer segmentNum, String createdBy, Date createdAt,
			FlightSegment segmentDetails) {
		super();
		this.inventoryId = inventoryId;
		this.segmentNum = segmentNum;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.segmentDetails = segmentDetails;
	}

	public FlightData(UUID id, UUID inventoryId, String segmentId, Integer segmentNum, String createdBy, Date createdAt,
			String updatedBy, Date updatedAt, FlightSegment segmentDetails) {
		super();
		this.id = id;
		this.inventoryId = inventoryId;
		this.segmentId = segmentId;
		this.segmentNum = segmentNum;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.updatedBy = updatedBy;
		this.updatedAt = updatedAt;
		this.segmentDetails = segmentDetails;

	}
}
