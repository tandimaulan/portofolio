package com.voltras.ppob.service.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductType {
	PLN_PREPAID("PLN-PREPAID"), 
	PLN_POSTPAID("PLN-POSTPAID"), 
	PULSA_PRABAYAR("PULSA-PRABAYAR"),
	PULSA_PAKETDATA("PULSA-PAKETDATA"),
	PULSA_PREPAID("PULSA-PREPAID"),
	PDAM_P("PDAM-P"), 
	BPJS_KS("BPJS-KS"), 
	SAMOLNAS("SAMOLNAS"), 
	MULTIFINANCE("MULTIFINANCE") ;

	private String stringValue;

	private ProductType(String stringValue) {
		this.stringValue = stringValue;
	}

	@JsonCreator
	public static ProductType fromString(String text) {
		for (ProductType type : ProductType.values()) {
			if (type.stringValue.equalsIgnoreCase(text))
				return type;
		}
		return null;
	}

	@JsonValue
	public String toValue() {
		return stringValue;
	}
}
