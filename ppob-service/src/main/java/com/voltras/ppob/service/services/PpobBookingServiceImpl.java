package com.voltras.ppob.service.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.common.api.exceptions.GatewayTimeoutException;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.payment.common.api.exceptions.InsufficientBalanceException;
import com.voltras.ppob.api.exceptions.TransactionPauseException;
import com.voltras.ppob.api.models.ProductCode;
import com.voltras.ppob.api.models.availability.ProductDetail;
import com.voltras.ppob.api.models.book.BookDetail;
import com.voltras.ppob.api.models.book.BookResponse;
import com.voltras.ppob.api.models.book.PaymentResponse;
import com.voltras.ppob.api.models.book.RetrieveFilter;
import com.voltras.ppob.api.models.book.TransactionData;
import com.voltras.ppob.api.models.book.ValidatedPaymentResponse;
import com.voltras.ppob.api.services.PpobBookingService;
import com.voltras.ppob.service.components.BookingHelper;
import com.voltras.ppob.service.components.EmailHelper;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.entities.Office;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.ppob.service.repositories.DomainDataRepository;
import com.voltras.ppob.service.repositories.OfficeRepository;
import com.voltras.ppob.utils.DateUtils;
import com.voltras.ppob.utils.GenerateUtil;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service("ppobBookingService")
public class PpobBookingServiceImpl implements PpobBookingService, RpcBasicService {

	@Autowired
	private VanAdditionalRequestData session;
	@Autowired
	private BookingHelper bookHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	LogHelper log;

	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private DomainDataRepository domainDataRepo;
	@Autowired
	private OfficeRepository officeDataRepo;

	@Value("${ppob.gateway.cid}")
	private String cid;
	@Value("${ppob.gateway.secretkey}")
	private String secretkey;
	@Value("${scheduler.book.pendinglifetimeminutes}")
	private Integer pendingLifetime;

	private final String className = this.getClass().getCanonicalName();

	@Override
	@Publish(allowAll = true)
	public BookResponse book(ProductDetail productDetail, String number, Boolean detail)
			throws GatewayTimeoutException, TransactionPauseException {
		return bookHelper.inquiry(productDetail, number, detail);
	}

	@Override
	@Transactional
	@Publish(allowAll = true)
	public ValidatedPaymentResponse validatedPayment(@NotBlank String bookingCode,
			@NotNull @Valid PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, GatewayTimeoutException, DataNotFoundException {
		BookData data;
		try {
			data = getBookDataByBookingCode(bookingCode, "validatePayment");
		} catch (DataNotFoundException e) {
			throw new RuntimeException("BookData tidak ditemukan saat validateBook");
		}
		bookHelper.bookDataStatusCheck(data);
		String item = null;
		if (data.getProductDetail().getProductCode().equals("PLN-PREPAID")
				|| data.getProductDetail().getProductCode().equals("PULSA-PRABAYAR")) {
			Double nominal = data.getProductDetail().getNominal();
			DecimalFormat decimalFormat = new DecimalFormat("#");
			String formattedNominal = decimalFormat.format(nominal);

			item = data.getProductDetail().getVoucherName() + " " + formattedNominal;
		} else {
			item = data.getProductDetail().getVoucherName();
		}

		return new ValidatedPaymentResponse(bookingCode, data.getTotalPrice(), data.getNtsa(), item);
	}

	@Override
	@Transactional
	@Publish(allowAll = true)
	public PaymentResponse finalizePayment(@NotBlank String bookingCode, @NotNull @Valid PaymentType paymentType,
			Double totalPayment, String goblinAccountId, String providerTransactionId, String transactionId,
			@NotNull @Valid PaymentStatus paymentStatus) throws GatewayTimeoutException, DataNotFoundException {
		BookData data;
		try {
			data = getBookDataByBookingCode(bookingCode, "finalizePayment");
		} catch (DataNotFoundException e) {
			throw new RuntimeException("BookData tidak ditemukan saat finalizePayment");
		}
		data.setGoblinAccountId(goblinAccountId);
		data.setProviderTransactionId(providerTransactionId);
		data.setTransactionId(transactionId);
		switch (paymentStatus) {
		case SUCCESS -> {
			try {
				data = bookHelper.process(data);
				if (data.getGatewayStatus().equals("ERROR")) {
					log.error("[{}.process] Error: {}" + className, data.getResponseCodeMessage());
					throw new GatewayTimeoutException(data.getResponseCodeMessage());
				} else {
					data.setPaymentChannel("deposit");
					data.setTotalPayment(totalPayment);
					data.setPaymentStatus(PaymentStatus.SUCCESS);
					data.setPaymentType(paymentType);
					bookDataRepo.saveAndFlush(data);
				}
			} catch (JsonProcessingException e) {
				log.error("process: ", e.getMessage());
				e.printStackTrace();
			}
		}
		case WAITING -> {
			data.setStatus(BookingStatus.PENDING);
			data.setPaymentStatus(PaymentStatus.WAITING);
			data.setCreatedAt(new Date());
			if (paymentType.equals(PaymentType.TRANSFER)) {
				data.setPaymentType(paymentType);
			}
			data.setPaymentType(null);
			bookDataRepo.saveAndFlush(data);
			emailHelper.sendWaitingPaymentNotification(data);
			return new PaymentResponse(data.getBookId().toString(), bookingCode, providerTransactionId);
		}
		case FAIL -> {
			data.setCanceledBy("SYSTEM");
			data.setCancelAt(new Date());
			data.setStatus(BookingStatus.CANCELED);
			data.setTotalPayment(totalPayment);
			bookDataRepo.saveAndFlush(data);
//			emailHelper.sendFailIssued(data);
		}

		default -> throw new IllegalArgumentException("Unexpected value: " + paymentStatus);
		}

		return new PaymentResponse(data.getBookId().toString(), bookingCode, providerTransactionId);
	}

	@Override
	public PaymentResponse payment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, GatewayTimeoutException, DataNotFoundException {
		throw new RuntimeException("Unsupported Method");
	}

	@Override
	@Transactional
	@Publish(allowAll = true)
	public List<TransactionData> retrieveTransaction(String bookingCode, Integer page, Integer size,
			RetrieveFilter filter) {
		filter = filter != null ? filter : new RetrieveFilter(null, null, null, null);
		List<BookData> allDatas = new ArrayList<BookData>();
		allDatas = bookDataRepo.findAllByOfficeCodeAndBookDateAndStatusAndProductType(session.getOffice().getCode(),
				DateUtils.getMinRetrieveDate(filter.from()), DateUtils.getMaxRetrieveDate(filter.to()), filter.status(),
				GenerateUtil.generatePageable(page, size, Map.of("bookDate", false)), filter.productCode());
		if (bookingCode != null) {
			allDatas = bookDataRepo.findByBookingCode(bookingCode);
		}
		return allDatas.isEmpty() ? List.of()
				: allDatas.stream().map(data -> new TransactionData(new ProductDetail(data.getProductDetail().getCode(),
						stringToEnum(data.getProductDetail().getProductCode()), data.getProductDetail().getNominal(),
						data.getProductDetail().getVoucherName(), data.getProductDetail().getDescription(), null),
						data.getBookingCode(), data.getTotalPrice(), data.getStatus(), data.getBookDate(),
						data.getCustomerName(), data.getCustomerProductNumber(), data.getToken(),
						data.getFareAndPower(), data.getTotalKwh(), data.getRefNum(), data.getSerialNumber(),
						data.getVirtualAccountNumber())).collect(Collectors.toList());
	}

	@Override
	@Transactional
	@Publish(allowAll = true)
	public BookDetail getBookDetail(String bookingCode) throws DataNotFoundException {
		var office = session.getOffice();
		var data = bookHelper.getByOfficeCodeAndBookingCode(office.getCode(), bookingCode);
		var domain = domainDataRepo.findByName(session.getDomain());
		if (!domain.getIsB2c()) {
			var officeData = officeDataRepo.findByCode(office.getCode()).orElse(null);
			if (officeData == null) {
				officeDataRepo.saveAndFlush(
						new Office(office.getAddress(), office.getCode(), office.getEmail(), office.getFax(),
								office.getLogo() == null ? null : Base64.getEncoder().encodeToString(office.getLogo()),
								office.getName(), office.getPackageName(), office.getPhone(),
								session.getUser().getPrincipal()));
			} else {
				officeData.setName(office.getName());
				officeData.setLogo(
						office.getLogo() == null ? null : Base64.getEncoder().encodeToString(office.getLogo()));
			}
		}
		if (data.getCanceledBy() == null && data.getStatus().equals(BookingStatus.CANCELED)) {
			data.setCanceledBy("SYSTEM");
			bookDataRepo.saveAndFlush(data);
		}
		String providerTransactionId = data.getPaymentType() != null && data.getPaymentType().equals(PaymentType.VA)
				? data.getProviderTransactionId()
				: null;

		var productDetail = data.getProductDetail();
		var fareData = data.getFareData();
		Double totalPrice = 0D;
		switch (data.getProductType()) {
		case BPJS_KS, PLN_POSTPAID, PLN_PREPAID, PDAM_P -> {
			totalPrice = data.getTotalPrice();
		}
		case PULSA_PRABAYAR, PULSA_PAKETDATA, PULSA_PREPAID -> {
			totalPrice = fareData.getTotalPrice();
		}
		case MULTIFINANCE -> throw new UnsupportedOperationException("Unimplemented case: " + data.getProductType());
		case SAMOLNAS -> throw new UnsupportedOperationException("Unimplemented case: " + data.getProductType());
		default -> throw new IllegalArgumentException("Unexpected value: " + data.getProductType());
		}

		return new BookDetail(data.getBookId(),
				new ProductDetail(productDetail.getCode(), stringToEnum(productDetail.getProductCode()),
						productDetail.getNominal(), productDetail.getVoucherName(), productDetail.getDescription(),
						null),
				data.getBookingCode(), data.getStatus(), data.getBookedBy(), data.getBookDate(), data.getTimelimit(),
				data.getCustomerName(), data.getCustomerProductNumber(), data.getRefNum(), data.getToken(),
				data.getFareAndPower(), data.getTotalKwh(), data.getSerialNumber(), data.getVirtualAccountNumber(),
				data.getPaymentDate(), data.getPaidBy(), data.getPaymentType(), providerTransactionId,
				data.getTotalMonth(), data.getPeriod(), fareData.getNominal(), fareData.getPrice(),
				fareData.getAdminFee(), fareData.getPenaltie(), totalPrice, fareData.getChannelDiscount(),
				data.getNtsa());
	}

	private BookData getBookDataByBookingCode(String bookingCode, String officeCode) throws DataNotFoundException {

		var bookDataOpt = bookDataRepo.findByBookingCodeAndOfficeCode(bookingCode, session.getOffice().getCode());
		if (bookDataOpt.isEmpty()) {
			log.error("[{}.getBookDataByBookingCode] {} error: BookData dengan bookingCode: {} tidak ditemukan",
					this.getClass().getCanonicalName(), className, bookingCode);
			throw new DataNotFoundException();
		}
		return bookDataOpt.get();
	}

	private ProductCode stringToEnum(String type) {

		ProductCode enumData = switch (type) {
		case "PULSA-PRABAYAR" -> ProductCode.PULSA_PRABAYAR;
		case "PULSA-PAKETDATA" -> ProductCode.PULSA_PAKETDATA;
		case "PLN-PREPAID" -> ProductCode.PLN_PREPAID;
		case "PLN-POSTPAID" -> ProductCode.PLN_POSTPAID;
		case "PDAM-P" -> ProductCode.PDAM_P;
		case "BPJS-KS" -> ProductCode.BPJS_KS;
		case "SAMOLNAS" -> ProductCode.SAMOLNAS;
		case "MULTIFINANCE" -> ProductCode.MULTIFINANCE;
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
		return enumData;
	}

}
