package com.voltras.helpdesk.api.services;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.voltras.helpdesk.api.exceptions.RequirementMismatchException;
import com.voltras.helpdesk.api.models.Category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Validated
public interface AskService {
	public List<Category> getCategories();

	public Boolean ask(@NotEmpty String sender, @NotNull @Valid Category category, String body, String type,
			String subject, String bookingCode, String bankName, String bankCode, String accountName,
			String accountNumber, String branchBank, String phoneNumber) throws RequirementMismatchException;
}