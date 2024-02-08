package com.voltras.ppob.gateway.api.responses.plnprepaid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PLNPreInquiryResponse {

	@JsonProperty("status")
	String status;
	@JsonProperty("rc")
	String rc;
	@JsonProperty("rcm")
	String rcm;
	@JsonProperty("text")
	String text;
	@JsonProperty("refnum")
	String refnum;
	@JsonProperty("msn")
	String msn;
	@JsonProperty("subid")
	String subid;
	@JsonProperty("nama")
	String nama;
	@JsonProperty("tarif")
	String tarif;
	@JsonProperty("admin")
	Double admin;
	@JsonProperty("unsold")
	String unsold;
	@JsonProperty("denomunsold")
	String denomunsold;
	@JsonProperty("distcode")
	String distcode;
	@JsonProperty("sunit")
	String sunit;
	@JsonProperty("suphone")
	String suphone;
	@JsonProperty("maxkwh")
	String maxkwh;
	@JsonProperty("trxid")
	String trxid;
}
