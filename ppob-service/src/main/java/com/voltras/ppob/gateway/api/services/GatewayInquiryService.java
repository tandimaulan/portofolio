package com.voltras.ppob.gateway.api.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltras.ppob.gateway.api.requests.bpjs.BPJSInquiryRequest;
import com.voltras.ppob.gateway.api.requests.pdam.PDAMInquiryRequest;
import com.voltras.ppob.gateway.api.requests.plnpostpaid.PLNPostpaidInquiryRequest;
import com.voltras.ppob.gateway.api.requests.plnprepaid.PLNPrepaidInquiryRequest;
import com.voltras.ppob.gateway.api.requests.voucher.VoucherInquiryRequest;
import com.voltras.ppob.gateway.api.responses.bpjs.BPJSInquiryResponse;
import com.voltras.ppob.gateway.api.responses.pdam.PDAMInquiryDetailResponse;
import com.voltras.ppob.gateway.api.responses.pdam.PDAMInquiryResponse;
import com.voltras.ppob.gateway.api.responses.plnpostpaid.PLNPostpaidInquiryDetailResponse;
import com.voltras.ppob.gateway.api.responses.plnpostpaid.PLNPostpaidInquiryResponse;
import com.voltras.ppob.gateway.api.responses.plnprepaid.PLNPreInquiryResponse;
import com.voltras.ppob.gateway.api.responses.voucher.VoucherInquiryResponse;

@Service
public class GatewayInquiryService {
	@Autowired
	private PpobSender sender;

	public PLNPreInquiryResponse PLNPREInquiry(PLNPrepaidInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("msn", request.msn());
		parameters.put("nominal", request.nominal());
		parameters.put("resp", request.resp());
		parameters.put("trxid", request.trxid());
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			PLNPreInquiryResponse inquiryResponse = mapper.treeToValue(jsonResponse, PLNPreInquiryResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}

	public PLNPostpaidInquiryResponse PLNPostpaidInquiry(PLNPostpaidInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("trxid", request.trxid());
		parameters.put("idpel", request.idpel());
		parameters.put("resp", request.resp());
		parameters.put("detail", "false");
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			System.out.println(jsonResponse);
			ObjectMapper mapper = new ObjectMapper();
			PLNPostpaidInquiryResponse inquiryResponse = mapper.treeToValue(jsonResponse,
					PLNPostpaidInquiryResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}

	public PLNPostpaidInquiryDetailResponse PLNPostpaidDetailInquiry(PLNPostpaidInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("trxid", request.trxid());
		parameters.put("idpel", request.idpel());
		parameters.put("resp", request.resp());
		parameters.put("detail", "true");
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			System.out.println(jsonResponse);
			ObjectMapper mapper = new ObjectMapper();
			PLNPostpaidInquiryDetailResponse inquiryResponse = mapper.treeToValue(jsonResponse,
					PLNPostpaidInquiryDetailResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}

	public BPJSInquiryResponse BPJSInquiry(BPJSInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("trxid", request.trxid());
		parameters.put("resp", request.resp());
		parameters.put("biller", request.biller());
		parameters.put("input1", request.input1());
		System.out.println(parameters);
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			System.out.println(jsonResponse);
			ObjectMapper mapper = new ObjectMapper();
			BPJSInquiryResponse inquiryResponse = mapper.treeToValue(jsonResponse, BPJSInquiryResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}

	public PDAMInquiryResponse PDAMInquiry(PDAMInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("trxid", request.trxid());
		parameters.put("idpel", request.idpel());
		parameters.put("resp", request.resp());
		parameters.put("detail", request.detail());
		parameters.put("biller", request.biller());
		System.out.println(parameters);
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			PDAMInquiryResponse inquiryResponse = mapper.treeToValue(jsonResponse, PDAMInquiryResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}

	public PDAMInquiryDetailResponse PDAMInquiryDetail(PDAMInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("trxid", request.trxid());
		parameters.put("idpel", request.idpel());
		parameters.put("resp", request.resp());
		parameters.put("detail", request.detail());
		parameters.put("biller", request.biller());
		System.out.println(parameters);
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			PDAMInquiryDetailResponse inquiryResponse = mapper.treeToValue(jsonResponse,
					PDAMInquiryDetailResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}

	public VoucherInquiryResponse VoucherInquiry(VoucherInquiryRequest request) {

		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("Command", request.Command());
		parameters.put("trxid", request.trxid());
		parameters.put("tujuan", request.tujuan());
		parameters.put("resp", request.resp());
		parameters.put("voucherId", request.voucherId());
		System.out.println(parameters);
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			VoucherInquiryResponse inquiryResponse = mapper.treeToValue(jsonResponse, VoucherInquiryResponse.class);
			return inquiryResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}

	}
}
