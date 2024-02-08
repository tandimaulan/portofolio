package com.voltras.ppob.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

@Component
public class PagingUtil {
	public static Date getMaxRetrieveDate(Date date) {
		date = date != null ? date : new Date();
		return Date.from(date.toInstant().plus(1, ChronoUnit.DAYS));
	}

	public static Date getMinRetrieveDate(Date date) {
		return date != null ? date : Date.from(Instant.parse("2000-01-01T00:00:00.00Z"));
	}

	public static Pageable generatePageable(Integer page, Integer size, Map<String, Boolean> sortMapping) {
		var newSize = size == null ? 10 : size;
		var newPage = page == null ? 0 : page;

		var sort = Sort.unsorted();
		if (sortMapping != null && !sortMapping.isEmpty()) {
			sortMapping = sortMapping.entrySet().stream().filter(sortType -> sortType.getValue() != null)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			if (!sortMapping.isEmpty()) {
				sort = Sort.by(sortMapping.entrySet().stream()
						.map(sortType -> sortType.getValue().equals(true) ? Order.asc(sortType.getKey())
								: Order.desc(sortType.getKey()))
						.toList());
			}
		}

		return PageRequest.of(newPage, newSize, sort);
	}
}
