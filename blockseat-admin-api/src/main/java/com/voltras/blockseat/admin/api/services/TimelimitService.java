package com.voltras.blockseat.admin.api.services;

import java.util.List;

import com.voltras.blockseat.admin.api.enums.TimelimitCondition;
import com.voltras.blockseat.admin.api.exceptions.InventoryNotFoundException;
import com.voltras.blockseat.admin.api.models.Timelimit;

/**
 * TimelimitService
 */
public interface TimelimitService {

	/**
	 * inventoryId id of Inventory whose attribute is to look for a Timelimit that uses inventoryId
	 * 
	 * @return {@link List} <{@link FareDetail> getAll Fare
	 * 
	 */
	List<Timelimit> getAll(String inventoryId);

	/**
	 * @param inventoryId 		id of Inventory that its attribute will be created Timelimit
	 * @param dayFrom       	new Timelimit dayFrom
	 * @param dayTo    			new Timelimit dayTo
	 * @param duration       	new Timelimit duration
	 * @param condition       	new Timelimit condition
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link Timelimit} create Timelimit
	 * 
	 */
	Timelimit create(String inventoryId, Integer dayFrom, Integer dayTo, Integer duration, TimelimitCondition condition)
			throws InventoryNotFoundException;
	
	/**
	 * @param inventoryId 		id of Inventory that its attribute will be edit Timelimit
	 * @param dayFrom       	edit Timelimit dayFrom
	 * @param dayTo    			edit Timelimit dayTo
	 * @param duration       	edit Timelimit duration
	 * @param condition       	edit Timelimit condition
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link Timelimit} edit Timelimit
	 * 
	 */
	Timelimit edit(String timelimitId, String inventoryId, Integer dayFrom, Integer dayTo, Integer duration,
			TimelimitCondition condition) throws InventoryNotFoundException;

}