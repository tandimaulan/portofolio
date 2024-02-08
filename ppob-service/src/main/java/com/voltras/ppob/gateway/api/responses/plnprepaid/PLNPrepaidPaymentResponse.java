package com.voltras.ppob.gateway.api.responses.plnprepaid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PLNPrepaidPaymentResponse {
	@JsonProperty("status")
	private String status;
	@JsonProperty("rc")
	private String rc;
	@JsonProperty("rcm")
	private String rcm;
	@JsonProperty("text")
	private String text;
	@JsonProperty("refnum")
	private String refnum;
	@JsonProperty("msn")
	private String msn;
	@JsonProperty("subid")
	private String subid;
	@JsonProperty("nam")
	private String nama;
	@JsonProperty("tarifdaya")
	private String tarifdaya;
	@JsonProperty("nominal")
	private String nominal;
	@JsonProperty("jmlkwh")
	private String jmlkwh;
	@JsonProperty("token")
	private String token;
	@JsonProperty("ppn")
	private String ppn;
	@JsonProperty("ppj")
	private Long ppj;
	@JsonProperty("admin")
	private String admin;
	@JsonProperty("angsuran")
	private String angsuran;
	@JsonProperty("materai")
	private String materai;
	@JsonProperty("info")
	private String info;
	@JsonProperty("rptoken")
	private Long rptoken;
	@JsonProperty("distcode")
	private String distcode;
	@JsonProperty("sunit")
	private String sunit;
	@JsonProperty("suphone")
	private String suphone;
	@JsonProperty("maxkwh")
	private String maxkwh;
	@JsonProperty("trxid")
	private String trxid;
}
