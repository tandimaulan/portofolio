package com.voltras.ppob.service.components;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
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

	// Gateway
	public String SHA256Generator(String cid, String dateYYYYMMDD, String secretKey) {
		String input = cid + dateYYYYMMDD + secretKey;
		byte[] sha256Bytes = DigestUtils.sha256(input);
		return Hex.encodeHexString(sha256Bytes);
	}

	public String generateClientReference(String customerNumber) {
		String currentDate = getCurrentDate();
		String generatedCode = "VLTRS" + customerNumber + currentDate + uniqueCurrentTime();
		return generatedCode;
	}

	private static String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(new Date());
	}


}