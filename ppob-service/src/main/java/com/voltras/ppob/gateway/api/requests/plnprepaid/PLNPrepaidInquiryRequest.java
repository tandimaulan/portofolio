package com.voltras.ppob.gateway.api.requests.plnprepaid;

public record PLNPrepaidInquiryRequest(String cid, String dt, String hc, String modul,
		String command, String msn, String nominal, String resp, String trxid) {

}
/*
 	PARAM
	cid 				= Client ID
	dt					= Tangggal Request
	hc					= SHA256(cid+dt+secretKey)
	modul				= nama alias modul/produk yang diakses (PRE)
	command				= Type Request (INQ)
	msn					= Nomor Mesin/ID Pelanggan
	nominal (optional)	= Nilai Denom prepaid
	resp				= Type Respon (JSON / XML)
	trxId				= Transaksi ID Mitra
*/