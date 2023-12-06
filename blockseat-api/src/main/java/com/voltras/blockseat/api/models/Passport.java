package com.voltras.blockseat.api.models;

import java.time.LocalDate;

public record Passport(
		String number,
		String nationality,
		LocalDate expirationDate) {
}
/**
 * @param number		 number of passport
 * @param nationality	 nationality of passport
 * @param expirationDate expiration date passport	
 */
