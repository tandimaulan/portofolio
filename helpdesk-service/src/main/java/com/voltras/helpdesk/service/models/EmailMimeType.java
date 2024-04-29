package com.voltras.helpdesk.service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailMimeType {
	TEXT("text/plain"), HTML("text/html");

	private String stringValue;

	private EmailMimeType(String stringValue) {
		this.stringValue = stringValue;
	}

	@JsonCreator
	public static EmailMimeType fromString(String text) {
		for (EmailMimeType type : EmailMimeType.values()) {
			if (type.stringValue.equalsIgnoreCase(text))
				return type;
		}
		return null;
	}

	@JsonValue
	public String toValue() {
		return stringValue;
	}
}
