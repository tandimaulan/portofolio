package com.voltras.ppob.gateway.api.responses.plnprepaid;

public record PLNPrepaidInquiryResponse(String status, String rc, String rcm, String text,
		String refnum, String msn, String subid, String nama, String tarif, String admin, Integer unsold,
		String denomunsold, String distcode, String sunit, String suphone, String maxkwh, String trxid) {
}

/*
	PARAM
	status					= status Response (SUCCESS / ERRoR)
	rc						= Response Code
	rcm 					= Response Message
	text					= Response Text untuk format response SMS
	refnum					= Nomor Referensi Switching
	msn						= Nomor Mesin
	subid					= ID Pelanggan PLN
	nama					= Nama Pelanggan
	tarif					= Jenis tarif daya pelanggan
	admin					= Nilai Admin
	unsold (optional)		= Jumlah token unsold
	denumunsold (optional) 	= List denom Unsold
	distcode				= Kode distribusi (kode wilayah/daerah)
	sunit					= PLN service unit
	suphone					= Nomor kontak PLN service unit
	maxkwh					= Max kwh - daya pelanggan
	trxid					= transaksi Id Mitra
*/