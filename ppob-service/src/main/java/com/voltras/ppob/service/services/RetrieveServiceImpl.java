package com.voltras.ppob.service.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.manager.api.models.OfficeContactDetail;
import com.voltras.core.manager.api.models.ProductInfo;
import com.voltras.core.manager.api.models.RequiredPaymentParam;
import com.voltras.core.manager.api.models.RevenueInfo;
import com.voltras.core.manager.api.models.RevenueSummary;
import com.voltras.core.manager.api.models.TransactionSummary;
import com.voltras.core.manager.api.services.RetrieveService;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.ppob.utils.PagingUtil;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;

import jakarta.validation.constraints.NotBlank;

@Service("retrieveService")
public class RetrieveServiceImpl implements RetrieveService, RpcBasicService {
	@Autowired
	private LogHelper logger;

	@Autowired
	private BookDataRepository bookDataRepo;

	private final String className = this.getClass().getCanonicalName();

	@Override
	@Publish(allowAll = true)
	public List<TransactionSummary> getTransactions(String officeCode, Date bookingFrom, Date bookingTo,
			String productType, BookingStatus status, String bookingCode) {
		if (!productType.equalsIgnoreCase("PPOB")) {
			logger.error("[{}.getTransactions] Invalid case", className);
			return new ArrayList<>();
		}
		bookingFrom = PagingUtil.getMinRetrieveDate(bookingFrom);
		bookingTo = PagingUtil.getMaxRetrieveDate(bookingTo);
		List<BookData> retrievedDatas = bookDataRepo.listAllByOfficeCodeAndBookingDateAndBookingCode(officeCode,
				PagingUtil.getMinRetrieveDate(bookingFrom), PagingUtil.getMaxRetrieveDate(bookingTo), bookingCode);

		return retrievedDatas.stream()
				.map(retrievedData -> new TransactionSummary("PPOB", retrievedData.getProductType().toString(),
						retrievedData.getBookId().toString(), retrievedData.getBookingCode(),
						retrievedData.getBookDate(), retrievedData.getStatus(),
						retrievedData.getFinancialStatement().getTotalPrice(),
						retrievedData.getProductDetail().getDescription()))
				.collect(Collectors.toList());
	}

	@Override
	@Publish(allowAll = true)
	public RequiredPaymentParam getRequiredPaymentParam(@NotBlank String item) {
		BookData data = bookDataRepo.findById(UUID.fromString(item)).orElse(null);
		if (data == null) {
			logger.throwRuntime(className, "getRequiredPaymentParam", "Book data dengan item tersebut tidak ditemukan");
		}

		return new RequiredPaymentParam(data.getTimelimit(), data.getBookingCode(),
				data.getFinancialStatement().getTotalPrice());
	}

	@Override
	@Publish(allowAll = true)
	public Boolean checkTranscationDetail(String domain, String productType, String bookingCode, String email)
			throws DataNotFoundException {
		if (!productType.equalsIgnoreCase("PPOB")) {
			logger.error("[{}.checkTranscationDetail] productType tidak sesuai", className);
			throw new DataNotFoundException();
		}
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
	public OfficeContactDetail getOfficeContactDetail(String productType) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Publish(allowAll = true)
	public RevenueInfo getRevenue(LocalDate from, LocalDate to, String productType, String officeCode, Integer page,
			Integer pageSize) {
		if (!productType.equalsIgnoreCase("PPOB")) {
			throw new RuntimeException("PPOB uppercase only");
		}
		if (pageSize != null && pageSize > 20) {
			pageSize = 20;
		}

		Pageable pageable = page != null && pageSize != null ? PageRequest.of(page, pageSize) : Pageable.unpaged();
		var changeTo = to.atTime(LocalTime.MAX);
		var datas = bookDataRepo.findByPaymentDateAndBookingStatusAndOfficeCode(
				Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant()),
				Date.from(changeTo.atZone(ZoneId.systemDefault()).toInstant()), officeCode, pageable);
		List<RevenueSummary> revenueList = new ArrayList<RevenueSummary>();
		for (var data : datas) {
			revenueList.add(new RevenueSummary(data.getBookId().toString(),
					data.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
					data.getProductDetail().getProductCode(), data.getBookingCode(),
					data.getFinancialStatement().getSupplierCommision(), data.getFinancialStatement().getSaServiceFee(),
					0D, data.getFinancialStatement().getSupplierCommision()
							+ data.getFinancialStatement().getSaServiceFee()));
		}

		return new RevenueInfo(productType, revenueList, Integer.valueOf((int) datas.getTotalElements()));
	}

}
