
package com.voltras.blockseat.admin.api.services;

import java.util.List;

import com.voltras.blockseat.admin.api.exceptions.InventoryNotFoundException;
import com.voltras.blockseat.api.models.FlightSegment;

/**
 * SegmentService
 */
public interface SegmentService {

	/**
	 * inventoryId 				id of Inventory whose attribute is to look for a FlightSegment that uses inventoryId
	 * 
	 * @return {@link List} <{@link FareDetail> getAll Fare
	 * 
	 */
	List<FlightSegment> getAll(String inventoryId);

	/**
	 * inventoryId 				id of Inventory that its attribute will be created
	 * flightSegment			FlightSegment parameter for createSegment
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link List} <{@link FlightSegment> createSegment FlightSegment
	 * 
	 */
	List<FlightSegment> createSegment(String inventoryId, FlightSegment flightSegment)
			throws InventoryNotFoundException;

	/**
	 * flightSegment 			FlightSegment parameter for editSegment
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link List} <{@link FlightSegment> editSegment FlightSegment
	 * 
	 */
	List<FlightSegment> editSegment(FlightSegment flightSegment) throws InventoryNotFoundException;

}