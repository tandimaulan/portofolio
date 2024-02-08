package com.voltras.ppob.gateway.api.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltras.ppob.gateway.api.requests.GatewayPaymentRequest;
import com.voltras.ppob.gateway.api.requests.voucher.VoucherPaymentRequest;
import com.voltras.ppob.gateway.api.responses.bpjs.BPJSPaymentResponse;
import com.voltras.ppob.gateway.api.responses.pdam.PDAMPaymentDetailResponse;
import com.voltras.ppob.gateway.api.responses.pdam.PDAMPaymentResponse;
import com.voltras.ppob.gateway.api.responses.plnpostpaid.PLNPostpaidPaymentResponse;
import com.voltras.ppob.gateway.api.responses.plnprepaid.PLNPrepaidPaymentResponse;
import com.voltras.ppob.gateway.api.responses.voucher.VoucherPaymentResponse;

@Service
public class GatewayPaymentService {
	@Autowired
	private PpobSender sender;

	public PLNPrepaidPaymentResponse paymentPLNPrepaid(GatewayPaymentRequest request)
			throws JsonProcessingException, IllegalArgumentException {
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
		parameters.put("refnum", request.refnum());
		try {
			PLNPrepaidPaymentResponse jsonResponse = sender.sendGet(parameters, PLNPrepaidPaymentResponse.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}
	}

	public PLNPostpaidPaymentResponse paymentPLNPostpaid(GatewayPaymentRequest request)
			throws JsonProcessingException, IllegalArgumentException {
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
		parameters.put("refnum", request.refnum());
		try {
			PLNPostpaidPaymentResponse jsonResponse = sender.sendGet(parameters, PLNPostpaidPaymentResponse.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}
	}

	public BPJSPaymentResponse paymentBPJS(GatewayPaymentRequest request)
			throws JsonProcessingException, IllegalArgumentException {
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
		parameters.put("amount", request.amount());
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			BPJSPaymentResponse paymentResponse = mapper.treeToValue(jsonResponse, BPJSPaymentResponse.class);
			return paymentResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}
	}

	// if detail for inquiry false
	public PDAMPaymentResponse paymentPDAM(GatewayPaymentRequest request)
			throws JsonProcessingException, IllegalArgumentException {
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
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			PDAMPaymentResponse paymentResponse = mapper.treeToValue(jsonResponse, PDAMPaymentResponse.class);
			return paymentResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}
	}

	// if detail for inquiry true
	public PDAMPaymentDetailResponse paymentDetailPDAM(GatewayPaymentRequest request)
			throws JsonProcessingException, IllegalArgumentException {
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
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			PDAMPaymentDetailResponse paymentResponse = mapper.treeToValue(jsonResponse,
					PDAMPaymentDetailResponse.class);
			return paymentResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}
	}

	public VoucherPaymentResponse paymentVoucher(VoucherPaymentRequest request)
			throws JsonProcessingException, IllegalArgumentException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("cid", request.cid());
		parameters.put("dt", request.dt());
		parameters.put("hc", request.hc());
		parameters.put("modul", request.modul());
		parameters.put("command", request.command());
		parameters.put("trxid", request.trxid());
		parameters.put("tujuan", request.tujuan());
		parameters.put("resp", request.resp());
		parameters.put("voucherId", request.voucherId());
		try {
			JsonNode jsonResponse = sender.sendGet(parameters, JsonNode.class);
			ObjectMapper mapper = new ObjectMapper();
			VoucherPaymentResponse paymentResponse = mapper.treeToValue(jsonResponse, VoucherPaymentResponse.class);
			return paymentResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing the response JSON", e);
		}
	}

}
