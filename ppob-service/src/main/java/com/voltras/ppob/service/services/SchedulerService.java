package com.voltras.ppob.service.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.ppob.service.components.EmailHelper;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;

@Service
public class SchedulerService implements RpcBasicService {
	@Autowired
	private LogHelper logger;

	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private EmailHelper emailHelper;

	private final String className = this.getClass().getCanonicalName();

	@Value("${scheduler.statement.waitingminutes}")
	private Integer paymentWaitingTime;

	@Transactional
	public void timelimitBookHandler() {
	    var datas = bookLockingProcessWithPassingTimelimit();
	    if (datas == null) {
	        return;
	    }
	    var cancelAt = new Date();
	    datas.forEach(data -> {
	        data.setIsChecking(false);
	        data.setStatus(BookingStatus.CANCELED);
	        data.setCancelAt(cancelAt);
	        data.setCanceledBy("SYSTEM");
	        bookDataRepo.saveAndFlush(data);
	        emailHelper.sendCancellIssuedToCustomer(data);
	    });
	    bookDataRepo.saveAllAndFlush(datas);
	}

	@Transactional
	public void timelimitPaymentHandle() {
		var data = waitingPaymentLockingProcess(PaymentStatus.WAITING, paymentWaitingTime);
		if (data == null) {
			return;
		}

		data.forEach(datas -> {
			datas.setPaymentStatus(PaymentStatus.FAIL);
			emailHelper.sendFailIssued(datas);
		});
		bookDataRepo.saveAllAndFlush(data);
	}

	@Transactional
	private List<BookData> bookLockingProcessWithPassingTimelimit() {
		var datas = bookDataRepo.findByPassingTimelimit(new Date());
		if (datas.isEmpty()) {
			return null;
		}
		logger.info("[{}.bookLockingProcessWithPassingTimelimit] jumlah data yang melewati timelimit: {}", className,
				datas.size());

		datas.forEach(data -> {
			data.setIsChecking(true);
		});
		bookDataRepo.saveAllAndFlush(datas);
		return datas;
	}

	@Transactional
	private List<BookData> waitingPaymentLockingProcess(PaymentStatus status, Integer lifetime) {
		var data = bookDataRepo.findByPassingTimelimitAt(status, DateUtils.addMinutes(new Date(), -lifetime));
		if (data.isEmpty()) {
			return null;
		}
		logger.info("[{}.waitingPaymentLockingProcess] jumlah data {}: {}", className, status, data.size());
		bookDataRepo.saveAllAndFlush(data);
		return data;
	}

}
