package com.voltras.ppob.gateway.api.responses.plnpostpaid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PLNPOSTPAIDInquiryResp {
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
	@JsonProperty("subid")
	private String subid;
	@JsonProperty("nama")
	private String nama;
	@JsonProperty("bulan")
	private String bulan;
	@JsonProperty("blth")
	private String blth;
	@JsonProperty("tagihan")
	private String tagihan;
	@JsonProperty("admin")
	private String admin;
	@JsonProperty("total")
	private String total;
	@JsonProperty("tarif")
	private String tarif;
	@JsonProperty("bln_1")
	private String bln_1;
	@JsonProperty("meter_1")
	private String meter_1;
	@JsonProperty("tagbln_1")
	private String tagbln_1;
	@JsonProperty("admin_1")
	private String admin_1;
	@JsonProperty("penalty_1")
	private String penalty_1;
	@JsonProperty("duedate_1")
	private String duedate_1;
	@JsonProperty("incentive_1")
	private String incentive_1;
	@JsonProperty("meterread1")
	private String meterread1;
	@JsonProperty("bln_2")
	private String bln_2;
	@JsonProperty("meter_2")
	private String meter_2;
	@JsonProperty("tagbln_2")
	private String tagbln_2;
	@JsonProperty("admin_2")
	private String admin_2;
	@JsonProperty("penalty_2")
	private String penalty_2;
	@JsonProperty("duedate_2")
	private String duedate_2;
	@JsonProperty("incentive_2")
	private String incentive_2;
	@JsonProperty("meterread2")
	private String meterread2;
	@JsonProperty("bln_3")
	private String bln_3;
	@JsonProperty("meter_3")
	private String meter_3;
	@JsonProperty("tagbln_3")
	private String tagbln_3;
	@JsonProperty("admin_3")
	private String admin_3;
	@JsonProperty("penalty_3")
	private String penalty_3;
	@JsonProperty("duedate_3")
	private String duedate_3;
	@JsonProperty("incentive_3")
	private String incentive_3;
	@JsonProperty("meterread3")
	private String meterread3;
	@JsonProperty("bln_4")
	private String bln_4;
	@JsonProperty("meter_4")
	private String meter_4;
	@JsonProperty("tagbln_4")
	private String tagbln_4;
	@JsonProperty("admin_4")
	private String admin_4;
	@JsonProperty("penalty_4")
	private String penalty_4;
	@JsonProperty("duedate_4")
	private String duedate_4;
	@JsonProperty("incentive_4")
	private String incentive_4;
	@JsonProperty("meterread4")
	private String meterread4;
	@JsonProperty("sunit")
	private String sunit;
	@JsonProperty("sphone")
	private String sphone;
	@JsonProperty("trxid")
	private String trxid;
	
}
