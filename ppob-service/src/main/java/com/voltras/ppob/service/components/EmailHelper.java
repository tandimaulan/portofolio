package com.voltras.ppob.service.components;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.voltras.ppob.api.models.ProductCode;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.models.EmailMimeType;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.ppob.service.repositories.OfficeRepository;
import com.voltras.ppob.service.services.EmailService;
import com.voltras.ppob.service.services.SystemParameterService;
import com.voltras.ppob.utils.GenerateUtil;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

@Component
public class EmailHelper {
	@Autowired
	private LogHelper logger;
	@Autowired
	TicketingHelper ticketingHelper;
	@Autowired
	private SystemParameterService systemParameter;
	@Autowired
	private EmailService emailService;
	@Autowired
	private VanAdditionalRequestData session;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private OfficeRepository officeRepo;

	@Value("${scheduler.statement.waitingminutes}")
	private Integer paymentWaitingTime;

//	private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

	public void sendFailIssued(BookData data) {
		sendFailIssuedToCustomer(data);
		sendFailIssuedToHelpdesk(data);
	}

	public void sendReserveNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);

			var bookingCode = data.getBookingCode();
			var productType = data.getProductType();
			var customerName = String.format("%s", data.getContactData().getName());
			SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd yyyy HH:mm:ss z",
					Locale.forLanguageTag("id-ID"));

			var timelimit = format.format(data.getTimelimit());

			var customerProductNumber = data.getCustomerProductNumber();
			var totalPrice = data.getTotalPrice();
			System.out.println(enumToString(productType));
			var officeName = office.getName();
			Map<String, String> parameters = Map.ofEntries(Map.entry("productType", enumToString(productType)),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("timelimit", timelimit), Map.entry("customerProductNumber", customerProductNumber),
					Map.entry("officeName", officeName), Map.entry("totalPrice", GenerateUtil.getIdrPrice(totalPrice))

			);
			String templateName = "reserve.notif";

			emailService.send(List.of(data.getContactData().getEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendNewNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getEmail(), e);
		}
	}

	public void sendSuccessNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);

			var bookingCode = data.getBookingCode();
			var productType = data.getProductType();
			var refNum = data.getRefNum();
			var customerProductNumber = data.getCustomerProductNumber();
			var totalPrice = data.getTotalPrice();

			var officeName = office.getName();
			Map<String, String> parameters = new HashMap<String, String>();
			parameters = Map.ofEntries(Map.entry("bookingCode", bookingCode),
					Map.entry("productType", enumToString(productType)),
					Map.entry("customerProductNumber", customerProductNumber), Map.entry("refNum", refNum),
					Map.entry("totalPrice", GenerateUtil.getIdrPrice(totalPrice)), Map.entry("officeName", officeName)

			);
			String templateName = "transaction.success";

			emailService.send(List.of(data.getContactData().getEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendSuccessNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getEmail(), e);
		}
	}

//
	public void sendWaitingPaymentNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);

			var bookingCode = data.getBookingCode();
			var productType = data.getProductType();
			var customerName = String.format("%s", data.getContactData().getName());
			var timelimitPayment = DateUtils.addMinutes(data.getCreatedAt(), +paymentWaitingTime);

			var refNum = data.getRefNum();
			var customerProductNumber = data.getCustomerProductNumber();
			var totalPrice = data.getTotalPrice();
			var officeName = office.getName();
			Map<String, String> parameters = Map.ofEntries(Map.entry("productType", enumToString(productType)),
					Map.entry("bookingCode", bookingCode), Map.entry("refNum", refNum),
					Map.entry("customerName", customerName),
					Map.entry("timelimitPayment",
							timelimitPayment.toString().formatted(
									DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")))),
					Map.entry("customerProductNumber", customerProductNumber), Map.entry("officeName", officeName),
					Map.entry("totalPrice", GenerateUtil.getIdrPrice(totalPrice))

			);
			String templateName = "waiting.payment";

			emailService.send(List.of(data.getContactData().getEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendSuccessNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getEmail(), e);
		}
	}

//
	public void sendFailIssuedToCustomer(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);

			var bookingCode = data.getBookingCode();
			var productType = data.getProductType();
			var customerName = String.format("%s", data.getContactData().getName());
			var paymentDate = data.getPaymentDate().toString()
					.formatted(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")));

			var customerProductNumber = data.getCustomerProductNumber();

			var officeName = office.getName();
			Map<String, String> parameters = Map.ofEntries(Map.entry("bookingCode", bookingCode),
					Map.entry("productType", enumToString(productType)),
					Map.entry("customerProductNumber", customerProductNumber), Map.entry("paymentDate", paymentDate),
					Map.entry("paymentType", data.getPaymentType().toString()),
					Map.entry("totalPayment", GenerateUtil.getIdrPrice(data.getTotalPayment())),
					Map.entry("customerName", customerName),
					Map.entry("customerPhone", String.format("%s", data.getContactData().getPhone())),
					Map.entry("officeName", officeName)

			);
			String templateName = "transaction.failed.customer";

			emailService.send(List.of(data.getContactData().getEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendSuccessNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getEmail(), e);
		}
	}

	public void sendFailIssuedToHelpdesk(BookData data) {
		try {
			var bookingCode = data.getBookingCode();
			var productType = data.getProductType();
			var customerName = String.format("%s", data.getContactData().getName());
			var refNum = data.getRefNum();
			var customerProductNumber = data.getCustomerProductNumber();
			var paymentDate = data.getPaymentDate().toString()
					.formatted(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")));

			Map<String, String> parameters = Map.ofEntries(Map.entry("bookingCode", bookingCode),
					Map.entry("productType", enumToString(productType)),
					Map.entry("customerProductNumber", customerProductNumber),
					Map.entry("officeName", data.getOfficeCode()), Map.entry("customerName", customerName),
					Map.entry("totalPayment", GenerateUtil.getIdrPrice(data.getTotalPayment())),
					Map.entry("paymentDate", paymentDate), Map.entry("paymentType", data.getPaymentType().toString()),
					Map.entry("refNum", refNum)

			);
			String templateName = "transaction.failed.hd";

			emailService.send(List.of(data.getContactData().getEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendSuccessNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getEmail(), e);
		}
	}

	public void sendCancellIssuedToCustomer(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var voucherName = data.getProductDetail().getVoucherName();
			var bookingCode = data.getBookingCode();
			var customerName = String.format("%s", data.getContactData().getName());
			var officeName = office.getName();
			var officeCode = office.getCode();
			var bookingDate = String.format("%s", data.getBookDate().toString()
					.formatted(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID"))));
			var bookingStatus = String.format("%s", data.getStatus());

			String price = String.format("%s", data.getTotalPrice());
			var logo = ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo());

			Map<String, String> parameters = Map.ofEntries(Map.entry("voucherName", voucherName),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("officeName", officeName), Map.entry("price", price), Map.entry("logo", logo),
					Map.entry("officeCode", officeCode), Map.entry("bookingDate", bookingDate),
					Map.entry("bookingStatus", bookingStatus));
			String templateName = "canceled.notif";

			emailService.send(List.of(data.getContactData().getEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));

		} catch (Exception e) {
			logger.error(
					"[{}.sendCancellIssuedToCustomer] gagal memberikan notifikasi ke Customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getEmail(), e);
		}
	}

	public void sendReceipt(String bookingCode, String document, String email) {
		var bookData = bookDataRepo.findByOfficeCodeAndBookingCode(session.getOffice().getCode(), bookingCode).get();
		try {
			var target = bookData.getContactData().getEmail();
			var officeName = bookData.getOfficeName();
			var sender = systemParameter.get("email.travelagent");

			var parameters = Map.ofEntries(Map.entry("bookingCode", bookingCode), Map.entry("officeName", officeName));
			logger.info("[{}.sendReceipt] SEND EMAIL TO RAVEN", this.getClass().getCanonicalName());
			var fileName = "Receipt " + bookingCode + ".pdf";
			var templateName = "send.receipt";
			if (email.isBlank()) {
				emailService.send(List.of(target), null, null, templateName, parameters, fileName, document,
						EmailMimeType.TEXT, sender);
			}
			emailService.send(List.of(email), null, null, templateName, parameters, fileName, document,
					EmailMimeType.TEXT, sender);
		} catch (Exception e) {
			logger.error("[{}.sendReceipt] gagal memberikan kuitansi ke Customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), bookData.getContactData().getEmail(), e);
		}
	}

	private String enumToString(ProductCode type) {

		String enumData = switch (type) {
		case PULSA_PRABAYAR -> "PULSA-PRABAYAR";
		case PULSA_PAKETDATA -> "PULSA-PAKETDATA";
		case PLN_PREPAID -> "PLN-PREPAID";
		case PLN_POSTPAID -> "PLN-POSTPAID";
		case PDAM_P -> "PDAM-P";
		case BPJS_KS -> "BPJS-KS";
		case SAMOLNAS -> "SAMOLNAS";
		case MULTIFINANCE -> "MULTIFINANCE";
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
		return enumData;
	}
}
