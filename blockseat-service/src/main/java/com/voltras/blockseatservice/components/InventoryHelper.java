package com.voltras.blockseatservice.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.voltras.blockseat.admin.api.models.BlockseatDataReport;
import com.voltras.blockseat.api.models.Airport;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseat.api.models.FlightSummary;
import com.voltras.blockseatservice.entities.DataReport;
import com.voltras.blockseatservice.entities.FlightData;
import com.voltras.blockseatservice.repositories.BlockseatDataReportRepository;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.FlightDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

@Component
public class InventoryHelper {

	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private FlightDataRepository flightDataRepo;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private BlockseatDataReportRepository reportRepo;

	@Autowired
	private VanAdditionalRequestData session;

	public List<FlightSegment> getFlightSegement(UUID inventoryId) {
		List<FlightSegment> flightSegment = new ArrayList<>();

		var segmentData = flightDataRepo.findByInventoryIdOrderBySegmentNumAsc(inventoryId);
		for (var flightSegments : segmentData) {
			var segmentDetails = flightSegments.getSegmentDetails();
			var newSegment = new FlightSegment(segmentDetails.segmentId(), segmentDetails.isDepart(),
					segmentDetails.departure(), segmentDetails.arrival(), segmentDetails.flightDuration(),
					segmentDetails.operatingAirline(), segmentDetails.flightNumber(), segmentDetails.stop(),
					segmentDetails.equipment(), segmentDetails.transitDuration());
			flightSegment.add(newSegment);
		}
		return flightSegment;
	}

	public FlightSummary getFlightSummary(UUID inventoryId) {
		FlightSummary summary = null;
		var inventoryData = inventoryRepo.getByInventoryId(inventoryId);
		var segmentData = flightDataRepo.findByInventoryIdOrderBySegmentNumAsc(inventoryId);
		for (var segments : segmentData) {
			List<FlightSegment> segment = getFlightSegement(segments.getInventoryId());
			List<Airport> transitPoints = new ArrayList<>();
			if (!segments.getSegmentNum().equals(0)) {
				var airport = new Airport(segments.getSegmentDetails().departure().iata(),
						segments.getSegmentDetails().departure().name(),
						segments.getSegmentDetails().departure().city(),
						segments.getSegmentDetails().departure().country());
				transitPoints.add(airport);
			}
			List<String> operatingAirline = new ArrayList<>();
			for (var opAirline : segment) {
				operatingAirline.add(opAirline.operatingAirline());
			}
			operatingAirline = operatingAirline.stream().distinct().collect(Collectors.toList());

			var travelTime = segment.stream()
					.mapToInt(time -> Integer.sum(time.flightDuration(), time.transitDuration())).sum();

			var stop = 0;
			stop = stop + segments.getSegmentDetails().stop();

			summary = new FlightSummary(segmentData.get(0).getSegmentDetails().departure(),
					segments.getSegmentDetails().arrival(), travelTime, operatingAirline,
					stop + inventoryData.getFlightData().size() - 1, transitPoints);
		}
		return summary;
	}

	public List<BlockseatDataReport> getDataForReport(LocalDateTime from, LocalDateTime to) {
		List<BlockseatDataReport> dataReport = new ArrayList<>();

		var bookDataAll = bookDataRepo.findAll();
		if (!session.getUser().getUserName().equals("2")) {
			BlockseatDataReport report = null;
			var inventoryData = inventoryRepo.findAll();
			for (var inventoryDatas : inventoryData) {
				var flightData = inventoryDatas.getFlightData();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				flightData.sort(Comparator.comparing(FlightData::getSegmentNum));
				LocalDateTime departureDate = flightData.get(0).getSegmentDetails().departure().time();
				Integer durationDays = 0;
				var route = "";
				LocalDateTime arrivalDate = null;
				for (var flightDatas : flightData) {
					route = flightData.get(0).getSegmentDetails().departure().iata() + "-"
							+ flightData.get(0).getSegmentDetails().arrival().iata() + ","
							+ flightDatas.getSegmentDetails().departure().iata() + "-"
							+ flightDatas.getSegmentDetails().arrival().iata();
					arrivalDate = flightDatas.getSegmentDetails().arrival().time();

					durationDays = (int) ChronoUnit.DAYS.between(
							LocalDate.parse(departureDate.toLocalDate().format(formatter)),
							LocalDate.parse(arrivalDate.toLocalDate().format(formatter))) + 1;
				}

				Integer totalSeatOpenActive = inventoryData.stream()
						.filter(inven -> inven.getIsAvailable().equals(true))
						.mapToInt(seatOpenAll -> seatOpenAll.getSeatAllot()).sum();
				Integer totalSeatOpenInActive = inventoryData.stream()
						.filter(inven -> inven.getIsAvailable().equals(false))
						.mapToInt(seatOpenAll -> seatOpenAll.getSeatAllot()).sum();

				Integer totalSupplier = inventoryData.stream().mapToInt(supplier -> supplier.getSupplierData().size())
						.distinct().sum();
				Integer allSeatCapacity = inventoryData.stream()
						.mapToInt(seatCapacityAll -> seatCapacityAll.getSeatCapacity()).sum();

				Integer seatOpenPerInventory = inventoryDatas.getSeatAllot();
				var bookDataz = bookDataRepo.findByInventoryIdAndBookDateAndStatus(inventoryDatas.getInventoryId(),
						from, to.plusDays(1));
				// count total status per bookData
				Integer countStatusNew = bookDataz.stream().filter(sn -> sn.getStatus().equals(BookingStatus.NEW))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();
				Integer countStatusBooked = (int) bookDataz.stream()
						.filter(sb -> sb.getStatus().equals(BookingStatus.RESERVED))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();
				Integer countStatusConfirm = (int) bookDataz.stream()
						.filter(sc -> sc.getStatus().equals(BookingStatus.CONFIRMED))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();

				// count total status all bookData
				var totalsStatusNew = bookDataAll.stream().filter(sn -> sn.getStatus().equals(BookingStatus.NEW))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();
				var totalsStatusBooked = bookDataAll.stream()
						.filter(sb -> sb.getStatus().equals(BookingStatus.RESERVED))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();
				var totalsStatusConfirm = bookDataAll.stream()
						.filter(sc -> sc.getStatus().equals(BookingStatus.CONFIRMED))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();

				Double totalPayment = bookDataz.stream().mapToDouble(totalPay -> totalPay.getTotalPayment()).sum();
				Double totalRevenue = bookDataAll.stream()
						.filter(totalRev -> totalRev.getStatus().equals(BookingStatus.CONFIRMED))
						.mapToDouble(tr -> tr.getTotalPayment()).sum();
				String supplierEmail = "";
				for (var supplier : inventoryDatas.getSupplierData()) {
					supplierEmail = supplier.getSupplierEmail();
				}
				reportRepo.saveAllAndFlush(List
						.of(new DataReport(supplierEmail, inventoryDatas.getTitle(), durationDays, route, departureDate,
								arrivalDate, inventoryDatas.getSeatCapacity(), countStatusNew, countStatusBooked,
								countStatusConfirm, totalsStatusNew, totalsStatusBooked, totalsStatusConfirm,
								totalPayment, totalRevenue, totalSupplier, allSeatCapacity, seatOpenPerInventory)));

				report = new BlockseatDataReport(supplierEmail, inventoryDatas.getTitle(), durationDays, route,
						departureDate, arrivalDate, inventoryDatas.getSeatCapacity(), countStatusNew, countStatusBooked,
						countStatusConfirm, totalsStatusNew, totalsStatusBooked, totalsStatusConfirm, totalPayment,
						totalRevenue, totalSupplier, allSeatCapacity, seatOpenPerInventory, totalSeatOpenActive,
						totalSeatOpenInActive);
				dataReport.add(report);
			}

		} else {
			String supplierEmails = session.getUser().getPrincipal();
			var inventoryData = inventoryRepo.findBySupplierDataSupplierEmail(supplierEmails);
			BlockseatDataReport reports = null;
			for (var inventoryDatas : inventoryData) {
				var flightData = inventoryDatas.getFlightData();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				flightData.sort(Comparator.comparing(FlightData::getSegmentNum));
				LocalDateTime departureDate = flightData.get(0).getSegmentDetails().departure().time();
				Integer durationDays = 0;
				var route = "";
				LocalDateTime arrivalDate = null;
				for (var flightDatas : flightData) {
					route = flightData.get(0).getSegmentDetails().departure().iata() + "-"
							+ flightData.get(0).getSegmentDetails().arrival().iata() + ","
							+ flightDatas.getSegmentDetails().departure().iata() + "-"
							+ flightDatas.getSegmentDetails().arrival().iata();
					arrivalDate = flightDatas.getSegmentDetails().arrival().time();

					durationDays = (int) ChronoUnit.DAYS.between(
							LocalDate.parse(departureDate.toLocalDate().format(formatter)),
							LocalDate.parse(arrivalDate.toLocalDate().format(formatter))) + 1;
				}
				Integer allSeatCapacity = inventoryData.stream().mapToInt(inventory -> inventory.getSeatCapacity())
						.sum();
				Integer totalSeatOpenActive = inventoryData.stream()
						.filter(inven -> inven.getIsAvailable().equals(true))
						.mapToInt(seatOpenAll -> seatOpenAll.getSeatAllot()).sum();
				Integer totalSeatOpenInActive = inventoryData.stream()
						.filter(inven -> inven.getIsAvailable().equals(false))
						.mapToInt(seatOpenAll -> seatOpenAll.getSeatAllot()).sum();
				var bookDataz = bookDataRepo.findByInventoryIdAndBookDateAndStatus(inventoryDatas.getInventoryId(),
						from, to.plusDays(1));

				Integer countStatusNew = bookDataz.stream().filter(sn -> sn.getStatus().equals(BookingStatus.NEW))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();
				Integer countStatusBooked = (int) bookDataz.stream()
						.filter(sb -> sb.getStatus().equals(BookingStatus.RESERVED))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();
				Integer countStatusConfirm = (int) bookDataz.stream()
						.filter(sc -> sc.getStatus().equals(BookingStatus.CONFIRMED))
						.mapToInt(seatCount -> seatCount.getSeatCount()).sum();

				Double totalPayment = bookDataz.stream().mapToDouble(totalPay -> totalPay.getTotalPayment()).sum();
				Double totalRevenue = bookDataz.stream()
						.filter(totalRev -> totalRev.getStatus().equals(BookingStatus.CONFIRMED))
						.mapToDouble(tr -> tr.getTotalPayment()).sum();
				reportRepo.saveAllAndFlush(List.of(new DataReport(supplierEmails, inventoryDatas.getTitle(),
						durationDays, route, departureDate, arrivalDate, inventoryDatas.getSeatCapacity(),
						countStatusNew, countStatusBooked, countStatusConfirm, countStatusNew, countStatusBooked,
						countStatusConfirm, totalPayment, totalRevenue, inventoryDatas.getSupplierData().size(),
						allSeatCapacity, inventoryDatas.getSeatAllot())));

				reports = new BlockseatDataReport(supplierEmails, inventoryDatas.getTitle(), durationDays, route,
						departureDate, arrivalDate, inventoryDatas.getSeatCapacity(), countStatusNew, countStatusBooked,
						countStatusConfirm, countStatusNew, countStatusBooked, countStatusConfirm, totalPayment,
						totalRevenue, inventoryDatas.getSupplierData().size(), allSeatCapacity,
						inventoryDatas.getSeatAllot(), totalSeatOpenActive, totalSeatOpenInActive);
				dataReport.add(reports);
			}
		}
		dataReport.sort(Comparator.comparing(BlockseatDataReport::departureDate));
		return dataReport;
	}
}
