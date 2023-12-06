package com.voltras.ppob.api.models.book;

import com.voltras.ppob.api.models.ProductCode;

public record Fare(ProductCode productCode, String number, String power, Double price, Double adminFee,
		Double penaltie) {

}
