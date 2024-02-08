package com.voltras.ppob.service.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.payment.notification.api.models.ReversalDetail;
import com.voltras.payment.notification.api.services.ReversalService;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.repositories.ReversalDataRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;

@Service("reversalService")
public class ReversalServiceImpl implements ReversalService, RpcBasicService {
	@Autowired
	private LogHelper logger;

	@Autowired
	private ReversalDataRepository repo;

	@Override
	@Publish(allowAll = true)
	public List<ReversalDetail> pull() {
		var datas = repo.findAllNullActionDate();
		logger.info("[{}.pull] size: {}", datas.size());
		datas.forEach(data -> data.setActionDate(new Date()));
		repo.saveAll(datas);
		return datas.stream()
				.map(data -> new ReversalDetail(data.getGoblinAccountId(), data.getTransactionId(), "PPOB"))
				.toList();

	}

}
