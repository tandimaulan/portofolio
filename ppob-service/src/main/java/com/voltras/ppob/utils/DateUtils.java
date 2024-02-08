package com.voltras.ppob.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtils{
	public static LocalDateTime convertDateToLocalDateTimeWithJoda(Date date) {
		var localDateTime = org.joda.time.LocalDateTime.fromDateFields(date);
		return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthOfYear(), localDateTime.getDayOfMonth(),
				localDateTime.getHourOfDay(), localDateTime.getMinuteOfHour());
	};
	
	public static Date getCurrentDate() {
		return new Date();
	}

	public static Date getMaxRetrieveDate(Date date) {
		date = date != null ? date : new Date();
		return Date.from(date.toInstant().plus(1, ChronoUnit.DAYS));
	}

	public static Date getMinRetrieveDate(Date date) {
		return date == null ? Date.from(Instant.parse("2000-01-01T00:00:00.00Z")) : date;
	}

	public static Instant getCurrentInstant() {
		return Instant.now();
	}

	public static LocalDate getCurrentLocalDate() {
		return LocalDate.now();
	}
}
