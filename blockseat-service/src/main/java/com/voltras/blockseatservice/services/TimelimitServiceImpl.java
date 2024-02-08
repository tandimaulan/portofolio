package com.voltras.blockseatservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.admin.api.enums.TimelimitCondition;
import com.voltras.blockseat.admin.api.exceptions.InventoryNotFoundException;
import com.voltras.blockseat.admin.api.models.Timelimit;
import com.voltras.blockseat.admin.api.services.TimelimitService;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.entities.TimelimitData;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.blockseatservice.repositories.TimelimitDataRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.IgnoreApiVersion;
import com.voltras.voltrasspring.security.Publish;

@Service("timelimitService")
public class TimelimitServiceImpl implements TimelimitService, RpcBasicService {

	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private TimelimitDataRepository timelimitDataRepo;

	@Autowired
	LogHelper log;

	private final String className = this.getClass().getCanonicalName();

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public Timelimit create(String inventoryId, Integer from, Integer to, Integer duration,
			TimelimitCondition condition) throws InventoryNotFoundException {

		var inven = inventoryRepo.getByInventoryId(UUID.fromString(inventoryId));
		TimelimitData timelimitData = null;
		if (!UUID.fromString(inventoryId).equals(inven.getInventoryId())) {
			log.error("[{}.create timelimit] error: DataNotFoundException", className);
			throw new InventoryNotFoundException();
		}

		switch (condition) {
		case BeforeETD -> {
			timelimitData = new TimelimitData(UUID.fromString(inventoryId), from, to, duration,
					TimelimitCondition.BeforeETD);
			timelimitDataRepo.saveAndFlush(timelimitData);
		}
		case AfterBookingInDay -> {
			timelimitData = new TimelimitData(UUID.fromString(inventoryId), from, to, duration,
					TimelimitCondition.AfterBookingInDay);
			timelimitDataRepo.saveAndFlush(timelimitData);
		}
		case AfterBookingInMinute -> {
			timelimitData = new TimelimitData(UUID.fromString(inventoryId), from, to, duration,
					TimelimitCondition.AfterBookingInMinute);
			timelimitDataRepo.saveAndFlush(timelimitData);
		}

		}
		return new Timelimit(timelimitData.getId(), timelimitData.getDayFrom(), timelimitData.getDayTo(),
				timelimitData.getDuration(), timelimitData.getCondition());
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public Timelimit edit(String timelimitId, String inventoryId, Integer from, Integer to, Integer duration,
			TimelimitCondition condition) throws InventoryNotFoundException {
		var inven = inventoryRepo.getByInventoryId(UUID.fromString(inventoryId));
		Timelimit timelimit = null;
		if (!UUID.fromString(inventoryId).equals(inven.getInventoryId())) {
			log.error("[{}.edit timelimit] error: InventoryNotFoundException", className);
			throw new InventoryNotFoundException();
		} else {
			var timelimitDatas = timelimitDataRepo.findById(UUID.fromString(timelimitId));
			var dataz = timelimitDatas.get();
			if (!dataz.getId().equals(UUID.fromString(timelimitId))) {
				log.error("[{}.edit timelimit] error: DataNotFoundException", className);
			} else {
				TimelimitData timelimitData = null;
				switch (condition) {
				case BeforeETD -> {
					timelimitData = new TimelimitData(UUID.fromString(timelimitId), UUID.fromString(inventoryId), from,
							to, duration, TimelimitCondition.BeforeETD);
				}
				case AfterBookingInDay -> {
					timelimitData = new TimelimitData(UUID.fromString(timelimitId), UUID.fromString(inventoryId), from,
							to, duration, TimelimitCondition.AfterBookingInDay);
				}
				case AfterBookingInMinute -> {
					timelimitData = new TimelimitData(UUID.fromString(timelimitId), UUID.fromString(inventoryId), from,
							to, duration, TimelimitCondition.AfterBookingInMinute);
				}

				}
				timelimitDataRepo.saveAndFlush(timelimitData);
			}

			var data = timelimitDataRepo.findById(UUID.fromString(timelimitId));
			if (!data.isEmpty()) {
				var datas = data.get();
				timelimit = new Timelimit(datas.getId(), datas.getDayFrom(), datas.getDayTo(), datas.getDuration(),
						datas.getCondition());
			}

		}
		return timelimit;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<Timelimit> getAll(String inventoryId) {
		List<Timelimit> timelimit = new ArrayList<>();
		var data = timelimitDataRepo.findByInventoryId(UUID.fromString(inventoryId));
		for (var datas : data) {
			var timelimitData = new Timelimit(datas.getId(), datas.getDayFrom(), datas.getDayTo(), datas.getDuration(),
					datas.getCondition());
			timelimit.add(timelimitData);
		}
		return timelimit;
	}

}
