package com.voltras.blockseat.api.models;

import java.time.LocalDateTime;

import com.voltras.core.common.api.enums.BookingStatus;

public record RetrieveFilter(LocalDateTime from, LocalDateTime to, BookingStatus status) {

}
/**
 * @param from			start date for pnr data
 * @param to			end date for pnr data
 * @param status		status pnr data
 */