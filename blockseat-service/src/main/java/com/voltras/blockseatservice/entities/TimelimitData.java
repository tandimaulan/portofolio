package com.voltras.blockseatservice.entities;

import java.util.UUID;

import com.voltras.blockseat.admin.api.enums.TimelimitCondition;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(indexes = { @Index(name = "timelimit_data_idx", columnList = "inventoryId") })
public class TimelimitData {
	@Id
	@GeneratedValue
	private UUID id;

	private UUID inventoryId;
	private Integer dayFrom;
	private Integer dayTo;
	private Integer duration;
	
	@Enumerated(EnumType.STRING)
	private TimelimitCondition condition;

	// create
	public TimelimitData(UUID inventoryId, Integer dayFrom, Integer dayTo, Integer duration,
			TimelimitCondition condition) {
		super();
		this.inventoryId = inventoryId;
		this.dayFrom = dayFrom;
		this.dayTo = dayTo;
		this.duration = duration;
		this.condition = condition;
	}

	// edit
	public TimelimitData(UUID id, UUID inventoryId, Integer dayFrom, Integer dayTo, Integer duration,
			TimelimitCondition condition) {
		super();
		this.id = id;
		this.inventoryId = inventoryId;
		this.dayFrom = dayFrom;
		this.dayTo = dayTo;
		this.duration = duration;
		this.condition = condition;
	}
}
