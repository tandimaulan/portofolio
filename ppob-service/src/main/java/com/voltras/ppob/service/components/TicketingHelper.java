package com.voltras.ppob.service.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.ppob.api.models.ProductCode;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.entities.Office;
import com.voltras.ppob.service.models.SimpleJrxmlParamater;
import com.voltras.ppob.service.repositories.OfficeDataRepository;
import com.voltras.ppob.utils.GenerateUtil;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class TicketingHelper {
	@Autowired
	private VoucherGenerator voucherGenerator;
	private String receiptFooter;
	@Autowired
	private OfficeDataRepository officeRepo;
	@Autowired
	private VanAdditionalRequestData session;
	@Value("${template.backColor}")
	private String backColor;
	@Value("${template.receipt.header}")
	private String receiptHeader;
	@Value("${template.receipt.footer}")
	private final String whiteImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgDTD2qgAAAAASUVORK5CYII=";

	public String generateLogoFromString(String savedLogo) {
		var isFromSavedLogo = savedLogo != null && !savedLogo.isBlank();
		return isFromSavedLogo ? savedLogo : whiteImage;
	}

	@Transactional
	public String printReceipt(BookData data) {
		Map<String, Object> parameters = new HashMap<>();
		Office office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
		parameters.put("header", GenerateUtil.getBase64EncodedImage(receiptHeader));
		parameters.put("logo", office.getLogo() == null || office.getLogo().isBlank() ? whiteImage : office.getLogo());

		String officeName = null;
		String officeAddress = null;
		String officeEmail = null;
		String officeFax = null;
		String officePhone = null;

		if (session.getOffice() != null) {
			officeName = session.getOffice().getName();
			officeAddress = session.getOffice().getAddress();
			officeEmail = session.getOffice().getEmail();
			officeFax = session.getOffice().getFax();
			officePhone = session.getOffice().getPhone();
		}
		parameters.put("officeDatas",
				new JRBeanCollectionDataSource(List.of(new SimpleJrxmlParamater("Office Name", officeName),
						new SimpleJrxmlParamater("Office Address", officeAddress),
						new SimpleJrxmlParamater("Office Email", officeEmail),
						new SimpleJrxmlParamater("Office Fax", officeFax),
						new SimpleJrxmlParamater("Office Phone", officePhone)), false));

		// bookdata params
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		parameters.put("bookingStatus", "SUCCESS");
		parameters.put("bookingCode", data.getBookingCode());
		parameters.put("bookingDate", dateFormat.format(data.getBookDate()));

		// Transaction Details
		parameters.put("transactionDetails",
				new JRBeanCollectionDataSource(getTransactionDetailParam(data, data.getProductType())));
		// Payment Details
		parameters.put("paymentDetails", new JRBeanCollectionDataSource(getPaymentDetailsParam(data)));
		Double totalPayment = data.getFinancialStatement().getPaxPrice();

		parameters.put("grandTotal", GenerateUtil.getIdrPrice(totalPayment));

		parameters.put("footer", GenerateUtil.getBase64EncodedImage(receiptFooter));
		parameters.put("backgroundColor", backColor);
		return voucherGenerator.generate("receipt", parameters);
	}

	public List<SimpleJrxmlParamater> getTransactionDetailParam(BookData data, ProductCode type) {
		List<SimpleJrxmlParamater> response = new ArrayList<>();
		switch (type) {
		case PLN_PREPAID -> {
			response.add(new SimpleJrxmlParamater("Ref Pembayaran", data.getRefNum()));
			response.add(new SimpleJrxmlParamater("Produk", enumToStringProduct(type)));
			response.add(new SimpleJrxmlParamater("Keterangan", data.getProductDetail().getDescription()));
			response.add(new SimpleJrxmlParamater("Nama Pelanggan", data.getCustomerName()));
			response.add(new SimpleJrxmlParamater("Token", data.getToken()));
			response.add(new SimpleJrxmlParamater("Tarif Daya", data.getFareAndPower()));
			response.add(new SimpleJrxmlParamater("Jumlah KWH", data.getTotalKwh()));
			response.add(new SimpleJrxmlParamater("Info", data.getAdditionalInfo()));
		}
		case BPJS_KS -> {
			response.add(new SimpleJrxmlParamater("Ref Pembayaran", data.getRefNum()));
			response.add(new SimpleJrxmlParamater("Produk", enumToStringProduct(type)));
			response.add(new SimpleJrxmlParamater("Deskripsi", data.getProductDetail().getDescription()));
			response.add(new SimpleJrxmlParamater("ID Pelanggan", data.getCustomerProductNumber()));
			response.add(new SimpleJrxmlParamater("Nama Pelanggan", data.getCustomerName()));
			response.add(new SimpleJrxmlParamater("NO VA", data.getVirtualAccountNumber()));
			response.add(new SimpleJrxmlParamater("Periode", data.getTotalMonth() + " Bulan"));
			response.add(new SimpleJrxmlParamater("Info", data.getAdditionalInfo()));

		}
		case PDAM_P -> {
			response.add(new SimpleJrxmlParamater("Ref Pembayaran", data.getRefNum()));
			response.add(new SimpleJrxmlParamater("Produk", enumToStringProduct(type)));
			response.add(new SimpleJrxmlParamater("Penyedia Jasa", data.getProductDetail().getDescription()));
			response.add(new SimpleJrxmlParamater("No Registrasi", data.getCustomerProductNumber()));
			response.add(new SimpleJrxmlParamater("Nama Pelanggan", data.getCustomerName()));
			response.add(new SimpleJrxmlParamater("Periode", data.getPeriod()));
			response.add(new SimpleJrxmlParamater("Info", data.getAdditionalInfo()));

		}
		case PLN_POSTPAID -> {
			response.add(new SimpleJrxmlParamater("Ref Pembayaran", data.getRefNum()));
			response.add(new SimpleJrxmlParamater("Produk", enumToStringProduct(type)));
			response.add(new SimpleJrxmlParamater("Deskripsi", data.getProductDetail().getDescription()));
			response.add(new SimpleJrxmlParamater("ID Pelanggan", data.getCustomerProductNumber()));
			response.add(new SimpleJrxmlParamater("Nama Pelanggan", data.getCustomerName()));
			response.add(new SimpleJrxmlParamater("Periode", data.getPeriod()));
			response.add(new SimpleJrxmlParamater("Info", data.getAdditionalInfo()));
		}
		case PULSA_PRABAYAR, PULSA_PAKETDATA -> {
			response.add(new SimpleJrxmlParamater("Ref Pembayaran", data.getRefNum()));
			response.add(new SimpleJrxmlParamater("Produk", enumToStringProduct(type)));
			response.add(new SimpleJrxmlParamater("Deskripsi", data.getProductDetail().getDescription()));
			response.add(new SimpleJrxmlParamater("Nominal", data.getProductDetail().getNominal().toString()));
			response.add(new SimpleJrxmlParamater("SN", data.getSerialNumber()));
		}
		case PULSA_PREPAID -> {
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + data.getProductType());

		}

		return response;

	}

	public List<SimpleJrxmlParamater> getPaymentDetailsParam(BookData data) {
		List<SimpleJrxmlParamater> response = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		response.add(new SimpleJrxmlParamater("Status Pembayaran", data.getPaymentStatus().toString()));
		response.add(new SimpleJrxmlParamater("Tipe Pembayaran", enumToStringPayment(data.getPaymentType())));
		response.add(new SimpleJrxmlParamater("Tanggal Pembayaran", dateFormat.format(data.getPaymentDate())));

		return response;

	}

	private String enumToStringPayment(PaymentType type) {

		String enumData = switch (type) {
		case CC -> "Credit Card";
		case DEPOSIT -> "Deposit";
		case TRANSFER -> "Bank Transfer";
		case VA -> "VirtualAccount";
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
		return enumData;
	}

	private String enumToStringProduct(ProductCode type) {

		String enumData = switch (type) {
		case BPJS_KS -> "BPJS-KS";
		case MULTIFINANCE -> "MULTIFINANCE";
		case PDAM_P -> "PDAM-P";
		case PLN_POSTPAID -> "PLN-POSTPAID";
		case PLN_PREPAID -> "PLN-PREPAID";
		case PULSA_PAKETDATA -> "PULSA-PAKETDATA";
		case PULSA_PRABAYAR -> "PULSA-PRABAYAR";
		case PULSA_PREPAID -> "PULSA-PREPAID";
		case SAMOLNAS -> "SAMOLNAS";
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
		return enumData;
	}

}