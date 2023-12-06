package com.voltras.ppob.api.services;

import java.util.Date;

import com.voltras.core.common.api.enums.PrintType;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.common.api.exceptions.GatewayTimeoutException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface PpobTicketingService {

	String print(@NotBlank String bookingCode, @NotNull PrintType type, @NotNull Boolean shareToEmail)
			throws DataNotFoundException, GatewayTimeoutException;

	String exportTransactions(@NotNull Date from, @NotNull Date to) throws DataNotFoundException;

}
