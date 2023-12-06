package com.voltras.ppob.api.services;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.voltras.ppob.api.models.availability.Product;
import com.voltras.ppob.api.models.availability.ProductDetail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface PpobAvailabilityService {
	List<Product> getActiveProducts();

	List<ProductDetail> getProductDetails(@NotNull @Valid Product product, String phoneNumber);
}