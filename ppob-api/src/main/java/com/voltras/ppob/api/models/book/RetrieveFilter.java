package com.voltras.ppob.api.models.book;

import java.util.Date;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.ppob.api.models.ProductCode;

public record RetrieveFilter(Date from, Date to, BookingStatus status, ProductCode productCode) {

}
/**
 * @param from   		value to indicate start-line of date filtering
 * @param to     		value to indicate end-line of date filtering
 * @param status 		current transaction status
 * @param productCode 	product code transaction 
 */