package com.voltras.blockseatservice.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.voltras.blockseatservice.entities.BookData;
import com.voltras.core.common.api.enums.BookingStatus;

@Component
public class BookHelper {
	@Autowired
	LogHelper log;

	public void bookDataStatusCheck(BookData data) {
		if (data.getStatus().equals(BookingStatus.RESERVED) || data.getStatus().equals(BookingStatus.NEW)) {
			return;
		}
		if (data.getStatus().equals(BookingStatus.CANCELED)) {
			log.error("[{}.bookDataStatusCheck] error: Percobaan book ketika status CANCELED",
					this.getClass().getCanonicalName());
			throw new RuntimeException();
		}
		log.error("[{}.bookDataStatusCheck] error: Percobaan issue ketika status bukan BOOKED ataupun CANCELED",
				this.getClass().getCanonicalName());
		throw new RuntimeException("Percobaan issue ketika status bukan BOOKED ataupun CANCELED");

	}
}