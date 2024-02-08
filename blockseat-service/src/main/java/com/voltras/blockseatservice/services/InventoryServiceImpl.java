package com.voltras.blockseatservice.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.admin.api.models.BlockseatDataReport;
import com.voltras.blockseat.admin.api.services.InventoryService;
import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.Contact;
import com.voltras.blockseat.api.models.Fare;
import com.voltras.blockseat.api.models.Identity;
import com.voltras.blockseat.api.models.JourneyOption;
import com.voltras.blockseat.api.models.Person;
import com.voltras.blockseat.api.models.Pnr;
import com.voltras.blockseat.api.models.Statement;
import com.voltras.blockseat.api.models.Supplier;
import com.voltras.blockseatservice.components.InventoryHelper;
import com.voltras.blockseatservice.entities.FareData;
import com.voltras.blockseatservice.entities.Inventory;
import com.voltras.blockseatservice.entities.SupplierData;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.FareDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.blockseatservice.repositories.SupplierDataRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.IgnoreApiVersion;
import com.voltras.voltrasspring.security.Publish;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService, RpcBasicService {
	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private FareDataRepository fareRepo;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private SupplierDataRepository supplierRepo;

	@Autowired
	private InventoryHelper inventoryHelper;

	@Autowired
	private VanAdditionalRequestData session;

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<JourneyOption> getAll() {
		List<JourneyOption> journeyOptions = new ArrayList<>();
		if (!session.getUser().getUserName().equals("2")) {
			var inventoryData = inventoryRepo.findAll();
			for (var data : inventoryData) {
				var segment = inventoryHelper.getFlightSegement(data.getInventoryId());
				var summary = inventoryHelper.getFlightSummary(data.getInventoryId());

				var fare = data.getFareData().stream().map(dataz -> new Fare(dataz.getSubClass(), dataz.getPrice(),
						dataz.getAllotment(), dataz.getIsActive())).collect(Collectors.toList());
				List<Supplier> supplierData = data.getSupplierData().stream()
						.map(sd -> new Supplier(sd.getId(), sd.getSupplierName(), sd.getSupplierEmail()))
						.collect(Collectors.toList());

				journeyOptions.add(new JourneyOption(data.getInventoryId().toString(), data.getAirlineName(),
						data.getSeatCapacity(), fare, data.getMinSeatBooking(), data.getCabinClass(), summary, segment,
						List.of(data.getTitle(), data.getNotes()), data.getSearchTag(), data.getDownPaymentPrice(),
						supplierData, data.getIsAvailable()));
			}
		}
		var inventoryData = inventoryRepo.findBySupplierDataSupplierEmail(session.getUser().getPrincipal());
		for (var data : inventoryData) {
			var segment = inventoryHelper.getFlightSegement(data.getInventoryId());
			var summary = inventoryHelper.getFlightSummary(data.getInventoryId());

			var fare = data.getFareData().stream().map(
					dataz -> new Fare(dataz.getSubClass(), dataz.getPrice(), dataz.getAllotment(), dataz.getIsActive()))
					.collect(Collectors.toList());
			List<Supplier> supplierData = data.getSupplierData().stream()
					.map(sd -> new Supplier(sd.getId(), sd.getSupplierName(), sd.getSupplierEmail()))
					.collect(Collectors.toList());

			journeyOptions.add(new JourneyOption(data.getInventoryId().toString(), data.getAirlineName(),
					data.getSeatCapacity(), fare, data.getMinSeatBooking(), data.getCabinClass(), summary, segment,
					List.of(data.getTitle(), data.getNotes()), data.getSearchTag(), data.getDownPaymentPrice(),
					supplierData, data.getIsAvailable()));

		}

		return journeyOptions.stream().distinct().toList();
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<JourneyOption> create(String title, String tags, String notes, String airlineName, Integer seatCapacity,
			Integer minSeatBooking, CabinClass cabinClass, List<Fare> fare, Double downPaymentPrice,
			Boolean isAvailable, Supplier supplier) {
		List<JourneyOption> journeyOptions = new ArrayList<>();

		var createdBy = session.getUser().getPrincipal();
		var createdAt = new Date();
		var data = new Inventory(seatCapacity, airlineName, seatCapacity, notes, minSeatBooking, tags, title,
				downPaymentPrice, createdBy, createdAt, null, null, cabinClass);
		data.setIsAvailable(true);
		inventoryRepo.saveAndFlush(data);

		var supplierDatas = new SupplierData(data.getInventoryId(), supplier.supplierEmail(), supplier.supplierEmail());
		supplierRepo.saveAndFlush(supplierDatas);

		var fareDatas = fare.stream().map(fareData -> new FareData(data.getInventoryId(), fareData.subClass(),
				fareData.price(), fareData.seatCapacity())).distinct().collect(Collectors.toList());
		fareRepo.saveAllAndFlush(fareDatas);

		var fares = fareDatas.stream().map(
				dataz -> new Fare(dataz.getSubClass(), dataz.getPrice(), dataz.getAllotment(), dataz.getIsActive()))
				.collect(Collectors.toList());

		List<Supplier> suppliers = List.of(
				new Supplier(supplierDatas.getId(), supplierDatas.getSupplierName(), supplierDatas.getSupplierEmail()));
		List<String> note = Arrays.asList(data.getTitle(), data.getNotes());

		var journeyData = new JourneyOption(data.getInventoryId().toString(), data.getAirlineName(),
				data.getSeatCapacity(), fares, data.getMinSeatBooking(), data.getCabinClass(), null, null, note,
				data.getSearchTag(), data.getDownPaymentPrice(), suppliers, data.getIsAvailable());
		journeyOptions.add(journeyData);

		return journeyOptions;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<JourneyOption> edit(String inventoryId, String title, String tags, String notes, String airlineName,
			Integer seatCapacity, Integer minSeatBooking, CabinClass cabinClass, Double downPaymentPrice,
			Boolean isAvailable, Supplier supplier) {
		List<JourneyOption> journeyOptions = new ArrayList<>();
		var updatedBy = session.getUser().getPrincipal();
		var updatedAt = new Date();
		var data = inventoryRepo.getByInventoryId(UUID.fromString(inventoryId));
		var segment = inventoryHelper.getFlightSegement(data.getInventoryId());
		var summary = inventoryHelper.getFlightSummary(data.getInventoryId());

		Inventory inventoryDatas = new Inventory(UUID.fromString(inventoryId), airlineName, seatCapacity, seatCapacity,
				notes, minSeatBooking, tags, title, isAvailable, downPaymentPrice, data.getCreateBy(),
				data.getCreatedAt(), updatedBy, updatedAt, cabinClass, data.getFlightData(), data.getFareData(),
				data.getTimelimitData());
		var fare = inventoryDatas.getFareData().stream()
				.map(fd -> new Fare(fd.getSubClass(), fd.getPrice(), fd.getAllotment(), fd.getIsActive()))
				.collect(Collectors.toList());

		inventoryDatas.setSupplierData(data.getSupplierData().stream().map(sd -> new SupplierData(sd.getId(),
				sd.getInventoryId(), supplier.supplierEmail(), supplier.supplierEmail())).collect(Collectors.toList()));

		List<Supplier> suppliers = data.getSupplierData().stream()
				.map(sd -> new Supplier(sd.getId(), supplier.supplierEmail(), supplier.supplierEmail()))
				.collect(Collectors.toList());

		JourneyOption journeyData = new JourneyOption(data.getInventoryId().toString(), data.getAirlineName(),
				data.getSeatCapacity(), fare, data.getMinSeatBooking(), data.getCabinClass(), summary, segment,
				List.of(inventoryDatas.getNotes()), data.getSearchTag(), data.getDownPaymentPrice(), suppliers,
				data.getIsAvailable());

		inventoryRepo.saveAndFlush(inventoryDatas);
		journeyOptions.add(journeyData);
		return journeyOptions;

	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<JourneyOption> editMinimumSeat(String inventoryId, Integer minimumSeat) {
		List<JourneyOption> journeyOptions = new ArrayList<>();
		var updatedBy = session.getUser().getPrincipal();
		var updatedAt = new Date();
		var data = inventoryRepo.getByInventoryId(UUID.fromString(inventoryId));

		var inventoryDatas = new Inventory(UUID.fromString(inventoryId), data.getAirlineName(), data.getSeatCapacity(),
				data.getSeatCapacity(), data.getNotes(), minimumSeat, data.getSearchTag(), data.getTitle(),
				data.getIsAvailable(), data.getDownPaymentPrice(), data.getCreateBy(), data.getCreatedAt(), updatedBy,
				updatedAt, data.getCabinClass(), data.getFlightData(), data.getFareData(), data.getTimelimitData());
		inventoryRepo.saveAndFlush(inventoryDatas);

		var segment = inventoryHelper.getFlightSegement(inventoryDatas.getInventoryId());
		var summary = inventoryHelper.getFlightSummary(inventoryDatas.getInventoryId());
		List<String> note = Arrays.asList(inventoryDatas.getTitle(), inventoryDatas.getNotes());
		var fareDatas = inventoryDatas.getFareData();
		var fare = fareDatas.stream().map(
				dataz -> new Fare(dataz.getSubClass(), dataz.getPrice(), dataz.getAllotment(), dataz.getIsActive()))
				.collect(Collectors.toList());
		List<Supplier> suppliers = data.getSupplierData().stream()
				.map(sd -> new Supplier(sd.getId(), sd.getSupplierName(), sd.getSupplierEmail()))
				.collect(Collectors.toList());

		var journeyData = new JourneyOption(data.getInventoryId().toString(), data.getAirlineName(),
				data.getSeatCapacity(), fare, data.getMinSeatBooking(), data.getCabinClass(), summary, segment, note,
				data.getSearchTag(), data.getDownPaymentPrice(), suppliers, data.getIsAvailable());
		journeyOptions.add(journeyData);

		return journeyOptions;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<Pnr> getGroupNameList(String inventoryId) {
		var data = bookDataRepo.findByInventoryId(UUID.fromString(inventoryId));

		List<Pnr> pnr = new ArrayList<>();
		for (var datas : data) {
			var persons = datas.getPassengers().stream().map(person -> new Person(person.getTitle(),
					person.getFirstName(), person.getLastName(), person.getPersonType(), person.getDob(),
					new Identity(person.getIdentity().getNumber(), person.getIdentity().getNationality(),
							person.getIdentity().getIssuingCountry(), person.getIdentity().getExpirationDate())))
					.collect(Collectors.toList());
			var statements = datas.getStatement().stream()
					.map(statement -> new Statement(statement.getSeatBooked(), statement.getPricePerSeat(),
							statement.getTotalPrice(), statement.getTotalPayment(), statement.getPaymentDate(),
							statement.getPaymentStatus(), statement.getPaymentType()))
					.collect(Collectors.toList());
			var contactData = datas.getContactData();
			var contact = new Contact(contactData.getCustomerName(), contactData.getCustomerPhoneNumber(),
					contactData.getCustomerEmail());

			pnr.add(new Pnr(datas.getId(), datas.getBookingCode(), statements, datas.getInventoryNotes(),
					datas.getTotalPrice(), datas.getTotalPrice(), datas.getDownPaymentPrice(), datas.getSeatCount(),
					datas.getStatus(), datas.getTimelimit(), datas.getTimelimitDescription(),
					List.of(datas.getRemarks()), datas.getCabinClass(), datas.getSubClass(), contact,
					datas.getFlightSummary(), datas.getSegmentDetails(), datas.getBookDate(), true, persons,
					datas.getTotalPayment(), datas.getBookedBy()));
		}
		return pnr;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<Person> getPassengerNameList(String inventoryId) {

		List<Person> persons = new ArrayList<>();
		var data = bookDataRepo.findByInventoryId(UUID.fromString(inventoryId));
		for (var datas : data) {
			var personData = datas.getPassengers().stream().map(person -> new Person(person.getTitle(),
					person.getFirstName(), person.getLastName(), person.getPersonType(), person.getDob(),
					new Identity(person.getIdentity().getNumber(), person.getIdentity().getNationality(),
							person.getIdentity().getIssuingCountry(), person.getIdentity().getExpirationDate())))
					.collect(Collectors.toList());
			persons.addAll(personData);
		}
		return persons;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public List<BlockseatDataReport> getDataReportList(LocalDateTime from, LocalDateTime to) {
		List<BlockseatDataReport> dataReport = new ArrayList<>();
		dataReport = inventoryHelper.getDataForReport(from, to);
		return dataReport;
	}

	@Override
	@IgnoreApiVersion
	@Publish(allowAll = true)
	public String downloadListData(LocalDateTime from, LocalDateTime to) {
		try {
			exportDataToExcel(from, to);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return "Spreadsheet exported successfully.";
	}

	public void exportDataToExcel(LocalDateTime from, LocalDateTime to) throws IOException {
		List<BlockseatDataReport> dataReportList = getDataReportList(from, to);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet1");

		Row headerRow = sheet.createRow(0);
		List<String> headers = new ArrayList<String>();
		headers.addAll(Arrays.asList("Supplier Email", "Journey Title", "Duration", "Route", "Departure Date",
				"Arrival Date", "Capacity", "New", "Booked", "Confirm", "Total Payment", "Seat Open"));
		for (Integer i = 0; i < headers.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers.get(i));
		}

		int rowNum = 1;
		for (BlockseatDataReport report : dataReportList) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(report.supplierEmail());
			row.createCell(1).setCellValue(report.journeyTitle());
			row.createCell(2).setCellValue(report.duration().toString() + " Days");
			row.createCell(3).setCellValue(report.route());
			row.createCell(4).setCellValue(report.departureDate().toString());
			row.createCell(5).setCellValue(report.arrivalDate().toString());
			row.createCell(6).setCellValue(report.seatCapacity().toString());
			row.createCell(7).setCellValue(report.statusNew().toString());
			row.createCell(8).setCellValue(report.statusBooked().toString());
			row.createCell(9).setCellValue(report.statusConfirm().toString());
			row.createCell(10).setCellValue(report.totalPayment().toString());
			row.createCell(11).setCellValue(report.seatOpen().toString());
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'_time_'HH:mm");
		String currentDateTime = LocalDateTime.now().format(dateTimeFormatter).replace(":", "-");
		FileOutputStream outputStream = new FileOutputStream("result_list_" + currentDateTime + ".xlsx");
		workbook.write(outputStream);
		workbook.close();
	}

}
