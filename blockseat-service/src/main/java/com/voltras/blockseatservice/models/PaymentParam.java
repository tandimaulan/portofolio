package com.voltras.blockseatservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PaymentParam {
	private String date;
	private String description;
	private String amount;
}