package com.voltras.blockseatservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.api.exceptions.SeatNotAvailableException;
import com.voltras.blockseat.api.models.FareDetail;
import com.voltras.blockseat.api.services.BlockseatFareService;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.entities.FareData;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.FareDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.IgnoreApiVersion;
import com.voltras.voltrasspring.security.Publish;

@Service("blockseatFareService")
public class FareServiceImpl implements RpcBasicService, BlockseatFareService,
		com.voltras.blockseat.admin.api.services.BlockseatFareService {

	@Autowired
	private FareDataRepository fareRepo;
	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private BookDataRepository bookDataRepo;

	@Autowired
	LogHelper log;

	@Override
	@Publish(allowAll = true)
	public FareDetail calculateTotalPrice(String inventoryId, Integer paxCount, String subClass)
			throws SeatNotAvailableException {
		var fare = fareRepo.findByInventoryIdAndSubClass(UUID.fromString(inventoryId), subClass);
		var inventory = inventoryRepo.getByInventoryId(UUID.fromString(inventoryId));
		FareDetail fareDetail = null;

		if (fare.getSubClass() != null) {
			Double price = 0D;
			Double totalPrice = 0D;
			Double downPayment = inventory.getDownPaymentPrice();
			Double totalDownPayment = 0D;
			price = fare.getPrice();
			totalPrice = price * paxCount;
			totalDownPayment = paxCount * downPayment;
			fareDetail = new FareDetail(price, totalPrice, downPayment, totalDownPayment);
		} else {
			throw new SeatNotAvailableException();
		}

		return fareDetail;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public com.voltras.blockseat.admin.api.models.FareDetail create(String inventoryId, String subClass, Double fare,
			Integer allotment, Boolean isActive) {
		var data = fareRepo.findByInventoryId(UUID.fromString(inventoryId));
		com.voltras.blockseat.admin.api.models.FareDetail fareDetail = null;
		FareData fareData = null;
		for (var datas : data) {
			if (datas.getSubClass().equals(subClass)) {
				throw new RuntimeException();
			} else {
				fareData = new FareData(UUID.fromString(inventoryId), subClass, fare, allotment);
			}
		}
		fareRepo.saveAndFlush(fareData);
		fareDetail = new com.voltras.blockseat.admin.api.models.FareDetail(fareData.getId(), fareData.getSubClass(),
				fareData.getPrice(), fareData.getAllotment(), 0, 0, fareData.getIsActive());
		return fareDetail;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public com.voltras.blockseat.admin.api.models.FareDetail edit(String fareId, String inventoryId, String subClass,
			Double fare, Integer allotment, Boolean isActive) {
		var data = fareRepo.findById(UUID.fromString(fareId));
		FareData fareData = null;
		if (!data.isEmpty()) {
			var fareDatas = data.get();
			var invenData = inventoryRepo.getByInventoryId(fareDatas.getInventoryId());
			fareData = new FareData(fareDatas.getId(), fareDatas.getInventoryId(), subClass, fare, allotment,
					invenData.getDownPaymentPrice(), isActive);
			fareRepo.saveAndFlush(fareData);
		}

		return new com.voltras.blockseat.admin.api.models.FareDetail(fareData.getId(), fareData.getSubClass(),
				fareData.getPrice(), fareData.getAllotment(), fareData.getDownPayment(), fareData.getTotalPaidOff(),
				fareData.getIsActive());
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<com.voltras.blockseat.admin.api.models.FareDetail> getAll(String inventoryId) {
		List<com.voltras.blockseat.admin.api.models.FareDetail> datas = new ArrayList<com.voltras.blockseat.admin.api.models.FareDetail>();
		var fareData = fareRepo.findByInventoryId(UUID.fromString(inventoryId));
		var bookData = bookDataRepo.findByInventoryId(UUID.fromString(inventoryId));

		Integer totalPassangerDp = bookData.stream().filter(bd -> bd.getStatus().equals(BookingStatus.RESERVED))
				.mapToInt(data -> data.getPassengers().size()).sum();
		;
		Integer totalPaidOff = bookData.stream().filter(bd -> bd.getStatus().equals(BookingStatus.CONFIRMED))
				.mapToInt(data -> data.getPassengers().size()).sum();
		for (var fare : fareData) {
			var fares = new com.voltras.blockseat.admin.api.models.FareDetail(fare.getId(), fare.getSubClass(),
					fare.getPrice(), fare.getAllotment(), totalPassangerDp, totalPaidOff, fare.getIsActive());
			datas.add(fares);
		}

		return datas;
	}

}
