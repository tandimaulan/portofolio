package com.voltras.ppob.service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentParam {
	private String date;
	private String description;
	private String amount;
}
