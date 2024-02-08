package com.voltras.ppob.service.services;

import static com.voltras.payment.common.api.statics.PaymentRoleStatic.VOLTRAS_PAYMENT;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.common.api.exceptions.GatewayTimeoutException;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.payment.notification.api.exceptions.ForwardFailInfo;
import com.voltras.payment.notification.api.exceptions.ForwardFailedException;
import com.voltras.payment.notification.api.models.ForwardResponse;
import com.voltras.payment.notification.api.models.PaymentNotification;
import com.voltras.payment.notification.api.services.PaymentNotificationService;
import com.voltras.ppob.service.components.BookingHelper;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.entities.ReversalData;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.ppob.service.repositories.ReversalDataRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Service("paymentNotificationService")
public class PaymentNotificationServiceImpl implements PaymentNotificationService, RpcBasicService {
	@Autowired
	private LogHelper logger;
//	@Autowired
//	private VanAdditionalRequestData session;
	@Autowired
	private BookingHelper bookHelper;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private ReversalDataRepository reversalDataRepo;
//	@Autowired
//	private EmailHelper emailHelper;

	@Value("${spring.profiles.active}")
	private String springActiveProfile;

	@Override
	public Boolean receiveNotification(@Valid PaymentNotification notification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Publish({ VOLTRAS_PAYMENT })
	@Transactional
	public ForwardResponse forward(@NotNull String item, @NotNull String referenceNo, @NotNull String transactionId,
			@NotNull String providerName, @NotNull Double amount) throws ForwardFailedException {
		logger.info("[{}.forward] Mendapatkan notifikasi untuk item: {}", this.getClass().getCanonicalName(), item,
				transactionId);
		var failInfo = new ForwardFailInfo();
		String failMessage = null;

		var bookData = bookDataRepo.findById(UUID.fromString(item)).orElse(null);

		if (bookData == null) {
			logger.error("[{}.forward] tidak ada transaksi dengan transactionId: {}",
					this.getClass().getCanonicalName(), transactionId);
			failMessage = String.format("Data untuk item %s tidak ditemukan", item);
			failInfo.setMessage(failMessage);
			throw new ForwardFailedException(failInfo);
		}
		bookData.setOrderId(transactionId);
		bookData.setPaymentChannel(providerName);

		if (!bookData.getPaymentStatus().equals(PaymentStatus.WAITING)) {
			failMessage = "Get payment notification but status data is not WAITING";
			logger.error("[{}.forward] {}", this.getClass().getCanonicalName(), failMessage);
			if (bookData.getStatus().equals(BookingStatus.CANCELED)) {
				reversalDataRepo.saveAndFlush(new ReversalData(bookData.getReversalTransactionId(),
						bookData.getGoblinAccountId(), "Get payment notification but status data is CANCELED"));

			}
			failInfo.setMessage(failMessage);
			throw new ForwardFailedException(failInfo);
		}
		try {
			bookData = bookHelper.process(bookData);
			if (bookData.getGatewayStatus().equals("ERROR")) {
				logger.error("[{}.process] Error: {}" + "", bookData.getResponseCodeMessage());
				throw new GatewayTimeoutException(bookData.getResponseCodeMessage());
			} else {
				bookData.setPaymentStatus(PaymentStatus.SUCCESS);
				bookData.setTotalPayment(amount);
				if (bookData.getPaymentType() == null) {
					bookData.setPaymentType(PaymentType.CC);
				}
			}
		} catch (GatewayTimeoutException | DataNotFoundException | JsonProcessingException e) {
			e.printStackTrace();
		}
		bookDataRepo.saveAndFlush(bookData);

		return new ForwardResponse(List.of(bookData.getBookingCode()));
	}

}
