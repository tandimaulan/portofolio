package com.voltras.blockseatservice.components;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.voltras.blockseatservice.entities.BookData;
import com.voltras.blockseatservice.models.EmailMimeType;
import com.voltras.blockseatservice.repositories.OfficeRepository;
import com.voltras.blockseatservice.services.EmailService;
import com.voltras.blockseatservice.services.SystemParameterService;
import com.voltras.payment.common.api.enums.PaymentStatus;

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
	private OfficeRepository officeRepo;

	@Value("${scheduler.statement.waitingminutes}")
	private Integer paymentWaitingTime;

	private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

	public void sendFailIssued(BookData data) {
		sendFailIssuedToCustomer(data);
		sendFailIssuedToHelpdesk(data);
	}

	public void sendReserveNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var logo = String.format("data:image/png;base64, %s",
					ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo()));
			var customerName = data.getContactData().getCustomerName();
			var timelimit = data.getTimelimit()
					.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")));
			var productName = "";
			var departure = data.getFlightSummary().departure();
			var airportName = departure.name();
			var airportIata = departure.iata();
			var airportCity = departure.city();
			var airportCountry = departure.country();
			var time = departure.time().toString();
			var officeName = office.getName();
			String totalPayment = null;

			var outstanding = numberFormat.format(data.getOutstanding()).replace("Rp", "IDR ");
			var totalPayments = numberFormat.format(data.getStatement().stream()
					.filter(status -> status.getPaymentStatus().equals(PaymentStatus.SUCCESS))
					.mapToDouble(total -> total.getTotalPayment()).sum());
			var statementData = data.getStatement();
			String paymentType = null;
			for (var statementDatas : statementData) {
				if (statementDatas.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
					paymentType = statementDatas.getPaymentType().toString();
					totalPayment = numberFormat.format(statementDatas.getTotalPayment());

				}
			}

			Map<String, String> parameters = Map.ofEntries(Map.entry("productName", productName),
					Map.entry("blockseatName", blockseatName), Map.entry("bookingCode", bookingCode),
					Map.entry("customerName", customerName), Map.entry("timelimit", timelimit),
					Map.entry("airportName", airportName), Map.entry("airportIata", airportIata),
					Map.entry("airportCity", airportCity), Map.entry("airportCountry", airportCountry),
					Map.entry("time", time), Map.entry("logo", logo), Map.entry("officeName", officeName),
					Map.entry("totalPayment", totalPayment), Map.entry("totalPayments", totalPayments),
					Map.entry("outstanding", outstanding), Map.entry("paymentType", paymentType));
			String templateName = "reserved.notif";

			var receipt = ticketingHelper.printReceipt(data, office);
			Map<String, String> attachments = new HashMap<>();
			attachments.put(String.format("Receipt %s.pdf", data.getBookingCode()), receipt);

			emailService.send(List.of(data.getContactData().getCustomerEmail()), null, null, templateName, parameters,
					attachments, EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendReserveNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getCustomerEmail(), e);
		}
	}

	public void sendReserveNotificationToHelpdesk(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var logo = String.format("data:image/png;base64, %s",
					ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo()));
			var customerName = data.getContactData().getCustomerName();
			var timelimit = data.getTimelimit()
					.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")));
			var productName = "";
			var departure = data.getFlightSummary().departure();
			var airportName = departure.name();
			var airportIata = departure.iata();
			var airportCity = departure.city();
			var airportCountry = departure.country();
			var time = departure.time().toString();
			var officeName = office.getName();
			String totalPayment = null;

			var outstanding = numberFormat.format(data.getOutstanding()).replace("Rp", "IDR ");
			var totalPayments = numberFormat.format(data.getStatement().stream()
					.filter(status -> status.getPaymentStatus().equals(PaymentStatus.SUCCESS))
					.mapToDouble(total -> total.getTotalPayment()).sum());
			var statementData = data.getStatement();
			String paymentType = null;
			for (var statementDatas : statementData) {
				if (statementDatas.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
					paymentType = statementDatas.getPaymentType().toString();
					totalPayment = numberFormat.format(statementDatas.getTotalPayment());

				}
			}

			Map<String, String> parameters = Map.ofEntries(Map.entry("productName", productName),
					Map.entry("blockseatName", blockseatName), Map.entry("bookingCode", bookingCode),
					Map.entry("customerName", customerName), Map.entry("timelimit", timelimit),
					Map.entry("airportName", airportName), Map.entry("airportIata", airportIata),
					Map.entry("airportCity", airportCity), Map.entry("airportCountry", airportCountry),
					Map.entry("time", time), Map.entry("logo", logo), Map.entry("officeName", officeName),
					Map.entry("totalPayment", totalPayment), Map.entry("totalPayments", totalPayments),
					Map.entry("outstanding", outstanding), Map.entry("paymentType", paymentType));
			var emailTarget = systemParameter.get("email.hd");
			String templateName = "reserved.notif.hd";

			var receipt = ticketingHelper.printReceipt(data, office);
			Map<String, String> attachments = new HashMap<>();
			attachments.put(String.format("Receipt %s.pdf", data.getBookingCode()), receipt);

			emailService.send(List.of(emailTarget), null, null, templateName, parameters, attachments,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendReserveNotificationToHelpdesk] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), systemParameter.get("email.hd"), e);
		}
	}

	public void sendNewNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var logo = String.format("data:image/png;base64, %s",
					ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo()));
			var customerName = String.format("%s", data.getContactData().getCustomerName());
			var timelimit = data.getTimelimit()
					.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")));
			var flightSummary = data.getFlightSummary().toString();
			var productName = "";
			var departure = data.getFlightSummary().departure();
			var airportName = departure.name();
			var airportIata = departure.iata();
			var airportCity = departure.city();
			var airportCountry = departure.country();
			var time = departure.time().toString();
			var officeName = office.getName();
			Map<String, String> parameters = Map.ofEntries(Map.entry("productName", productName),
					Map.entry("blockseatName", blockseatName), Map.entry("bookingCode", bookingCode),
					Map.entry("logo", logo), Map.entry("customerName", customerName), Map.entry("timelimit", timelimit),
					Map.entry("flightSummary", flightSummary), Map.entry("officeName", officeName),
					Map.entry("airportName", airportName), Map.entry("airportIata", airportIata),
					Map.entry("airportCity", airportCity), Map.entry("airportCountry", airportCountry),
					Map.entry("time", time));
			String templateName = "new.notif";

			emailService.send(List.of(data.getContactData().getCustomerEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendNewNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getCustomerEmail(), e);
		}
	}

	public void sendSuccessNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var logo = String.format("data:image/png;base64, %s",
					ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo()));
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var customerName = String.format("%s", data.getContactData().getCustomerName());
			var timelimit = data.getTimelimit()
					.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")));
			var flightSummary = data.getFlightSummary().toString();
			var departure = data.getFlightSummary().departure();
			var airportName = departure.name();
			var airportIata = departure.iata();
			var airportCity = departure.city();
			var airportCountry = departure.country();
			var time = departure.time().toString();
			var officeName = office.getName();
			Map<String, String> parameters = Map.ofEntries(Map.entry("blockseatName", blockseatName),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("timelimit", timelimit), Map.entry("flightSummary", flightSummary),
					Map.entry("officeName", officeName), Map.entry("airportName", airportName),
					Map.entry("airportIata", airportIata), Map.entry("airportCity", airportCity),
					Map.entry("airportCountry", airportCountry), Map.entry("time", time), Map.entry("logo", logo));
			String templateName = "success.notif";

			var receipt = ticketingHelper.printReceipt(data, office);
			Map<String, String> attachments = new HashMap<>();
			attachments.put(String.format("Receipt %s.pdf", data.getBookingCode()), receipt);

			emailService.send(List.of(data.getContactData().getCustomerEmail()), null, null, templateName, parameters,
					attachments, EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendSuccessNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getCustomerEmail(), e);
		}
	}

	public void sendWaitingPaymentNotification(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var customerName = String.format("%s", data.getContactData().getCustomerName());
			var departure = data.getFlightSummary().departure();
			var airportName = departure.name();
			var airportIata = departure.iata();
			var airportCity = departure.city();
			var airportCountry = departure.country();
			var departTime = departure.time().toString();
			var officeName = office.getName();
			var timelimitPayment = data.getStatement().stream()
					.filter(status -> status.getPaymentStatus().equals(PaymentStatus.WAITING))
					.map(timelimits -> timelimits.getCreatedAt().plusMinutes(paymentWaitingTime)
							.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID"))))
					.toString();
			var price = data.getStatement().stream().map(tp -> tp.getTotalPayment()).toString();

			Map<String, String> parameters = Map.ofEntries(Map.entry("blockseatName", blockseatName),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("timelimitPayment", timelimitPayment), Map.entry("officeName", officeName),
					Map.entry("airportName", airportName), Map.entry("airportIata", airportIata),
					Map.entry("airportCity", airportCity), Map.entry("airportCountry", airportCountry),
					Map.entry("time", departTime), Map.entry("price", price));
			String templateName = "waiting.payment.notif";

			emailService.send(List.of(data.getContactData().getCustomerEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendSuccessNotification] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getCustomerEmail(), e);
		}
	}

	public void sendFailIssuedToCustomer(BookData data) {
		try {
			var emailTarget = systemParameter.get("email.hd");
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var customerName = String.format("%s", data.getContactData().getCustomerName());
			var officeName = office.getName();
			var officeCode = office.getCode();
			var statementData = data.getStatement();

			String price = null;
			String paymentType = null;
			String totalPayment = null;
			String paymentDate = null;
			String paymentChannel = null;
			String orderId = null;
			for (var statementDatas : statementData) {
				if (statementDatas.getPaymentStatus().equals(PaymentStatus.FAIL)) {
					price = String.format("%s", statementDatas.getTotalPayment());
					paymentType = String.format("%s", statementDatas.getPaymentType());
					totalPayment = String.format("%s", statementDatas.getTotalPayment());
					paymentDate = String.format("%s", statementDatas.getCreatedAt());
					paymentChannel = String.format("%s", statementDatas.getPaymentChannel());
					orderId = String.format("%s", statementDatas.getOrderId());
				}
			}
			var logo = ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo());

			Map<String, String> parameters = Map.ofEntries(Map.entry("blockseatName", blockseatName),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("officeName", officeName), Map.entry("paymentType", paymentType),
					Map.entry("totalPayment", totalPayment), Map.entry("paymentDate", paymentDate),
					Map.entry("price", price), Map.entry("logo", logo), Map.entry("orderId", orderId.toString()),
					Map.entry("paymentChannel", paymentChannel), Map.entry("officeCode", officeCode));
			String templateName = "transaction.failed.customer";

			emailService.send(List.of(emailTarget), null, null, templateName, parameters, EmailMimeType.HTML,
					systemParameter.get("email.travelagent"));
		} catch (Exception e) {
			logger.error(
					"[{}.sendFailIssuedToCustomer] gagal memberikan notifikasi ke customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getCustomerEmail(), e);
		}
	}

	private void sendFailIssuedToHelpdesk(BookData data) {
		try {
			var emailTarget = systemParameter.get("email.hd");
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var customerName = String.format("%s", data.getContactData().getCustomerName());
			var officeName = office.getName();
			var officeCode = office.getCode();
			var statementData = data.getStatement();

			String price = null;
			String paymentType = null;
			String totalPayment = null;
			String paymentDate = null;
			String paymentChannel = null;
			String orderId = null;
			for (var statementDatas : statementData) {
				if (statementDatas.getPaymentStatus().equals(PaymentStatus.FAIL)) {
					price = String.format("%s", statementDatas.getTotalPayment());
					paymentType = String.format("%s", statementDatas.getPaymentType());
					totalPayment = String.format("%s", statementDatas.getTotalPayment());
					paymentDate = String.format("%s", statementDatas.getCreatedAt());
					paymentChannel = String.format("%s", statementDatas.getPaymentChannel());
					orderId = String.format("%s", statementDatas.getOrderId());
				}
			}
			var logo = ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo());

			Map<String, String> parameters = Map.ofEntries(Map.entry("blockseatName", blockseatName),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("officeName", officeName), Map.entry("paymentType", paymentType),
					Map.entry("totalPayment", totalPayment), Map.entry("paymentDate", paymentDate),
					Map.entry("price", price), Map.entry("logo", logo), Map.entry("orderId", orderId),
					Map.entry("paymentChannel", paymentChannel), Map.entry("officeCode", officeCode));
			String templateName = "transaction.failed.hd";

			emailService.send(List.of(emailTarget), null, null, templateName, parameters, EmailMimeType.HTML,
					systemParameter.get("email.travelagent"));

		} catch (Exception e) {
			logger.error(
					"[{}.sendFailIssuedToHelpdesk] gagal memberikan notifikasi ke helpdesk dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), systemParameter.get("email.hd"), e);
		}
	}

	public void sendCancellIssuedToCustomer(BookData data) {
		try {
			var office = officeRepo.findByCode(data.getOfficeCode()).orElse(null);
			var blockseatName = data.getBlockseatName();
			var bookingCode = data.getBookingCode();
			var customerName = String.format("%s", data.getContactData().getCustomerName());
			var officeName = office.getName();
			var officeCode = office.getCode();
			var bookingDate = String.format("%s", data.getBookDate()
					.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID"))));
			var bookingStatus = String.format("%s", data.getStatus());

			String price = String.format("%s", data.getTotalPrice());
			var logo = ticketingHelper.generateLogoFromString(office == null ? null : office.getLogo());

			Map<String, String> parameters = Map.ofEntries(Map.entry("blockseatName", blockseatName),
					Map.entry("bookingCode", bookingCode), Map.entry("customerName", customerName),
					Map.entry("officeName", officeName), Map.entry("price", price), Map.entry("logo", logo),
					Map.entry("officeCode", officeCode), Map.entry("bookingDate", bookingDate),
					Map.entry("bookingStatus", bookingStatus));
			String templateName = "canceled.notif";

			emailService.send(List.of(data.getContactData().getCustomerEmail()), null, null, templateName, parameters,
					EmailMimeType.HTML, systemParameter.get("email.travelagent"));

		} catch (Exception e) {
			logger.error(
					"[{}.sendCancellIssuedToCustomer] gagal memberikan notifikasi ke Customer dengan email {} dengan error: {}",
					this.getClass().getCanonicalName(), data.getContactData().getCustomerEmail(), e);
		}
	}
}
