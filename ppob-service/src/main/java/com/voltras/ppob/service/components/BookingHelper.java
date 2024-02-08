package com.voltras.ppob.service.components;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.enums.ContactType;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.common.api.exceptions.GatewayTimeoutException;
import com.voltras.ppob.api.exceptions.TransactionPauseException;
import com.voltras.ppob.api.models.ProductCode;
import com.voltras.ppob.api.models.availability.ProductDetail;
import com.voltras.ppob.api.models.book.BookResponse;
import com.voltras.ppob.gateway.api.requests.GatewayPaymentRequest;
import com.voltras.ppob.gateway.api.requests.bpjs.BPJSInquiryRequest;
import com.voltras.ppob.gateway.api.requests.pdam.PDAMInquiryRequest;
import com.voltras.ppob.gateway.api.requests.plnpostpaid.PLNPostpaidInquiryRequest;
import com.voltras.ppob.gateway.api.requests.plnprepaid.PLNPrepaidInquiryRequest;
import com.voltras.ppob.gateway.api.requests.voucher.VoucherInquiryRequest;
import com.voltras.ppob.gateway.api.requests.voucher.VoucherPaymentRequest;
import com.voltras.ppob.gateway.api.responses.bpjs.BPJSInquiryResponse;
import com.voltras.ppob.gateway.api.responses.pdam.PDAMInquiryResponse;
import com.voltras.ppob.gateway.api.responses.plnpostpaid.PLNPostpaidInquiryResponse;
import com.voltras.ppob.gateway.api.responses.plnprepaid.PLNPreInquiryResponse;
import com.voltras.ppob.gateway.api.responses.voucher.VoucherInquiryResponse;
import com.voltras.ppob.gateway.api.services.GatewayInquiryService;
import com.voltras.ppob.gateway.api.services.GatewayPaymentService;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.entities.ContactData;
import com.voltras.ppob.service.entities.FareData;
import com.voltras.ppob.service.entities.MarkupValue;
import com.voltras.ppob.service.entities.Office;
import com.voltras.ppob.service.entities.ReversalData;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.ppob.service.repositories.MarkupValueRepository;
import com.voltras.ppob.service.repositories.OfficeRepository;
import com.voltras.ppob.service.repositories.ProductRepository;
import com.voltras.ppob.service.repositories.ReversalDataRepository;
import com.voltras.ppob.service.services.DomainService;
import com.voltras.ppob.service.services.FinancialStatementService;
import com.voltras.ppob.service.services.SystemParameterService;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

import jakarta.transaction.Transactional;

@Component
public class BookingHelper {
	@Autowired
	private ClientReferenceGenerator generator;
	@Autowired
	private VanAdditionalRequestData session;

	@Autowired
	private GatewayInquiryService inquiryService;
	@Autowired
	private GatewayPaymentService gatewayPayment;
//	@Autowired
//	private GatewayAdviceService gatewayAdvice;
	@Autowired
	private FinancialStatementService financialService;
	@Autowired
	private PriceEvaluator priceEvaluator;
	@Autowired
	private DomainService domain;
	@Autowired
	LogHelper log;

	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private OfficeRepository officeRepo;
	@Autowired
	private MarkupValueRepository markupRepo;
	@Autowired
	private ReversalDataRepository reversalRepo;
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private SystemParameterService systemParameter;

	@Value("${ppob.gateway.cid}")
	private String cid;
	@Value("${ppob.gateway.secretkey}")
	private String secretkey;

	private final String className = this.getClass().getCanonicalName();

	public BookResponse inquiry(ProductDetail productDetail, String number, Boolean detail)
			throws GatewayTimeoutException, TransactionPauseException {
		var office = session.getOffice();
		if (!domain.getIsB2c(session.getDomain())) {
			var officeDataOpt = officeRepo.findByCode(office.getCode());
			if (officeDataOpt.isEmpty()) {
				officeRepo.saveAndFlush(new Office(office.getAddress(), office.getCode(), office.getEmail(),
						office.getFax(),
						office.getLogo() == null ? null
								: java.util.Base64.getEncoder().encodeToString(office.getLogo()),
						office.getName(), office.getPackageName(), office.getPhone(),
						session.getUser().getPrincipal()));
			}
		}

		var getData = bookDataRepo.findByProductCodeAndCustomerNumberAndOffice(productDetail.code(), number,
				office.getCode());

		if (getData.isPresent()) {

			var data = getData.get();

			if (!data.getCustomerProductNumber().equals(number) && LocalDateTime.now().isBefore(
					data.getBookDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(5))) {
				throw new TransactionPauseException("Proses Transaksi Terjeda. Harap tunggu selama 5-15 menit.");
			}
			if (data.getProductCode().equalsIgnoreCase(productDetail.code())
					&& data.getCustomerProductNumber().equalsIgnoreCase(number)
					&& data.getStatus().equals(BookingStatus.CONFIRMED)
					&& LocalDateTime.now().isBefore(data.getBookDate().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime().plusMinutes(15))) {
				throw new TransactionPauseException("Proses Transaksi Terjeda. Harap tunggu selama 5-15 menit.");
			}
		}
		BookData bookData = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dt = formatter.format(new Date());
		String hashcode = generator.SHA256Generator(cid, dt, secretkey);
		String trxId = UUID.randomUUID().toString();
		String bookingCode = generator.generateClientReference(number);
		LocalDateTime currentDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
				.plusMinutes(15);
		Date timelimit = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());

		var productData = productRepo.findByCode(productDetail.code());

		com.voltras.ppob.service.entities.ProductDetail detailProduct = new com.voltras.ppob.service.entities.ProductDetail(
				UUID.randomUUID(), productData.getCode(), productData.getType(), productData.getNominal(),
				productData.getVoucherName(), productData.getCodeDescription(), productData.getAdminCommission());
		ContactData contactData = new ContactData(session.getUser().getName(), session.getUser().getEmail(),
				session.getUser().getPhone(), ContactType.AGENT);

		BookResponse bookRespone = null;
		Double totalPrice = 0D;
		Double commission = 0D;
		Double nta = 0D;
		Double ntsa = 0D;
		Integer totalMonth = 0;

		switch (productDetail.productCode()) {
		case PLN_PREPAID -> {
			PLNPreInquiryResponse response = inquiryService.PLNPREInquiry(new PLNPrepaidInquiryRequest(cid, dt,
					hashcode, "PRE", "INQ", number, productDetail.nominal().toString(), "JSON", trxId));
			if (response.getStatus().equals("ERROR")) {
				log.error("[{}.inquiryPLN-PREPAID] Error: {}" + response.getRc(), className, response.getRcm());
				throw new GatewayTimeoutException(response.getRcm());
			} else {
				bookData = new BookData(UUID.randomUUID(), generator.generateClientReference(response.getMsn()),
						new Date(), BookingStatus.RESERVED, response.getSubid(), timelimit, session.getDomain(),
						office.getCode(), office.getName(), office.getPackageName(), ProductCode.PLN_PREPAID,
						response.getTrxid(), false, detailProduct);

				bookData.setBookedBy(
						session.getUser() == null ? bookData.getBookedBy() : session.getUser().getPrincipal());
				bookData.setRefNum(response.getRefnum());
				bookData.setProductCode(detailProduct.getCode());
				bookData.setCustomerName(response.getNama());
				bookData.setContactData(contactData);
				bookData.setMsn(response.getMsn());

				var expression = office.getMapCommissionExpression().get("PLN-PREPAID");
				nta = productDetail.nominal() + response.getAdmin();
				Double markup = getMarkupValue("PLN-PREPAID", office.getPackageName(),
						systemParameter.get("generalproduct.pln.prepaid.adm.van"));
				Double total = nta + markup;

				setProductPrice(bookData, nta, expression, total, detailProduct.getAdminCommission());
				bookData.setFinancialStatement(financialService.fromTotal(bookData, nta, total, expression, markup));
				totalPrice = Math.ceil(bookData.getFinancialStatement().getPaxPrice());
				bookData.setTotalPrice(totalPrice);

				var financialStatement = bookData.getFinancialStatement();
				bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.PLN_PREPAID, number,
						response.getTarif(), productDetail.nominal(), productDetail.nominal(), response.getAdmin(), 0D,
						totalPrice, financialStatement.getChannelDiscount(), ntsa));
				bookDataRepo.saveAndFlush(bookData);

				var fareData = bookData.getFareData();
				bookRespone = new BookResponse(bookData.getBookId(), bookData.getBookingCode(), bookData.getStatus(),
						bookData.getProductType(), bookData.getBookDate(), bookData.getTimelimit(),
						bookData.getCustomerName(), bookData.getCustomerProductNumber(), "-", bookData.getRefNum(),
						fareData.getPower(), fareData.getNominal(), fareData.getPrice(), fareData.getAdminFee(),
						fareData.getTotalPrice(), fareData.getPenaltie(), fareData.getChannelDiscount(),
						bookData.getNtsa(), bookData.getTotalMonth(), bookData.getPeriod());
			}
		}

		case PLN_POSTPAID -> {

			PLNPostpaidInquiryResponse response = inquiryService.PLNPostpaidInquiry(
					new PLNPostpaidInquiryRequest(cid, dt, hashcode, "PLN", "INQ", trxId, number, "JSON", "false"));

			if (response.status().equals("ERROR") || response.rc().equals("0088")) {
				log.error("[{}.inquiryPLN-POSTPAID] Error: {}" + response.rc(), className, response.rcm());
				throw new GatewayTimeoutException(response.rcm());
			} else {
				bookData = new BookData(UUID.randomUUID(), bookingCode, new Date(), BookingStatus.RESERVED, number,
						timelimit, session.getDomain(), office.getCode(), office.getName(), office.getPackageName(),
						ProductCode.PLN_POSTPAID, response.trxid(), false, detailProduct);
				bookData.setBookedBy(
						session.getUser() == null ? bookData.getBookedBy() : session.getUser().getPrincipal());
				bookData.setRefNum(response.refnum());
				bookData.setProductCode(detailProduct.getCode());
				bookData.setDetail("false");
				bookData.setPeriod(response.blth());
				bookData.setCustomerName(response.nama());
				bookData.setContactData(contactData);

				var expression = office.getMapCommissionExpression().get("PLN-POSTPAID");
				nta = Double.valueOf(response.total());
				bookData.setNta(nta);
				Double markup = getMarkupValue("PLN-POSTPAID", office.getPackageName(),
						systemParameter.get("generalproduct.pln.postpaid.comm.van"));
				String month = response.bulan().replaceAll("[^0-9]", "");
				totalMonth = Integer.parseInt(month);
				totalPrice = nta + (markup * totalMonth);
				if (expression == null) {
					throw new GatewayTimeoutException("error.calculatecommission.data.notfoundexpression");
				}
				commission = priceEvaluator.evaluateCommission(expression.replaceAll("%", "/100"), totalPrice, nta);
				ntsa = totalPrice - (commission * totalMonth);
				bookData.setNtsa(ntsa);

				bookData.setFinancialStatement(
						financialService.fromTotal(bookData, nta, totalPrice, expression, markup));
				bookData.setTotalMonth(totalMonth.toString());
				var financialStatement = bookData.getFinancialStatement();
				totalPrice = Math.ceil(bookData.getFinancialStatement().getTicketPrice());
				bookData.setTotalPrice(totalPrice);
				bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.PLN_POSTPAID, number, response.tarif(),
						Double.valueOf(response.tagihan()), Double.valueOf(response.tagihan()), markup * totalMonth, 0D,
						Double.valueOf(response.total()), financialStatement.getChannelDiscount() * totalMonth, ntsa));
				bookDataRepo.saveAndFlush(bookData);

				var fareData = bookData.getFareData();
				bookRespone = new BookResponse(bookData.getBookId(), bookData.getBookingCode(), bookData.getStatus(),
						bookData.getProductType(), bookData.getBookDate(), bookData.getTimelimit(),
						bookData.getCustomerName(), bookData.getCustomerProductNumber(), "-", bookData.getRefNum(),
						fareData.getPower(), Double.valueOf(response.total()), Double.valueOf(response.total()),
						markup * totalMonth, bookData.getTotalPrice(), fareData.getPenaltie(),
						fareData.getChannelDiscount(), fareData.getNtsa(), bookData.getTotalMonth(),
						bookData.getPeriod());
			}
		}

		case BPJS_KS -> {
			BPJSInquiryResponse response = inquiryService
					.BPJSInquiry(new BPJSInquiryRequest(cid, dt, hashcode, "GP", "INQ", trxId, "json", "bpjs", number));
			if (response.status().equals("ERROR") || response.rc().equals("0088")) {
				log.error("[{}.inquiryBPJS] Error: {}" + response.rc(), className, response.rcm());
				throw new GatewayTimeoutException(response.rcm());
			} else {
				bookData = new BookData(UUID.randomUUID(), generator.generateClientReference(formatCustomerId(number)),
						new Date(), BookingStatus.RESERVED, number, timelimit, session.getDomain(), office.getCode(),
						office.getName(), office.getPackageName(), ProductCode.BPJS_KS, response.trxid(), false,
						detailProduct);
				bookData.setBookedBy(
						session.getUser() == null ? bookData.getBookedBy() : session.getUser().getPrincipal());
				bookData.setRefNum(response.refnum());
				bookData.setProductCode(detailProduct.getCode());
				bookData.setCustomerName(response.data().NAMA_PELANGGAN());
				bookData.setContactData(contactData);
				bookData.setVirtualAccountNumber(response.data().NO_VA());

				// Calculate Financial Statement
				var expression = office.getMapCommissionExpression().get("BPJS-KS");
				String month = response.data().JML_BULAN().replaceAll("[^0-9]", "");
				totalMonth = Integer.parseInt(month);
				bookData.setTotalMonth(totalMonth.toString());

				nta = response.totaltag();
				Double markup = getMarkupValue("BPJS-KS", office.getPackageName(),
						systemParameter.get("generalproduct.bpjs.ks.van.adminfee").toString());
				Double totalPaid = nta + markup;
				setProductPrice(bookData, nta, expression, totalPaid, bookData.getProductDetail().getAdminCommission());

				bookData.setFinancialStatement(financialService.fromTotal(bookData,
						nta - bookData.getProductDetail().getAdminCommission(), totalPaid, expression, markup));

				totalPrice = Math.ceil(bookData.getFinancialStatement().getPaxPrice());
				bookData.setTotalPrice(totalPrice);
				bookData.setNta(nta);
				bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.BPJS_KS, number, "-",
						response.jmltagihan(), response.totaltag(), response.admin(), 0D, totalPrice,
						bookData.getFinancialStatement().getChannelDiscount(), ntsa));
				bookDataRepo.saveAndFlush(bookData);

				var fareData = bookData.getFareData();
				bookRespone = new BookResponse(bookData.getBookId(), bookData.getBookingCode(), bookData.getStatus(),
						bookData.getProductType(), bookData.getBookDate(), bookData.getTimelimit(),
						bookData.getCustomerName(), bookData.getCustomerProductNumber(), response.data().NO_VA(),
						bookData.getRefNum(), fareData.getPower(), fareData.getNominal(), fareData.getPrice(),
						fareData.getAdminFee(), bookData.getTotalPrice(), fareData.getPenaltie(),
						fareData.getChannelDiscount(), bookData.getNtsa(), bookData.getTotalMonth(),
						bookData.getPeriod());
			}
		}

		case PDAM_P -> {
			PDAMInquiryResponse response = inquiryService.PDAMInquiry(new PDAMInquiryRequest(cid, dt, hashcode, "PDAM",
					"INQ", trxId, number, "JSON", "false", productDetail.code()));

			if (response.status().equals("ERROR") || response.rc().equals("0088")) {
				log.error("[{}.inquiryPDAM] Error: {}" + response.rc(), className, response.rcm());
				throw new GatewayTimeoutException(response.rcm());
			} else {
				bookData = new BookData(UUID.randomUUID(), bookingCode, new Date(), BookingStatus.RESERVED, number,
						timelimit, session.getDomain(), office.getCode(), office.getName(), office.getPackageName(),
						ProductCode.PDAM_P, response.trxid(), false, detailProduct);

				bookData.setBookedBy(
						session.getUser() == null ? bookData.getBookedBy() : session.getUser().getPrincipal());
				bookData.setRefNum(response.refnum());
				bookData.setProductCode(detailProduct.getCode());
				bookData.setPeriod(response.billperiod());
				bookData.setCustomerName(response.name());
				bookData.setDetail("false");
				bookData.setContactData(contactData);
				var expression = office.getMapCommissionExpression().get("PDAM-P");
				String month = response.totalperiod().replaceAll("[^0-9]", "");
				totalMonth = Integer.parseInt(month);
				bookData.setTotalMonth(totalMonth.toString());
				nta = Double.valueOf(response.transamount());
				Double adminCommission = bookData.getProductDetail().getAdminCommission() * totalMonth;
				Double markup = getMarkupValue("PDAM-P", office.getPackageName(),
						systemParameter.get("generalproduct.pdam.adminfee"));
				totalPrice = nta + markup;
				setProductPrice(bookData, nta, expression, totalPrice, adminCommission);

				bookData.setFinancialStatement(
						financialService.fromTotal(bookData, nta - adminCommission, totalPrice, expression, markup));

				var financialStatement = bookData.getFinancialStatement();

				totalPrice = Math.ceil(financialStatement.getPaxPrice());
				bookData.setTotalPrice(totalPrice);

				var nominalPdam = Double.valueOf(response.transamount()) - Double.valueOf(response.admincharge());
				bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.PDAM_P, number, "-", nominalPdam,
						Double.valueOf(response.transamount()), (Double.valueOf(response.admincharge())) + markup, 0D,
						Double.valueOf(response.transamount()), financialStatement.getChannelDiscount(), ntsa));
				bookDataRepo.saveAndFlush(bookData);

				var fareData = bookData.getFareData();
				bookRespone = new BookResponse(bookData.getBookId(), bookData.getBookingCode(), bookData.getStatus(),
						bookData.getProductType(), bookData.getBookDate(), bookData.getTimelimit(),
						bookData.getCustomerName(), bookData.getCustomerProductNumber(), "-", bookData.getRefNum(),
						fareData.getPower(), fareData.getNominal(), fareData.getPrice(), fareData.getAdminFee(),
						bookData.getTotalPrice(), fareData.getPenaltie(), fareData.getChannelDiscount(),
						bookData.getNtsa(), bookData.getTotalMonth(), bookData.getPeriod());
			}
		}

		case PULSA_PAKETDATA, PULSA_PRABAYAR, PULSA_PREPAID -> {
			VoucherInquiryResponse response = inquiryService.VoucherInquiry(new VoucherInquiryRequest(cid, dt, hashcode,
					"ISI", "INQ", trxId, number, "JSON", productDetail.code()));

			if (response.status().equals("ERROR") || response.rc().equals("0088")) {
				log.error("[{}.inquiryVoucher] Error: {}" + response.rc(), className, response.rcm());
				throw new GatewayTimeoutException(response.rcm());
			} else {
				bookData = new BookData(UUID.randomUUID(), bookingCode, new Date(), BookingStatus.RESERVED, number,
						timelimit, session.getDomain(), office.getCode(), office.getName(), office.getPackageName(),
						null, response.trxid(), false, detailProduct);
				bookData.setBookedBy(
						session.getUser() == null ? bookData.getBookedBy() : session.getUser().getPrincipal());
				bookData.setRefNum(response.refnum());

				if (productDetail.productCode().equals(ProductCode.PULSA_PAKETDATA)) {
					bookData.setProductType(ProductCode.PULSA_PAKETDATA);
					bookData.setProductCode(detailProduct.getCode());
					var expression = office.getMapCommissionExpression().get("PULSA-PAKETDATA");
					nta = response.harga();
					Double markup = getMarkupValue("PULSA-PAKETDATA", office.getPackageName(),
							systemParameter.get("generalproduct.pulsa.markup.van"));
					totalPrice = nta + markup;
					var data = setProductPrice(bookData, nta, expression, totalPrice,
							bookData.getProductDetail().getAdminCommission());

					bookData.setFinancialStatement(
							financialService.fromTotal(bookData, nta, totalPrice, expression, markup));
					var financialStatement = bookData.getFinancialStatement();

					totalPrice = Math.ceil(bookData.getFinancialStatement().getPaxPrice());
					bookData.setTotalPrice(totalPrice);
					bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.PULSA_PAKETDATA, number, "-",
							response.nominal(), response.harga(), 0D, 0D, totalPrice,
							financialStatement.getChannelDiscount(), data.getNtsa()));

				} else if (productDetail.productCode().equals(ProductCode.PULSA_PRABAYAR)) {
					bookData.setProductType(ProductCode.PULSA_PRABAYAR);
					bookData.setProductCode(detailProduct.getCode());
					var expression = office.getMapCommissionExpression().get("PULSA-PRABAYAR");

					Double markup = getMarkupValue("PULSA-PRABAYAR", office.getPackageName(),
							systemParameter.get("generalproduct.pulsa.markup.van"));
					nta = response.harga();
					Double nominal = response.nominal();
					Double ntaNominalDiff = nominal - nta;

					if (ntaNominalDiff <= markup) {
						totalPrice = nta + markup;
					} else {
						totalPrice = nominal;
					}
					var data = setProductPrice(bookData, nta, expression, totalPrice,
							bookData.getProductDetail().getAdminCommission());
					bookData.setFinancialStatement(
							financialService.fromTotal(bookData, nta, totalPrice, expression, markup));

					var financialStatement = bookData.getFinancialStatement();

					totalPrice = Math.ceil(bookData.getFinancialStatement().getPaxPrice());
					bookData.setTotalPrice(totalPrice);
					bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.PULSA_PRABAYAR, number, "-",
							response.nominal(), response.harga(), 0D, 0D, totalPrice,
							financialStatement.getChannelDiscount(), data.getNtsa()));

				} else {
					bookData.setProductType(ProductCode.PULSA_PREPAID);
					bookData.setProductCode(detailProduct.getCode());
					var expression = office.getMapCommissionExpression().get("PULSA-PREPAID");
					nta = response.harga();
					Double nominal = response.nominal();
					Double ntaNominalDiff = nominal - nta;
					Double markup = getMarkupValue("PULSA-PREPAID", office.getPackageName(),
							systemParameter.get("generalproduct.pulsa.markup.van"));
					if (ntaNominalDiff <= markup) {
						totalPrice = markup + nta;
					} else {
						totalPrice = nominal;
					}
					setProductPrice(bookData, nta, expression, totalPrice,
							bookData.getProductDetail().getAdminCommission());

					bookData.setFinancialStatement(
							financialService.fromTotal(bookData, nta, totalPrice, expression, markup));
					var financialStatement = bookData.getFinancialStatement();
					totalPrice = Math.ceil(bookData.getFinancialStatement().getPaxPrice());
					bookData.setTotalPrice(totalPrice);
					bookData.setFareData(new FareData(UUID.randomUUID(), ProductCode.PULSA_PAKETDATA, number, "-",
							response.nominal(), response.harga(), 0D, 0D, totalPrice,
							financialStatement.getChannelDiscount(), ntsa));
				}

				bookData.setContactData(contactData);
				bookDataRepo.saveAndFlush(bookData);
				var fareData = bookData.getFareData();
				bookRespone = new BookResponse(bookData.getBookId(), bookData.getBookingCode(), bookData.getStatus(),
						bookData.getProductType(), bookData.getBookDate(), bookData.getTimelimit(),
						bookData.getCustomerName(), bookData.getCustomerProductNumber(), "-", bookData.getRefNum(),
						fareData.getPower(), fareData.getNominal(), fareData.getPrice(), fareData.getAdminFee(),
						fareData.getTotalPrice(), fareData.getPenaltie(), fareData.getChannelDiscount(),
						bookData.getNtsa(), bookData.getTotalMonth(), bookData.getPeriod());
			}
		}

		default -> throw new IllegalArgumentException("Unexpected value: " + productDetail.productCode());
		}
		emailHelper.sendReserveNotification(bookData);
		return bookRespone;

	}

	@Transactional
	public BookData process(BookData data)
			throws GatewayTimeoutException, JsonProcessingException, DataNotFoundException {
		try {

			String hashcode = generator.SHA256Generator(cid, data.getBookDate().toString(), secretkey);
			switch (data.getProductType()) {
			case PLN_PREPAID -> {
				DecimalFormat decimalFormat = new DecimalFormat("#");
				String nominal = decimalFormat.format(data.getFareData().getPrice());

				var response = gatewayPayment.paymentPLNPrepaid(new GatewayPaymentRequest(cid,
						data.getBookDate().toString(), hashcode, "PRE", "PAY", data.getCustomerProductNumber(), null,
						null, null, null, null, null, nominal, "JSON", data.getTrxId(), data.getRefNum(), null));
				data.setGatewayStatus(response.getStatus());
				data.setResponseCodeMessage(response.getRcm());
				if (response.getStatus().equals("ERROR")) {
					data.setStatus(BookingStatus.CANCELED);
					data.setCancelAt(new Date());
					data.setCanceledBy("SYSTEM");
					bookDataRepo.saveAndFlush(data);
					var reversalData = new ReversalData(data.getTransactionId(), data.getGoblinAccountId(),
							response.getRc() + response.getRcm());
					reversalRepo.saveAndFlush(reversalData);
					log.error("[{}.process] Error: {}" + response.getRc(), className, response.getRcm());
					throw new GatewayTimeoutException(response.getRcm());
				} else {
					data.setAdditionalInfo(response.getInfo());
					data.setStatus(BookingStatus.CONFIRMED);
					data.setPaymentDate(new Date());
					data.setPaidBy(session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal());
					data.setToken(response.getToken());
					data.setFareAndPower(response.getTarifdaya());
					data.setTotalKwh(response.getJmlkwh());
					bookDataRepo.saveAndFlush(data);
					emailHelper.sendSuccessNotification(data);
				}

			}
			case PLN_POSTPAID -> {
				var response = gatewayPayment.paymentPLNPostpaid(new GatewayPaymentRequest(cid,
						data.getBookDate().toString(), hashcode, "PLN", "PAY", null, data.getCustomerProductNumber(),
						"detail", null, null, null, null, null, "JSON", data.getTrxId(), data.getRefNum(), null));
				data.setGatewayStatus(response.status());
				data.setResponseCodeMessage(response.rcm());
				if (response.status().equals("ERROR")) {
					data.setStatus(BookingStatus.CANCELED);
					data.setCancelAt(new Date());
					data.setCanceledBy("SYSTEM");
					var reversalData = new ReversalData(data.getTransactionId(), data.getGoblinAccountId(),
							response.rc() + response.rcm());
					reversalRepo.saveAndFlush(reversalData);
					bookDataRepo.saveAndFlush(data);
					log.error("[{}.process] Error: {}" + response.rc(), className, response.rcm());
					throw new GatewayTimeoutException(response.rcm());
				} else {
					data.setAdditionalInfo(response.info1() + " " + response.info2());
					data.setStatus(BookingStatus.CONFIRMED);
					data.setPaymentDate(new Date());
					data.setPaidBy(session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal());
					bookDataRepo.saveAndFlush(data);
					emailHelper.sendSuccessNotification(data);
				}
			}
			case PDAM_P -> {
				var response = gatewayPayment.paymentPDAM(new GatewayPaymentRequest(cid, data.getBookDate().toString(),
						hashcode, "PDAM", "PAY", null, data.getCustomerProductNumber(), data.getDetail(),
						data.getProductCode(), null, null, null, null, "JSON", data.getTrxId(), null, null));
				data.setGatewayStatus(response.status());
				data.setResponseCodeMessage(response.rcm());
				if (response.status().equals("ERROR")) {
					data.setStatus(BookingStatus.CANCELED);
					data.setCancelAt(new Date());
					data.setCanceledBy("SYSTEM");
					bookDataRepo.saveAndFlush(data);
					var reversalData = new ReversalData(data.getTransactionId(), data.getGoblinAccountId(),
							response.rc() + response.rcm());
					reversalRepo.saveAndFlush(reversalData);
					log.error("[{}.process] Error: {}" + response.rc(), className, response.rcm());
					throw new GatewayTimeoutException(response.rcm());
				} else {
					data.setAdditionalInfo(response.info());
					data.setStatus(BookingStatus.CONFIRMED);
					data.setPaymentDate(new Date());
					data.setPaidBy(session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal());
					bookDataRepo.saveAndFlush(data);
					emailHelper.sendSuccessNotification(data);
				}
			}
			case BPJS_KS -> {
				DecimalFormat decimalFormat = new DecimalFormat("#");
				String amount = decimalFormat.format(data.getNta());

				var response = gatewayPayment.paymentBPJS(new GatewayPaymentRequest(cid, data.getBookDate().toString(),
						hashcode, "GP", "PAY", null, null, null, "bpjs", data.getCustomerProductNumber(), null, amount,
						null, "JSON", data.getTrxId(), null, null));
				data.setGatewayStatus(response.status());
				data.setResponseCodeMessage(response.rcm());
				if (response.status().equals("ERROR")) {
					data.setStatus(BookingStatus.CANCELED);
					data.setCancelAt(new Date());
					data.setCanceledBy("SYSTEM");
					bookDataRepo.saveAndFlush(data);
					var reversalData = new ReversalData(data.getTransactionId(), data.getGoblinAccountId(),
							response.rc() + response.rcm());
					reversalRepo.saveAndFlush(reversalData);
					log.error("[{}.process] Error: {}" + response.rc(), className, response.rcm());
					throw new GatewayTimeoutException(response.rcm());
				} else {
					data.setAdditionalInfo(response.infotext());
					data.setStatus(BookingStatus.CONFIRMED);
					data.setPaymentDate(new Date());
					data.setPaidBy(session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal());
					bookDataRepo.saveAndFlush(data);
					emailHelper.sendSuccessNotification(data);
				}
			}
			case PULSA_PRABAYAR, PULSA_PAKETDATA -> {
				var response = gatewayPayment.paymentVoucher(new VoucherPaymentRequest(cid,
						data.getBookDate().toString(), hashcode, "ISI", "PAY", data.getTrxId(),
						data.getCustomerProductNumber(), "JSON", data.getProductDetail().getCode()));
				data.setGatewayStatus(response.status());
				data.setResponseCodeMessage(response.rcm());
				if (response.status().equals("ERROR")) {
					data.setStatus(BookingStatus.CANCELED);
					data.setCancelAt(new Date());
					data.setCanceledBy("SYSTEM");
					bookDataRepo.saveAndFlush(data);
					var reversalData = new ReversalData(data.getTransactionId(), data.getGoblinAccountId(),
							response.rc() + response.rcm());
					reversalRepo.saveAndFlush(reversalData);
					log.error("[{}.process] Error: {}" + response.rc(), className, response.rcm());
					throw new GatewayTimeoutException(response.rcm());
				} else {
					data.setTrxId(response.trxid());
					data.setStatus(BookingStatus.CONFIRMED);
					data.setPaymentDate(new Date());
					data.setPaidBy(session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal());
					data.setSerialNumber(response.serialnumber());
					bookDataRepo.saveAndFlush(data);
					emailHelper.sendSuccessNotification(data);
				}
			}
			case PULSA_PREPAID -> {
			}
			case SAMOLNAS -> {
			}
			case MULTIFINANCE -> {
			}

			default -> throw new IllegalArgumentException("Unexpected value: " + data.getProductType());
			}
		} catch (Exception e) {
			log.error("process: ", e.getMessage());
			e.printStackTrace();
		}
		return data;
	}

	public void bookDataStatusCheck(BookData data) {
		if (data.getStatus().equals(BookingStatus.RESERVED) || data.getStatus().equals(BookingStatus.NEW)) {
			return;
		}
		if (data.getStatus().equals(BookingStatus.CANCELED)) {
			log.error("[{}.bookDataStatusCheck] error: Percobaan book ketika status CANCELED",
					this.getClass().getCanonicalName());
			throw new RuntimeException();
		}
		log.error("[{}.bookDataStatusCheck] error: Percobaan issue ketika status bukan BOOKED ataupun CANCELED",
				this.getClass().getCanonicalName());
		throw new RuntimeException("Percobaan issue ketika status bukan BOOKED ataupun CANCELED");

	}

	public BookData getByOfficeCodeAndBookingCode(String officeCode, String bookingCode) throws DataNotFoundException {
		var data = bookDataRepo.findByOfficeCodeAndBookingCode(officeCode, bookingCode);
		if (data.isEmpty()) {
			log.error("[{}.getByOfficeIdAndBookingCode] Data Not Found", className);
			throw new DataNotFoundException();
		}
		return data.get();
	}

	public Double getMarkupValue(String productCode, String packageName, String defaultMarkup) {
		Double markup = 0D;
		MarkupValue customMarkup = markupRepo.findByProductCodeAndPackageName(productCode, packageName);
		if (customMarkup != null && customMarkup.getCustomMarkup() != null) {
			markup = Double.valueOf(customMarkup.getCustomMarkup());
		} else {
			markup = Double.valueOf(defaultMarkup);
		}
		return markup;
	}

	public BookData setProductPrice(BookData data, Double nta, String expression, Double totalPrice,
			Double adminCommission) throws GatewayTimeoutException {
		Double ntsa;
		if (expression == null) {
			throw new GatewayTimeoutException("error.calculatecommission.data.notfoundexpression");
		}
		nta = nta - adminCommission;
		data.setNta(nta);

		Double commission = priceEvaluator.evaluateCommission(expression.replaceAll("%", "/100"), totalPrice, nta);
		ntsa = totalPrice - commission;
		data.setNtsa(ntsa);
		data.setTotalPrice(totalPrice);
		bookDataRepo.saveAndFlush(data);
		return data;

	}

	private static String formatCustomerId(String customerId) {
		while (customerId.length() < 13) {
			customerId = "0" + customerId;
		}
		return customerId;
	}

}
