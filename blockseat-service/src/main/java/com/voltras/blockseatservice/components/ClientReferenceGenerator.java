package com.voltras.blockseatservice.components;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class ClientReferenceGenerator {
	private static final AtomicLong LAST_TIME_MS = new AtomicLong();

	private static long uniqueCurrentTime() {
		long now = Instant.now().toEpochMilli();
		while (true) {
			long lastTime = LAST_TIME_MS.get();
			if (lastTime >= now) {
				now = lastTime + 1;
			}
			if (LAST_TIME_MS.compareAndSet(lastTime, now)) {
				return now;
			}
		}
	}

	public String generateClientReference() {
		return "BS" + uniqueCurrentTime();
	}
}
