package com.voltras.blockseatservice.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voltras.blockseatservice.components.EmailHelper;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.entities.BookData;
import com.voltras.blockseatservice.entities.StatementData;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.FareDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.blockseatservice.repositories.StatementDataRepository;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;

@Service
public class SchedulerService implements RpcBasicService {

	@Autowired
	private LogHelper logger;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private StatementDataRepository statementDataRepo;
	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private FareDataRepository fareRepo;

	private final String className = this.getClass().getCanonicalName();

	@Value("${scheduler.statement.waitingminutes}")
	private Integer paymentWaitingTime;

	@Transactional
	private List<BookData> bookLockingProcess(BookingStatus status) {
		var datas = bookDataRepo.findAllByStatus(status);
		if (datas.isEmpty()) {
			return null;
		}
		logger.info("[{}.bookLockingProcess] jumlah data {}: {}", this.getClass().getCanonicalName(), status,
				datas.size());
		datas.forEach(data -> data.setIsChecking(true));
		return datas;

	}

	@Transactional
	public void timelimitBookHandler() {
		var datas = bookLockingProcessWithPassingTimelimit();
		if (datas == null) {
			return;
		}
		var cancelAt = LocalDateTime.now();
		datas.forEach(data -> {
			var invenData = inventoryRepo.getByInventoryId(data.getInventoryId());

			data.setIsChecking(false);
			data.setStatus(BookingStatus.CANCELED);
			data.setCancelAt(cancelAt);
			data.setCancelBy("SYSTEM");
			bookDataRepo.saveAndFlush(data);

			invenData.setSeatAllot(invenData.getSeatAllot() + data.getSeatCount());
			invenData.setSeatCapacity(invenData.getSeatCapacity() + data.getSeatCount());
			var fareDataz = fareRepo.findByInventoryIdAndSubClass(data.getInventoryId(), data.getSubClass());
			var fareData = invenData.getFareData();
			var allotments = fareData.stream().filter(subClass -> subClass.getSubClass().equals(data.getSubClass()))
					.mapToInt(fd -> fd.getAllotment()).sum();
			fareDataz.setAllotment(allotments + data.getSeatCount());
			inventoryRepo.saveAndFlush(invenData);
			emailHelper.sendCancellIssuedToCustomer(data);
		});
		bookDataRepo.saveAllAndFlush(datas);
	}

	@Transactional
	public void timelimitPaymentHandler() {
		var datas = waitingPaymentLockingProcess(PaymentStatus.WAITING, paymentWaitingTime);
		if (datas == null) {
			return;
		}
		datas.stream().map(bd -> bd.getBookData());
		datas.forEach(data -> {
			data.setPaymentStatus(PaymentStatus.FAIL);
			emailHelper.sendFailIssued(data.getBookData());
		});
		statementDataRepo.saveAllAndFlush(datas);
	}

	@Transactional
	private List<BookData> bookLockingProcessWithPassingTimelimit() {
		var datas = bookDataRepo.findByPassingTimelimit(LocalDateTime.now());
		if (datas.isEmpty()) {
			return null;
		}
		logger.info("[{}.bookLockingProcessWithDate] jumlah data yang melewati timelimit: {}", className, datas.size());

		datas.forEach(data -> {
			data.setIsChecking(true);
		});
		bookDataRepo.saveAllAndFlush(datas);
		return datas;
	}

	@Transactional
	private List<StatementData> waitingPaymentLockingProcess(PaymentStatus status, Integer lifetime) {
		var datas = statementDataRepo.findByPassingTimelimitAt(status, LocalDateTime.now().minusMinutes(lifetime));
		if (datas.isEmpty()) {
			return null;
		}
		logger.info("[{}.statementLockingProcess] jumlah data {}: {}", className, status, datas.size());
		statementDataRepo.saveAllAndFlush(datas);
		return datas;
	}

}
