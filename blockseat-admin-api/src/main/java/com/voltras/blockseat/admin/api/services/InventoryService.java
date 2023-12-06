package com.voltras.blockseat.admin.api.services;

import java.time.LocalDateTime;
import java.util.List;

import com.voltras.blockseat.admin.api.exceptions.InventoryNotFoundException;
import com.voltras.blockseat.admin.api.models.BlockseatDataReport;
import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.Fare;
import com.voltras.blockseat.api.models.JourneyOption;
import com.voltras.blockseat.api.models.Person;
import com.voltras.blockseat.api.models.Pnr;
import com.voltras.blockseat.api.models.Supplier;

/**
 * InventoryService
 */
public interface InventoryService {

	/**
	 * @return {@link List} <{@link JourneyOption> getAll Inventory
	 * 
	 */
	List<JourneyOption> getAll();

	/**
	 * @param title            new Inventory name
	 * @param tags             new Inventory tags
	 * @param notes            new Inventory notes
	 * @param airlineName      new Inventory airlineName
	 * @param seatCapacity     new Inventory seatCapacity
	 * @param minSeatBooking   new Inventory minSeatBooking
	 * @param cabinClass       new Inventory cabinClass
	 * @param fare             new Inventory fare
	 * @param downPaymentPrice new Inventory downPaymentPrice
	 * @param isAvailable      new Inventory isAvailable
	 * @param supplier         new Inventory supplier
	 * 
	 * @return {@link List} <{@link JourneyOption> create Inventory
	 * 
	 */
	List<JourneyOption> create(String title, String tags, String notes, String airlineName, Integer seatCapacity,
			Integer minSeatBooking, CabinClass cabinClass, List<Fare> fare, Double downPaymentPrice,
			Boolean isAvailable, Supplier supplier);

	/**
	 * @param inventoryId      id of Inventory that its attribute will be edited
	 * @param title            edit Inventory name
	 * @param tags             edit Inventory tags
	 * @param notes            edit Inventory notes
	 * @param airlineName      edit Inventory airlineName
	 * @param seatCapacity     edit Inventory seatCapacity
	 * @param minSeatBooking   edit Inventory minSeatBooking
	 * @param cabinClass       edit Inventory cabinClass
	 * @param downPaymentPrice edit Inventory downPaymentPrice
	 * @param isAvailable      edit Inventory isAvailable
	 * @param supplier         edit Inventory supplier
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link List} <{@link JourneyOption> edit Inventory
	 * 
	 */
	List<JourneyOption> edit(String inventoryId, String title, String tags, String notes, String airlineName,
			Integer seatCapacity, Integer minSeatBooking, CabinClass cabinClass, Double downPaymentPrice,
			Boolean isAvailable, Supplier supplier) throws InventoryNotFoundException;

	/**
	 * @param inventoryId    id of Inventory that its attribute will be edited
	 * @param minSeatBooking new Inventory minimumSeat
	 * 
	 * @throws InventoryNotFoundException Thrown if inventory data not found
	 * 
	 * @return {@link List} <{@link JourneyOption> editMinimumSeat Inventory
	 * 
	 */
	List<JourneyOption> editMinimumSeat(String inventoryId, Integer minimumSeat) throws InventoryNotFoundException;

	/**
	 * @param inventoryId    id of Inventory whose attribute is to look for a Pnr
	 *                       that uses inventoryId
	 * 
	 * @return {@link List} <{@link Pnr> getGroupNameList Pnr
	 * 
	 */
	List<Pnr> getGroupNameList(String inventoryId);

	/**
	 * @param inventoryId    id of Inventory whose attribute is to look for a Person
	 *                       that uses inventoryId
	 * 
	 * @return {@link List} <{@link Person> getGroupNameList Pnr
	 * 
	 */
	List<Person> getPassengerNameList(String inventoryId);

	/**
	 * @param from    	date for data
	 * @param to 		date for data
	 * 
	 * @return {@link List} <{@link Person> getDataReportList BlockseatDataReport
	 * 
	 */
	List<BlockseatDataReport> getDataReportList(LocalDateTime from, LocalDateTime to);

	/**
	 * @param from    	date for data
	 * @param to 		date for data
	 * 
	 * @return {@link String} downloadListData String
	 * 
	 */
	String downloadListData(LocalDateTime from, LocalDateTime to);
}
