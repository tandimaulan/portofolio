package com.voltras.blockseatservice.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllotmentInfo {
	@Id
	@GeneratedValue
	private UUID id;
	private String subClass;
	private Double fare;
	private Integer allotment;
	private Integer countSeatDp;
	private Integer countSeatPaid;
}
