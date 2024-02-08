package com.voltras.blockseatservice.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.StatementDataRepository;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.manager.api.models.OfficeContactDetail;
import com.voltras.core.manager.api.models.ProductInfo;
import com.voltras.core.manager.api.models.RequiredPaymentParam;
import com.voltras.core.manager.api.models.RevenueInfo;
import com.voltras.core.manager.api.models.RevenueSummary;
import com.voltras.core.manager.api.models.TransactionSummary;
import com.voltras.core.manager.api.services.RetrieveService;
import com.voltras.voltrasspring.exceptions.exceptions.VoltrasSpringRuntimeException;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.IgnoreApiVersion;
import com.voltras.voltrasspring.security.Publish;

@Service("retrieveService")
public class RetrieveServiceImpl implements RetrieveService, RpcBasicService {

	@Autowired
	private LogHelper logger;

	@Autowired
	private BookDataRepository bookDataRepo;

	@Autowired
	private StatementDataRepository statementDataRepository;

	private final String className = this.getClass().getCanonicalName();

	@Override
	public List<ProductInfo> getAllProductInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String printTransactions(Date bookingFrom, Date bookingTo, String productType, BookingStatus status,
			String bookingCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public RequiredPaymentParam getRequiredPaymentParam(@NotBlank String item) {
		var statementData = statementDataRepository.findById(UUID.fromString(item))
				.orElseThrow(() -> new VoltrasSpringRuntimeException(
						new RuntimeException("Book data dengan item tersebut tidak ditemukan")));
//		statementData.get
		return new RequiredPaymentParam(
				Date.from(statementData.getTimelimitAt().atZone(ZoneId.systemDefault()).toInstant()),
				statementData.getBookData().getBookingCode(), statementData.getTotalPayment());
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public Boolean checkTranscationDetail(String domain, String productType, String bookingCode, String email)
			throws DataNotFoundException {
		var data = bookDataRepo.getSpecificWithCommonParam(domain, bookingCode, email);
		if (data.isEmpty()) {
			logger.error("[{}.checkTranscationDetail] Data not found", className);
			throw new DataNotFoundException();
		}
		if (!data.get().getStatus().equals(BookingStatus.CONFIRMED)) {
			logger.error("[{}.checkTranscationDetail] Percobaan dengan status tidak CONFIRMED pada bookingCode:{}",
					className, bookingCode);
			throw new DataNotFoundException();
		}
		return true;
	}

	@Override
	public List<TransactionSummary> getTransactions(String officeCode, Date bookingFrom, Date bookingTo,
			String productType, BookingStatus status, String bookingCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OfficeContactDetail getOfficeContactDetail(String productType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Publish(allowAll = true)
	public RevenueInfo getRevenue(LocalDate from, LocalDate to, String productType, String officeCode, Integer page,
			Integer pageSize) {
		if (!productType.equalsIgnoreCase("BLOCKSEAT")) {
			throw new RuntimeException("BLOCKSEAT uppercase only");
		}
		if (pageSize != null && pageSize > 20) {
			pageSize = 20;
		}

		Pageable pageable = page != null && pageSize != null ? PageRequest.of(page, pageSize) : Pageable.unpaged();
		var datas = bookDataRepo.findByCreatedAtAndBookingStatusAndOfficeCode(from.atStartOfDay(),
				to.atTime(LocalTime.MAX), officeCode, pageable);

		List<RevenueSummary> revenueList = new ArrayList<RevenueSummary>();

		if (datas.isEmpty()) {
			return new RevenueInfo(productType, List.of(), datas.getNumberOfElements());
		}

		for (var data : datas) {
			for (var statement : data.getStatement()) {
				revenueList.add(
						new RevenueSummary(data.getId().toString(), statement.getCreatedAt(), data.getBlockseatName(),
								data.getBookingCode(), data.getFinancialStatement().getSupplierCommision(), 0D, 0D,
								data.getFinancialStatement().getSupplierCommision() + 0D));
			}
		}
		return new RevenueInfo(productType, revenueList, Integer.valueOf((int) datas.getTotalElements()));
	}

}
