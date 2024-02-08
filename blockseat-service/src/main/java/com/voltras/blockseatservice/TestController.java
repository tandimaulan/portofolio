package com.voltras.blockseatservice;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voltras.blockseatservice.repositories.InventoryRepository;

@RestController
public class TestController {
	@Autowired
	InventoryRepository inventoryRepo;

	@PostMapping("/test")
	public Boolean test(@RequestParam String id) {
		inventoryRepo.deleteById(UUID.fromString(id));
		return true;
	}

	@PostMapping("/deleteAll")
	public Boolean deleteAll() {
		inventoryRepo.deleteAll();
		return true;
	}
}
