package com.voltras.blockseatservice.services;

import static com.voltras.payment.common.api.statics.PaymentRoleStatic.VOLTRAS_PAYMENT;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.voltras.blockseatservice.components.EmailHelper;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.entities.ReversalData;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.ReversalDataRepository;
import com.voltras.blockseatservice.repositories.StatementDataRepository;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.payment.notification.api.exceptions.ForwardFailInfo;
import com.voltras.payment.notification.api.exceptions.ForwardFailedException;
import com.voltras.payment.notification.api.models.ForwardResponse;
import com.voltras.payment.notification.api.models.PaymentNotification;
import com.voltras.payment.notification.api.services.PaymentNotificationService;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;

import jakarta.transaction.Transactional;

@Service("paymentNotificationService")
public class PaymentNotificationServiceImpl implements PaymentNotificationService, RpcBasicService {
	@Autowired
	private LogHelper logger;
	@Autowired
	private StatementDataRepository statementRepo;
	@Autowired
	private ReversalDataRepository reversalDataRepo;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private EmailHelper emailHelper;

	@Value("${spring.profiles.active}")
	private String springActiveProfile;

	@Override
	public Boolean receiveNotification(@Valid PaymentNotification notification) {
		return true;
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
		var statementData = statementRepo.findById(UUID.fromString(item)).orElse(null);
		if (statementData == null) {
			logger.error("[{}.forward] tidak ada transaksi dengan transactionId: {}",
					this.getClass().getCanonicalName(), transactionId);
			failMessage = String.format("Data untuk item %s tidak ditemukan", item);
			failInfo.setMessage(failMessage);
			throw new ForwardFailedException(failInfo);
		}

		statementData.setOrderId(transactionId);
		statementData.setPaymentChannel(providerName);

		var bookData = statementData.getBookData();

		if (!statementData.getPaymentStatus().equals(PaymentStatus.WAITING)) {
			failMessage = "Get payment notification but status data is not WAITING";
			logger.error("[{}.forward] {}", this.getClass().getCanonicalName(), failMessage);
			if (bookData.getStatus().equals(BookingStatus.CANCELED)) {
				reversalDataRepo.saveAndFlush(new ReversalData(bookData.getReversalTransactionId(),
						bookData.getGoblinAccountId(), "Get payment notification but status data is CANCELED"));
			}
			failInfo.setMessage(failMessage);
			throw new ForwardFailedException(failInfo);
		}

		var normalForwardResponse = new ForwardResponse(List.of(bookData.getBookingCode()));
		statementData.setPaymentDate(LocalDateTime.now());
		statementData.setPaymentStatus(PaymentStatus.SUCCESS);
		statementData.setPaymentType(PaymentType.CC);

		var totalPayments = bookData.getStatement().stream()
				.filter(status -> status.getPaymentStatus().equals(PaymentStatus.SUCCESS))
				.mapToDouble(total -> total.getTotalPayment()).sum();

		bookData.setTotalPayment(totalPayments);
		bookData.setOutstanding(bookData.getTotalPrice() - totalPayments);
		bookDataRepo.saveAndFlush(bookData);

		if (!bookData.getTotalPrice().equals(totalPayments)) {
			bookData.setStatus(BookingStatus.RESERVED);
			bookDataRepo.saveAndFlush(bookData);
			emailHelper.sendReserveNotification(bookData);
		} else {
			bookData.setStatus(BookingStatus.CONFIRMED);
			bookDataRepo.saveAndFlush(bookData);
			emailHelper.sendSuccessNotification(bookData);
		}

		return normalForwardResponse;
	}
}
