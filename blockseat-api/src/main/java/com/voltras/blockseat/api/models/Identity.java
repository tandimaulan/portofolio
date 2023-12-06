package com.voltras.blockseat.api.models;

import java.time.LocalDate;

public record Identity(
		String number,
		String nationality,
		String issuingCountry,
		LocalDate expirationDate) {
}
/**
 * @param number			number indentity of person
 * @param nationaly			nationaly of person
 * @param issuingCountry	issuing country of person
 * @param expirationDate	expiration date identity data person
 */
