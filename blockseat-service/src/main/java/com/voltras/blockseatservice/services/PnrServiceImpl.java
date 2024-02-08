package com.voltras.blockseatservice.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.blockseat.admin.api.enums.TimelimitCondition;
import com.voltras.blockseat.api.enums.CabinClass;
import com.voltras.blockseat.api.enums.DocumentType;
import com.voltras.blockseat.api.exceptions.AmountDoesntMatchException;
import com.voltras.blockseat.api.exceptions.IncompletePassengerDataException;
import com.voltras.blockseat.api.exceptions.SeatNotAvailableException;
import com.voltras.blockseat.api.exceptions.TimelimitExpiredException;
import com.voltras.blockseat.api.models.Contact;
import com.voltras.blockseat.api.models.FlightSegment;
import com.voltras.blockseat.api.models.FlightSummary;
import com.voltras.blockseat.api.models.Identity;
import com.voltras.blockseat.api.models.PaymentResponse;
import com.voltras.blockseat.api.models.Person;
import com.voltras.blockseat.api.models.Pnr;
import com.voltras.blockseat.api.models.Statement;
import com.voltras.blockseat.api.models.ValidatedPaymentResponse;
import com.voltras.blockseat.api.services.BlockseatPnrService;
import com.voltras.blockseatservice.components.AvailabiltyHelper;
import com.voltras.blockseatservice.components.BookHelper;
import com.voltras.blockseatservice.components.ClientReferenceGenerator;
import com.voltras.blockseatservice.components.EmailHelper;
import com.voltras.blockseatservice.components.LogHelper;
import com.voltras.blockseatservice.components.SolrHelper;
import com.voltras.blockseatservice.components.TicketingHelper;
import com.voltras.blockseatservice.entities.BookData;
import com.voltras.blockseatservice.entities.ContactData;
import com.voltras.blockseatservice.entities.IdentityData;
import com.voltras.blockseatservice.entities.Office;
import com.voltras.blockseatservice.entities.PassengerAttachment;
import com.voltras.blockseatservice.entities.PassengerData;
import com.voltras.blockseatservice.entities.StatementData;
import com.voltras.blockseatservice.repositories.BookDataRepository;
import com.voltras.blockseatservice.repositories.FareDataRepository;
import com.voltras.blockseatservice.repositories.InventoryRepository;
import com.voltras.blockseatservice.repositories.OfficeRepository;
import com.voltras.blockseatservice.repositories.TimelimitDataRepository;
import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.payment.common.api.enums.PaymentStatus;
import com.voltras.payment.common.api.enums.PaymentType;
import com.voltras.payment.common.api.exceptions.InsufficientBalanceException;
import com.voltras.raven.model.MessageAttachment;
import com.voltras.voltrasspring.common.VoltrasSpringMultipartRequest;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service("blockseatPnrService")
public class PnrServiceImpl implements RpcBasicService, BlockseatPnrService {
	@Autowired
	private AvailabiltyHelper availHelper;
	@Autowired
	private BookHelper bookHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private TicketingHelper ticketingHelper;
	@Autowired
	private SolrHelper solrHelper;
	@Autowired
	private FareServiceImpl fareService;
	@Autowired
	private FinancialStatementService financialStatementService;
	@Autowired
	private DomainService domain;
	@Autowired
	private ClientReferenceGenerator generator;
	@Autowired
	private VoltrasSpringMultipartRequest multiPartRequest;
	@Autowired
	private VanAdditionalRequestData session;
	@Autowired
	LogHelper log;

	@Autowired
	private InventoryRepository inventoryRepo;
	@Autowired
	private BookDataRepository bookDataRepo;
	@Autowired
	private OfficeRepository officeRepo;
	@Autowired
	private FareDataRepository fareRepo;
	@Autowired
	private TimelimitDataRepository timelimitRepo;

	private final String className = this.getClass().getCanonicalName();

	@Override
	@Publish(allowAll = true)
	@Transactional
	public Pnr book(UUID journeyOptId, Integer paxCount, Contact contact, CabinClass cabinClass, String subClass)
			throws SeatNotAvailableException {

		var inventoryData = inventoryRepo.getByInventoryId(journeyOptId);
		var fareData = fareRepo.findByInventoryIdAndSubClass(journeyOptId, subClass);
		var flightData = inventoryData.getFlightData();
		LocalDateTime flightDates = flightData.get(0).getSegmentDetails().departure().time();
		LocalDateTime bookDate = LocalDateTime.now(ZoneId.systemDefault());

		if (bookDate.isAfter(flightDates) || !inventoryData.getIsAvailable() || bookDate == flightDates
				|| inventoryData.getSeatAllot().equals(0) || fareData.getAllotment().equals(0)
				|| paxCount < inventoryData.getMinSeatBooking() || paxCount > fareData.getAllotment()) {
			log.error("[{}.book] error: SeatNotAvailableException", className);
			throw new SeatNotAvailableException();
		} else {
			var office = session.getOffice();
			if (!domain.getIsB2c(session.getDomain())) {
				var officeDataOpt = officeRepo.findByCode(office.getCode());
				if (officeDataOpt.isEmpty()) {
					officeRepo.saveAndFlush(new Office(office.getAddress(), office.getCode(), office.getEmail(),
							office.getFax(),
							office.getLogo() == null ? null
									: java.util.Base64.getEncoder().encodeToString(office.getLogo()),
							office.getName(), office.getPackageName(), office.getPhone(),
							session.getUser().getPrincipal()));
				}
			}

			var internalBookingCode = generator.generateClientReference();
			var fare = fareService.calculateTotalPrice(journeyOptId.toString(), paxCount, subClass);
			List<FlightSegment> segment = availHelper.getFlightSegement(inventoryData.getInventoryId());
			FlightSummary summary = availHelper.getFlightSummary(inventoryData.getInventoryId());

			var bookData = new BookData(journeyOptId, inventoryData.getTitle(), internalBookingCode, bookDate,
					contact.fullname(), List.of(), 0D, null, null, paxCount, "", subClass, 0D,
					inventoryData.getCabinClass(), BookingStatus.NEW, List.of(),
					new ContactData(contact.fullname(), contact.phoneNumber(), contact.email()), summary);
			bookDataRepo.saveAndFlush(bookData);

			var seatAllotAfterBook = inventoryData.getSeatAllot() - paxCount;
			inventoryData.setSeatAllot(seatAllotAfterBook);
			inventoryRepo.saveAndFlush(inventoryData);

			var seatAfterBook = fareData.getAllotment() - paxCount;
			fareData.setAllotment(seatAfterBook);
			fareRepo.saveAndFlush(fareData);

			var timelimitDatas = timelimitRepo.findByInventoryId(journeyOptId);
			LocalDateTime timelimit = null;
			for (var timilimitData : timelimitDatas) {
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime lowerLimit = flightDates.minusDays(timilimitData.getDayFrom());
				LocalDateTime upperLimit = flightDates.minusDays(timilimitData.getDayTo());
				if (now.isAfter(lowerLimit) && !upperLimit.isBefore(now)) {
					timelimit = switch (timilimitData.getCondition()) {
					case BeforeETD: {
						yield bookDate.plusDays(1);

					}
					case AfterBookingInDay: {
						yield bookDate.plusDays(1);
					}
					case AfterBookingInMinute: {
						yield bookDate.plusMinutes(timilimitData.getDuration());
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + timilimitData.getCondition());
					};
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
					bookData.setTimelimit(timelimit);
					bookData.setTimelimitDescription(
							"Tenggat waktu pembayaran " + bookData.getTimelimit().format(formatter));
					bookData.setTimelimitCondition(timilimitData.getCondition());
				}

			}
			if (bookData.getTimelimitCondition() != null
					&& bookData.getTimelimitCondition().equals(TimelimitCondition.AfterBookingInMinute)) {
				bookData.setDownPaymentPrice(0D);
			} else {
				bookData.setDownPaymentPrice(inventoryData.getDownPaymentPrice());
			}
			bookDataRepo.saveAndFlush(bookData);

			var nta = fare.price();
			bookData.setFinancialStatement(
					financialStatementService.fromInventoryAndFare(bookData, nta, paxCount, null));
			bookData.setTotalPrice(bookData.getFinancialStatement().getTotalPrice());
			bookData.setPricePerSeat(bookData.getFinancialStatement().getTicketPrice());
			bookData.setOfficeCode(office.getCode());
			bookData.setOfficeName(office.getName());
			bookData.setOfficePackage(office.getPackageName());
			bookData.setOutstanding(bookData.getTotalPrice());
			bookData.setTotalPayment(0D);

			bookData.setSegmentDetails(segment);
			bookData.setDomain(session.getDomain());
			var note = Arrays.asList(inventoryData.getTitle(), inventoryData.getNotes()).toString();
			bookData.setInventoryNotes(note);
			bookData.setInventoryTitle(inventoryData.getTitle());
			bookData.setBookedBy(session.getUser() == null ? bookData.getBookedBy() : session.getUser().getPrincipal());

			emailHelper.sendNewNotification(bookData);

			return new Pnr(bookData.getId(), bookData.getBookingCode(), Arrays.asList(), bookData.getInventoryNotes(),
					bookData.getTotalPrice(), bookData.getOutstanding(), bookData.getDownPaymentPrice(),
					bookData.getSeatCount(), bookData.getStatus(), bookData.getTimelimit(),
					bookData.getTimelimitDescription(), Arrays.asList(bookData.getRemarks()), bookData.getCabinClass(),
					bookData.getSubClass(),
					new Contact(bookData.getContactData().getCustomerName(),
							bookData.getContactData().getCustomerPhoneNumber(),
							bookData.getContactData().getCustomerEmail()),
					summary, segment, bookData.getBookDate(), true, List.of(), 0D, bookData.getBookedBy());
		}
	}

	@Override
	@Publish(allowAll = true)
	public List<Pnr> retrieve(String internalBookingCode) throws DataNotFoundException {
		List<Pnr> dataReatrieve = new ArrayList<>();
		if (internalBookingCode != null) {
			var data = getBookDataByBookingCode(internalBookingCode, session.getOffice().getCode());

			if (data != null) {
				if (session.getOffice().getCode().equals(data.getOfficeCode())) {
					var persons = data.getPassengers().stream().map(person -> new Person(person.getTitle(),
							person.getFirstName(), person.getLastName(), person.getPersonType(), person.getDob(),
							new Identity(person.getIdentity().getNumber(), person.getIdentity().getNationality(),
									person.getIdentity().getIssuingCountry(),
									person.getIdentity().getExpirationDate())))
							.collect(Collectors.toList());
					var statementData = data.getStatement();
					List<Statement> statementDataList = new ArrayList<Statement>();
					for (var statement : statementData) {
						statementDataList.add(new Statement(statement.getSeatBooked(), statement.getPricePerSeat(),
								statement.getTotalPrice(), statement.getTotalPayment(), statement.getPaymentDate(),
								statement.getPaymentStatus(), statement.getPaymentType()));

						statementDataList.addAll(List.of());

					}

					var contactData = data.getContactData();
					var contact = new Contact(contactData.getCustomerName(), contactData.getCustomerPhoneNumber(),
							contactData.getCustomerEmail());

					var retrieveData = new Pnr(data.getId(), data.getBookingCode(), statementDataList,
							data.getInventoryNotes(), data.getTotalPrice(), data.getOutstanding(),
							data.getDownPaymentPrice(), data.getSeatCount(), data.getStatus(), data.getTimelimit(),
							data.getTimelimitDescription(), List.of(data.getRemarks()), data.getCabinClass(),
							data.getSubClass(), contact, data.getFlightSummary(), data.getSegmentDetails(),
							data.getBookDate(), true, persons, data.getTotalPayment(), data.getBookedBy());
					dataReatrieve.add(retrieveData);

				} else {
					log.error("[{}.retrieve] error: DataNotFoundException", className);
					throw new DataNotFoundException();
				}
			}
		} else {
			var datas = bookDataRepo.findByOfficeCode(session.getOffice().getCode());
			for (var allDatas : datas) {
				var officeCode = allDatas.getOfficeCode();
				if (session.getOffice().getCode().equals(officeCode)) {
					var persons = allDatas.getPassengers().stream().map(person -> new Person(person.getTitle(),
							person.getFirstName(), person.getLastName(), person.getPersonType(), person.getDob(),
							new Identity(person.getIdentity().getNumber(), person.getIdentity().getNationality(),
									person.getIdentity().getIssuingCountry(),
									person.getIdentity().getExpirationDate())))
							.collect(Collectors.toList());
					var statementData = allDatas.getStatement();
					List<Statement> statementDataList = new ArrayList<Statement>();
					for (var statement : statementData) {
						statementDataList.add(new Statement(statement.getSeatBooked(), statement.getPricePerSeat(),
								statement.getTotalPrice(), statement.getTotalPayment(), statement.getPaymentDate(),
								statement.getPaymentStatus(), statement.getPaymentType()));
						statementDataList.addAll(List.of());
					}
					var contactData = allDatas.getContactData();
					var contact = new Contact(contactData.getCustomerName(), contactData.getCustomerPhoneNumber(),
							contactData.getCustomerEmail());

					var retrieveAllData = new Pnr(allDatas.getId(), allDatas.getBookingCode(), statementDataList,
							allDatas.getInventoryTitle(), allDatas.getTotalPrice(), allDatas.getOutstanding(),
							allDatas.getDownPaymentPrice(), allDatas.getSeatCount(), allDatas.getStatus(),
							allDatas.getTimelimit(), allDatas.getTimelimitDescription(), List.of(allDatas.getRemarks()),
							allDatas.getCabinClass(), allDatas.getSubClass(), contact, allDatas.getFlightSummary(),
							allDatas.getSegmentDetails(), allDatas.getBookDate(), true, persons,
							allDatas.getTotalPayment(), allDatas.getBookedBy());

					dataReatrieve.add(retrieveAllData);
				}
			}
		}
		return dataReatrieve;
	}

	@Override
	@Publish(allowAll = true)
	public List<Pnr> addPassengers(String internalBookingCode, List<Person> passengers) {
		List<Pnr> pnrData = new ArrayList<>();
		BookData data = null;
		try {
			data = getBookDataByBookingCode(internalBookingCode, session.getOffice().getCode());
		} catch (DataNotFoundException e) {
			e.printStackTrace();
		}

		if (data != null) {
			List<PassengerAttachment> passangerAttachments = new ArrayList<>();
			Map<String, MessageAttachment> messageAttachments = new HashMap<>();
			if (multiPartRequest.getFiles() != null && !multiPartRequest.getFiles().isEmpty()) {
				for (var file : multiPartRequest.getFiles()) {
					try {
						var base64Attachment = Base64.encodeBase64String(file.getBytes());
						passangerAttachments.add(new PassengerAttachment(file.getName(), file.getSize(),
								base64Attachment, file.getContentType()));
						var messageAttachment = new MessageAttachment();
						messageAttachment.setUseBaseSixtyFour(true);
						messageAttachment.setFileDataBaseSixtyFour(base64Attachment);
						messageAttachments.put(file.getName(), messageAttachment);
					} catch (Exception e) {
					}
				}
			}

			if (data.getTimelimitCondition().equals(TimelimitCondition.AfterBookingInMinute)) {
				var passengerDataz = passengers.stream()
						.map(passengerz -> new PassengerData(passengerz.firstName(), passengerz.lastName(),
								passengerz.dob(), "", passengerz.title(), passengerz.personType(),
								new IdentityData(passengerz.identity().number(), passengerz.identity().nationality(),
										passengerz.identity().issuingCountry(), passengerz.identity().expirationDate()),
								passangerAttachments))
						.collect(Collectors.toList());

				data.getPassengers().addAll(passengerDataz);
				bookDataRepo.saveAndFlush(data);

				for (var passenger : passengers) {
					try {
						solrHelper.savePassengerData(data.getOfficeCode(), passenger);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				var contactData = new Contact(data.getContactData().getCustomerName(),
						data.getContactData().getCustomerPhoneNumber(), data.getContactData().getCustomerEmail());

				var persons = passengers.stream()
						.map(person -> new Person(person.title(), person.firstName(), person.lastName(),
								person.personType(), person.dob(),
								new Identity(person.identity().number(), person.identity().nationality(),
										person.identity().issuingCountry(), person.identity().expirationDate())))
						.collect(Collectors.toList());

				var statements = data.getStatement().stream()
						.map(statement -> new Statement(statement.getSeatBooked(), statement.getPricePerSeat(),
								statement.getTotalPrice(), statement.getTotalPayment(), statement.getPaymentDate(),
								statement.getPaymentStatus(), statement.getPaymentType()))
						.collect(Collectors.toList());

				pnrData.add(new Pnr(data.getId(), data.getBookingCode(), statements, data.getInventoryNotes(),
						data.getTotalPrice(), data.getOutstanding(), data.getDownPaymentPrice(), data.getSeatCount(),
						data.getStatus(), data.getTimelimit(), data.getTimelimitDescription(),
						List.of(data.getRemarks()), data.getCabinClass(), data.getSubClass(), contactData,
						data.getFlightSummary(), data.getSegmentDetails(), data.getBookDate(), true, persons,
						data.getTotalPayment(), data.getBookedBy()));
			} else {
				if (data.getTotalPayment().equals(0D)) {
					throw new RuntimeException("Silahkan melakukan pembayaran sebelum input Data passangers");
				} else {
					var passengerDataz = passengers.stream().map(passengerz -> new PassengerData(passengerz.firstName(),
							passengerz.lastName(), passengerz.dob(), "", passengerz.title(), passengerz.personType(),
							new IdentityData(passengerz.identity().number(), passengerz.identity().nationality(),
									passengerz.identity().issuingCountry(), passengerz.identity().expirationDate()),
							passangerAttachments)).collect(Collectors.toList());

					data.getPassengers().addAll(passengerDataz);
					bookDataRepo.saveAndFlush(data);

					for (var passenger : passengers) {
						try {
							solrHelper.savePassengerData(data.getOfficeCode(), passenger);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					var contactData = new Contact(data.getContactData().getCustomerName(),
							data.getContactData().getCustomerPhoneNumber(), data.getContactData().getCustomerEmail());

					var persons = passengers.stream()
							.map(person -> new Person(person.title(), person.firstName(), person.lastName(),
									person.personType(), person.dob(),
									new Identity(person.identity().number(), person.identity().nationality(),
											person.identity().issuingCountry(), person.identity().expirationDate())))
							.collect(Collectors.toList());

					var statements = data.getStatement().stream()
							.map(statement -> new Statement(statement.getSeatBooked(), statement.getPricePerSeat(),
									statement.getTotalPrice(), statement.getTotalPayment(), statement.getPaymentDate(),
									statement.getPaymentStatus(), statement.getPaymentType()))
							.collect(Collectors.toList());

					pnrData.add(new Pnr(data.getId(), data.getBookingCode(), statements, data.getInventoryNotes(),
							data.getTotalPrice(), data.getOutstanding(), data.getDownPaymentPrice(),
							data.getSeatCount(), data.getStatus(), data.getTimelimit(), data.getTimelimitDescription(),
							List.of(data.getRemarks()), data.getCabinClass(), data.getSubClass(), contactData,
							data.getFlightSummary(), data.getSegmentDetails(), data.getBookDate(), true, persons,
							data.getTotalPayment(), data.getBookedBy()));
				}
			}

		}
		return pnrData;
	}

	@Override
	public PaymentResponse payment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException {
		throw new RuntimeException("Unsupported Method");
	}

	@Override
	@Publish(allowAll = true)
	@Transactional
	public ValidatedPaymentResponse validatePayment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException,
			IncompletePassengerDataException {
		BookData data;

		try {
			data = getBookDataByBookingCode(bookingCode, "validatePayment");
		} catch (DataNotFoundException e) {
			throw new RuntimeException("BookData tidak ditemukan saat validateBook");
		}
		bookHelper.bookDataStatusCheck(data);

		if (totalPayment > data.getTotalPrice() || totalPayment > data.getOutstanding()) {
			log.error("[{}.validatePayment] error: AmountDoesntMatchException", className);
			throw new AmountDoesntMatchException();
		}

		if (LocalDateTime.now().isAfter(data.getTimelimit())) {
			throw new TimelimitExpiredException();
		}

		var contactData = new Contact(data.getContactData().getCustomerName(),
				data.getContactData().getCustomerPhoneNumber(), data.getContactData().getCustomerEmail());

		if (data.getTimelimitCondition().equals(TimelimitCondition.AfterBookingInMinute)) {
			return new ValidatedPaymentResponse(bookingCode, data.getTotalPrice(), totalPayment, contactData);
		} else {
			if (totalPayment.equals(data.getTotalPrice())) {
				if (data.getPassengers().size() != data.getSeatCount()) {
					throw new IncompletePassengerDataException();
				}
			}
		}

		return new ValidatedPaymentResponse(bookingCode, data.getTotalPrice(), totalPayment, contactData);
	}

	@Override
	@Publish(allowAll = true)
	@Transactional
	public PaymentResponse finalizePayment(String bookingCode, PaymentStatus paymentStatus, PaymentType paymentType,
			Double totalPayment, String goblinAccountId, String providerTransactionId, String transactionId)
			throws InsufficientBalanceException, AmountDoesntMatchException {
		BookData data;

		try {
			data = getBookDataByBookingCode(bookingCode, "finalizePayment");
		} catch (DataNotFoundException e) {
			throw new RuntimeException("BookData tidak ditemukan saat book");
		}
		data.setGoblinAccountId(goblinAccountId);
		data.setTotalPrice(data.getFinancialStatement().getTotalPrice());

		var paidBy = session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal();

		switch (paymentStatus) {
		case SUCCESS -> {
			data.setGoblinAccountId(goblinAccountId);
			data.setTotalPrice(data.getFinancialStatement().getTotalPrice());
			var statementData = new StatementData(paymentStatus, data.getSeatCount(), data.getPricePerSeat(),
					data.getTotalPrice(), totalPayment, data.getDownPaymentPrice(), LocalDateTime.now(),
					providerTransactionId, transactionId, paidBy, data.getInventoryId().toString());
			data.getStatement().add(statementData);
			var totalPayments = data.getStatement().stream()
					.filter(status -> status.getPaymentStatus().equals(PaymentStatus.SUCCESS))
					.mapToDouble(total -> total.getTotalPayment()).sum();
			data.setTotalPayment(totalPayments);
			data.setOutstanding(data.getTotalPrice() - data.getTotalPayment());
			statementData.setPaymentChannel("deposit");
			statementData.setPaymentType(paymentType);
			statementData.setCreatedAt(LocalDateTime.now());
			bookDataRepo.saveAndFlush(data);

			process(data);
		}
		case WAITING -> {
			var statementData = new StatementData(PaymentStatus.WAITING, data.getSeatCount(), data.getPricePerSeat(),
					data.getTotalPrice(), totalPayment, data.getDownPaymentPrice(), LocalDateTime.now(),
					providerTransactionId, transactionId, paidBy, data.getInventoryId().toString());
			data.getStatement().add(statementData);
			statementData.setCreatedAt(LocalDateTime.now());
			statementData.setTimelimitAt(LocalDateTime.now());
			bookDataRepo.saveAndFlush(data);

		}
		default -> throw new IllegalArgumentException("Unexpected value: " + paymentStatus);
		}
		var statement = data.getStatement();
		String statementId = null;
		for (var statements : statement) {
			statementId = statements.getId().toString();
		}
		return new PaymentResponse(statementId, bookingCode, providerTransactionId);
	}

	@Override
	public PaymentResponse downPayment(String bookingCode, PaymentType paymentType, Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException {
		throw new RuntimeException("Unsupported Method");
	}

	@Override
	@Publish(allowAll = true)
	@Transactional
	public ValidatedPaymentResponse validateDownPayment(String bookingCode, PaymentType paymentType,
			Double totalPayment)
			throws InsufficientBalanceException, AmountDoesntMatchException, TimelimitExpiredException {
		BookData data;
		try {
			data = getBookDataByBookingCode(bookingCode, "validatePayment");
		} catch (DataNotFoundException e) {
			throw new RuntimeException("BookData tidak ditemukan saat validateBook");
		}
		bookHelper.bookDataStatusCheck(data);
		if (LocalDateTime.now().isAfter(data.getTimelimit())) {
			throw new TimelimitExpiredException();
		}

		var inventoryData = inventoryRepo.getByInventoryId(data.getInventoryId());
		var flightData = inventoryData.getFlightData();
		LocalDateTime flightDates = flightData.get(0).getSegmentDetails().departure().time();
		var timelimitDatas = timelimitRepo.findByInventoryId(data.getInventoryId());
		LocalDateTime timelimit = null;
		for (var timilimitData : timelimitDatas) {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime lowerLimit = flightDates.minusDays(timilimitData.getDayFrom());
			LocalDateTime upperLimit = flightDates.minusDays(timilimitData.getDayTo());
			if (now.isAfter(lowerLimit) && now.isBefore(upperLimit)) {
				timelimit = switch (timilimitData.getCondition()) {
				case BeforeETD: {
					yield flightDates.minusDays(timilimitData.getDuration());
				}
				case AfterBookingInDay: {
					yield data.getBookDate().plusDays(timilimitData.getDuration());
				}
				case AfterBookingInMinute: {
					yield data.getBookDate().plusMinutes(timilimitData.getDuration());
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + timilimitData.getCondition());
				};
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				data.setTimelimit(timelimit);
				data.setTimelimitDescription("Tenggat waktu pelunasan " + data.getTimelimit().format(formatter));
				data.setTimelimitCondition(timilimitData.getCondition());
			}

		}
		switch (data.getTimelimitCondition()) {
		case AfterBookingInMinute: {
			throw new RuntimeException("Segera Melakukan Pembayaran Penuh");
		}
		case AfterBookingInDay, BeforeETD: {
			var contactData = new Contact(data.getContactData().getCustomerName(),
					data.getContactData().getCustomerPhoneNumber(), data.getContactData().getCustomerEmail());
			return new ValidatedPaymentResponse(bookingCode, data.getDownPaymentPrice(), totalPayment, contactData);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + data.getTimelimitCondition());
		}
	}

	@Override
	@Publish(allowAll = true)
	@Transactional
	public PaymentResponse finalizeDownPayment(String bookingCode, PaymentStatus paymentStatus, PaymentType paymentType,
			Double totalPayment, String goblinAccountId, String providerTransactionId, String transactionId)
			throws InsufficientBalanceException, AmountDoesntMatchException {
		BookData data;
		try {
			data = getBookDataByBookingCode(bookingCode, "finalizePayment");
		} catch (DataNotFoundException e) {
			throw new RuntimeException("BookData tidak ditemukan saat book");
		}

		var paidBy = session.getUser() == null ? data.getBookedBy() : session.getUser().getPrincipal();
		switch (paymentStatus) {
		case SUCCESS -> {
			data.setGoblinAccountId(goblinAccountId);
			data.setTotalPrice(data.getFinancialStatement().getTotalPrice());
			var statementData = new StatementData(paymentStatus, data.getSeatCount(), data.getPricePerSeat(),
					data.getTotalPrice(), totalPayment, data.getDownPaymentPrice(), LocalDateTime.now(),
					providerTransactionId, transactionId, paidBy, data.getInventoryId().toString());
			data.getStatement().add(statementData);
			var totalPayments = data.getStatement().stream()
					.filter(status -> status.getPaymentStatus().equals(PaymentStatus.SUCCESS))
					.mapToDouble(total -> total.getTotalPayment()).sum();
			data.setTotalPayment(totalPayments);
			data.setOutstanding(data.getTotalPrice() - data.getTotalPayment());
			statementData.setPaymentChannel("deposit");
			statementData.setPaymentType(paymentType);
			statementData.setCreatedAt(LocalDateTime.now());
			bookDataRepo.saveAndFlush(data);

			process(data);
		}
		case WAITING -> {
			var statementData = new StatementData(PaymentStatus.WAITING, data.getSeatCount(), data.getPricePerSeat(),
					data.getTotalPrice(), totalPayment, data.getDownPaymentPrice(), LocalDateTime.now(),
					providerTransactionId, transactionId, paidBy, data.getInventoryId().toString());
			data.getStatement().add(statementData);
			statementData.setCreatedAt(LocalDateTime.now());
			statementData.setTimelimitAt(LocalDateTime.now());
			bookDataRepo.saveAndFlush(data);

		}
		default -> throw new IllegalArgumentException("Unexpected value: " + paymentStatus);
		}

		var statement = data.getStatement();
		String statementId = null;
		for (var statements : statement) {
			statementId = statements.getId().toString();
		}
		return new PaymentResponse(statementId, bookingCode, providerTransactionId);
	}

	@Override
	@Publish(allowAll = true)
	public String print(@NotBlank String bookingCode, @NotNull DocumentType type, @NotNull Boolean shareToEmail)
			throws DataNotFoundException {
		BookData data = getBookDataByBookingCode(bookingCode, "print");
		if (!type.equals(DocumentType.RECEIPT)) {
			log.error("[{}.print] invalid case", className);
			throw new RuntimeException();
		}

		return ticketingHelper.printReceipt(data, officeRepo.findByCode(data.getOfficeCode()).orElse(null));
	}

	public BookData process(BookData data) {

		var totalPayments = data.getStatement().stream()
				.filter(status -> status.getPaymentStatus().equals(PaymentStatus.SUCCESS))
				.mapToDouble(total -> total.getTotalPayment()).sum();
		if (!Double.valueOf(totalPayments).equals(data.getFinancialStatement().getTotalPrice())) {
			data.setStatus(BookingStatus.RESERVED);
			emailHelper.sendReserveNotificationToHelpdesk(data);
			emailHelper.sendReserveNotification(data);
		} else if (Double.valueOf(totalPayments).equals(data.getFinancialStatement().getTotalPrice())) {
			data.setStatus(BookingStatus.CONFIRMED);
			emailHelper.sendSuccessNotification(data);
		}
		return data;
	}

	private BookData getBookDataByBookingCode(String bookingCode, String officeCode) throws DataNotFoundException {

		var bookDataOpt = bookDataRepo.findByBookingCodeAndOfficeCode(bookingCode, session.getOffice().getCode());
		if (bookDataOpt.isEmpty()) {
			log.error("[{}.getBookDataByBookingCode] {} error: BookData dengan bookingCode: {} tidak ditemukan",
					this.getClass().getCanonicalName(), className, bookingCode);
			throw new DataNotFoundException();
		}
		return bookDataOpt.get();
	}

	@Override
	@Publish(allowAll = true)
	public Pnr getBookDetail(@NotBlank String bookId) throws DataNotFoundException {
		var bookData = bookDataRepo.findById(UUID.fromString(bookId));
		if (bookData.isEmpty()) {
			log.error("[{}.getBookDetail] {} error: BookData dengan bookiId: {} tidak ditemukan",
					this.getClass().getCanonicalName(), className, UUID.fromString(bookId));
			throw new DataNotFoundException();
		}
		var data = bookData.get();

		var contact = new Contact(data.getContactData().getCustomerName(),
				data.getContactData().getCustomerPhoneNumber(), data.getContactData().getCustomerEmail());
		var passengersData = data.getPassengers().stream()
				.map(person -> new Person(person.getTitle(), person.getFirstName(), person.getLastName(),
						person.getPersonType(), person.getDob(),
						new Identity(person.getIdentity().getNumber(), person.getIdentity().getNationality(),
								person.getIdentity().getIssuingCountry(), person.getIdentity().getExpirationDate())))
				.collect(Collectors.toList());
		var statements = data.getStatement().stream()
				.map(statement -> new Statement(statement.getSeatBooked(), statement.getPricePerSeat(),
						statement.getTotalPrice(), statement.getTotalPayment(), statement.getPaymentDate(),
						statement.getPaymentStatus(), statement.getPaymentType()))
				.collect(Collectors.toList());

		return new Pnr(data.getId(), data.getBookingCode(), statements, data.getInventoryNotes(), data.getTotalPrice(),
				data.getOutstanding(), data.getDownPaymentPrice(), data.getSeatCount(), data.getStatus(),
				data.getTimelimit(), data.getTimelimitDescription(), List.of(data.getRemarks()), data.getCabinClass(),
				data.getSubClass(), contact, data.getFlightSummary(), data.getSegmentDetails(), data.getBookDate(),
				true, passengersData, data.getTotalPayment(), data.getBookedBy());
	}
//	@NotBlank UUID bookId, @NotBlank String bookingCode, @NotNull BookingStatus status,
//	LocalDateTime bookingDate, LocalDateTime timelimit, List<Statement> statements, Double totalPrice,
//	Double outstanding, Double downPaymentPrice, CabinClass cabinClass, String subClass, Contact contact,
//	FlightSummary flightSummary, List<FlightSegment> flightSegments, List<Person> passengers, Double totalPayment,
//	String bookedBy, LocalDateTime cancellationDate, String canceledBy
}
