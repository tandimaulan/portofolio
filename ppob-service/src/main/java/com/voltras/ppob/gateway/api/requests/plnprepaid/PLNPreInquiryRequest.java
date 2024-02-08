package com.voltras.ppob.gateway.api.requests.plnprepaid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PLNPreInquiryRequest {
	@JsonProperty("cid")
	String cid;
	@JsonProperty("dt")
	String dt;
	@JsonProperty("hc")
	String hc;
	@JsonProperty("modul")
	String modul;
	@JsonProperty("command")
	String command;
	@JsonProperty("msn")
	String msn;
	@JsonProperty("nominal")
	String nominal;
	@JsonProperty("resp")
	String resp;
	@JsonProperty("trxid")
	String trxid;
}
