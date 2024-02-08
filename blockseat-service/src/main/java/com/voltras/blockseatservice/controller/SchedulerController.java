package com.voltras.blockseatservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voltras.blockseatservice.configuration.AsyncSchedulerExecutor;
import com.voltras.blockseatservice.services.SchedulerService;

@RestController
public class SchedulerController {
	@Autowired
	private SchedulerService scheduler;
	@Autowired
	private AsyncSchedulerExecutor executor;

	@PostMapping("/scheduler")
	public void invoke() {
		for (var method : scheduler.getClass().getSuperclass().getMethods()) {
			if (method.getAnnotation(Transactional.class) != null) {
				executor.submit(scheduler, method);
			}
		}
	}
}
