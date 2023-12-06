package com.voltras.blockseat.api.exceptions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.voltras.voltrasspring.exceptions.exceptions.BusinessProcessException;

public class PriceHasChangedException extends BusinessProcessException {
	private static final long serialVersionUID = -2512098387382555862L;

	private Double newPrice;

	public PriceHasChangedException(@NotNull @Min(1) Double newPrice) {
		super(newPrice);
		this.newPrice = newPrice;

	}

	public Double getNewPrice() {
		return newPrice;
	}
}