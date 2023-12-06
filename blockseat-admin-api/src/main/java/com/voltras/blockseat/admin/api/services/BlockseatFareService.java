package com.voltras.blockseat.admin.api.services;

import java.util.List;

import com.voltras.blockseat.admin.api.exceptions.InventoryNotFoundException;
import com.voltras.blockseat.admin.api.models.FareDetail;
import com.voltras.core.common.api.exceptions.DataNotFoundException;

/**
 * FareService
 */
public interface BlockseatFareService {

	/**
	 * inventoryId id of Inventory whose attribute is to look for a Fare that uses inventoryId
	 * 
	 * @return {@link List} <{@link FareDetail> getAll Fare
	 * 
	 */
	List<FareDetail> getAll(String inventoryId);

	/**
	 * @param inventoryId id of Inventory that its attribute will be created
	 * @param subClass       	new Fare subClass
	 * @param fare    			new Fare price/fare
	 * @param allotment       	new Fare allotment/seaCapacity
	 * @param isActive       	new Fare isActive
	 * 
	 * @throws DataNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link FareDetail} create FareDetail
	 * 
	 */
	FareDetail create(String inventoryId, String subClass, Double fare, Integer allotment, Boolean isActive)
			throws DataNotFoundException;

	/**
	 * @param fareId id of FareDetail that its attribute will be edited
	 * @param inventoryId id of Inventory that its attribute will be edited
	 * @param subClass       	new Fare subClass
	 * @param fare    			new Fare price/fare
	 * @param allotment       	new Fare allotment/seaCapacity
	 * @param isActive       	new Fare isActive
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link FareDetail} edit FareDetail
	 * 
	 */
	FareDetail edit(String fareId, String inventoryId, String subClass, Double fare, Integer allotment,
			Boolean isActive) throws InventoryNotFoundException;
}