package com.voltras.blockseatservice.components;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseatservice.entities.BookData;
import com.voltras.blockseatservice.entities.FlightData;
import com.voltras.blockseatservice.entities.Inventory;
import com.voltras.blockseatservice.entities.Office;
import com.voltras.blockseatservice.models.FlightParam;
import com.voltras.blockseatservice.models.PaymentParam;
import com.voltras.blockseatservice.models.SimpleParam;
import com.voltras.blockseatservice.repositories.AirlineDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.blockseatservice.utils.GenerateUtil;
import com.voltras.payment.common.api.enums.PaymentStatus;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class TicketingHelper {
	@Autowired
	private VoucherGenerator voucherGenerator;

	@Value("${template.backColor}")
	private String backColor;

	@Value("${template.receipt.header}")
	private String receiptHeader;

	@Value("${template.receipt.footer}")
	private String receiptFooter;

	@Autowired
	private AirlineDataRepository airlineRepo;
	@Autowired
	private InventoryRepository inventoryRepo;

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm",
			Locale.forLanguageTag("en-US"));
	private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
	private final String whiteImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgDTD2qgAAAAASUVORK5CYII=";

	public String generateLogoFromString(String savedLogo) {
		var isFromSavedLogo = savedLogo != null && !savedLogo.isBlank();
		return isFromSavedLogo ? savedLogo : whiteImage;
	}

	public String printReceipt(BookData data, Office office) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("header", GenerateUtil.getBase64EncodedImage(receiptHeader));
		parameters.put("logo", office.getLogo() == null || office.getLogo().isBlank() ? whiteImage : office.getLogo());
		parameters.put("bookingStatus", data.getStatus().toString());
		parameters.put("bookingCode", data.getBookingCode());
		parameters.put("supplierName", "TRAVELAGENT");
		parameters.put("bookDateDescription", data.getBookingCode());
		parameters.put("bookDate", data.getBookDate().format(dateFormatter));
		data.getSubClass();
		Inventory inventory = inventoryRepo.getByInventoryId(data.getInventoryId());
		parameters.put("inventoryName", inventory.getTitle());
		List<FlightData> flightDatas = inventory.getFlightData();
		List<FlightParam> departureFlightDatas = flightDatas.stream()
				.filter(flightData -> flightData.getSegmentDetails().isDepart())
				.map(flightData -> getFlightParam(flightData, inventory.getCabinClass(), inventory.getSubClass(),
						backColor))
				.collect(Collectors.toList());
		if (!departureFlightDatas.isEmpty()) {
			parameters.put("goFlightTitle", " Departure Flight");
			parameters.put("goFlights", new JRBeanCollectionDataSource(departureFlightDatas, false));
		}

		List<FlightParam> arrivalFlightDatas = flightDatas.stream()
				.filter(flightData -> !flightData.getSegmentDetails().isDepart())
				.map(flightData -> getFlightParam(flightData, inventory.getCabinClass(), inventory.getSubClass(),
						backColor))
				.collect(Collectors.toList());
		if (!departureFlightDatas.isEmpty()) {
			parameters.put("backFlightTitle", " Arrival Flight");
			parameters.put("backFlights", new JRBeanCollectionDataSource(arrivalFlightDatas, false));
		}
		parameters.put("backgroundColor", backColor);

		List<PaymentParam> paymentParams = new ArrayList<>();
		paymentParams.add(new PaymentParam(data.getBookDate().format(dateFormatter),
				String.format("Ticket x%s", data.getSeatCount()),
				numberFormat.format(data.getTotalPrice()).replace("Rp", "IDR ")));
		Boolean isDownPayment = false;
		for (var i = 0; i < data.getStatement().size(); i++) {
			var statement = data.getStatement().get(i);
			if (!statement.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
				continue;
			}
			if (statement.getDownPayment() > 0D) {
				isDownPayment = true;
			}

			String description = isDownPayment && i == 0 ? "Down Payment"
					: String.format("Payment with %s", switch (statement.getPaymentType()) {
					case CC -> "Credit Card";
					case DEPOSIT -> "Deposit";
					case VA -> "Virtual Account";
					case TRANSFER -> "Transfers";
					});
			paymentParams.add(new PaymentParam(statement.getPaymentDate().format(dateFormatter), description,
					numberFormat.format(statement.getTotalPayment()).replace("Rp", "IDR ")));
		}
		parameters.put("paymentDetails", new JRBeanCollectionDataSource(paymentParams, false));
		parameters.put("outstanding", numberFormat.format(data.getOutstanding()).replace("Rp", "IDR "));

		parameters.put("office",
				new JRBeanCollectionDataSource(List.of(new SimpleParam("Office Name", office.getName()),
						new SimpleParam("Office Address", office.getAddress()),
						new SimpleParam("Office Email", office.getEmail()),
						new SimpleParam("Office Fax", office.getFax()),
						new SimpleParam("Office Phone", office.getPhone())), false));
		parameters.put("footer", GenerateUtil.getBase64EncodedImage(receiptFooter));
		return voucherGenerator.generate("receipt", parameters);
	}

	private String getDurationParam(Integer duration) {
		var hour = Integer.divideUnsigned(duration, 60);
		var minute = duration % 60;
		return String.format("%sh %sm", hour, minute);
	}

	public FlightParam getFlightParam(FlightData segment, CabinClass inventoryClass, String subClass,
			String backgroundColor) {
		FlightSegment flightSegment = segment.getSegmentDetails();
		var airline = airlineRepo.getByOperatingCode(segment.getSegmentDetails().operatingAirline());
		String cabinClass = switch (inventoryClass) {
		case BUS -> "BUSINESS";
		case ECO -> "ECONOMY";
		case FIR -> "FIRST CLASS";
		case PRE -> "PROMO";
		};
		var departure = flightSegment.departure();
		var arrival = flightSegment.arrival();
		var flightDuration = String.format("%s (%s stop)", getDurationParam(flightSegment.flightDuration()),
				flightSegment.stop());
		String transit = flightSegment.transitDuration() == null || flightSegment.transitDuration().equals(0) ? null
				: String.format("%s for transit on %s(%s)", getDurationParam(flightSegment.transitDuration()),
						departure.name(), departure.iata());
		var equipment = flightSegment.equipment() == null || flightSegment.equipment().isBlank() ? null
				: String.format("Aircraft %s", flightSegment.equipment());
		var logo = GenerateUtil.getBase64EncodedImage(airline.getLogoUrl());
		return new FlightParam(logo, String.format("%s (%s)", arrival.name(), arrival.iata()), arrival.city(),
				arrival.time(), String.format("%s (%s)", departure.name(), departure.iata()), departure.city(),
				departure.time(),
				subClass == null ? String.format("%s", cabinClass) : String.format("%s (%s)", cabinClass, subClass),
				flightDuration, equipment, airline.getOperatingName(), flightSegment.flightNumber(), transit,
				backgroundColor);
	}
}