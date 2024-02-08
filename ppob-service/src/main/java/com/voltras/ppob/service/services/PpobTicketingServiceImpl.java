package com.voltras.ppob.service.services;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voltras.core.common.api.enums.BookingStatus;
import com.voltras.core.common.api.enums.PrintType;
import com.voltras.core.common.api.exceptions.DataNotFoundException;
import com.voltras.core.common.api.exceptions.GatewayTimeoutException;
import com.voltras.ppob.api.services.PpobTicketingService;
import com.voltras.ppob.service.components.EmailHelper;
import com.voltras.ppob.service.components.LogHelper;
import com.voltras.ppob.service.components.TicketingHelper;
import com.voltras.ppob.service.entities.BookData;
import com.voltras.ppob.service.repositories.BookDataRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service("ppobTicketingService")
public class PpobTicketingServiceImpl implements PpobTicketingService, RpcBasicService {
	@Autowired
	private LogHelper logger;
	@Autowired
	private VanAdditionalRequestData session;

	@Autowired
	private BookDataRepository bookDataRepo;

	@Autowired
	private TicketingHelper ticketingHelper;
	@Autowired
	private EmailHelper emailHelper;
	private final String className = this.getClass().getCanonicalName();
	private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

	@Override
	@Publish(allowAll = true)
	public String print(@NotBlank String bookingCode, @NotNull PrintType type, @NotNull Boolean shareToEmail)
			throws DataNotFoundException, GatewayTimeoutException {
		logger.info("[{}.print] request: {bookingCode={}, type={}, shareToEmail={}}", className, bookingCode, type,
				shareToEmail);
		var office = session.getOffice();
		var data = bookDataRepo.findByOfficeCodeAndBookingCode(office.getCode(), bookingCode);
		var bookData = data.get();
		if (data.isEmpty()) {
			logger.error("[{}.print] BookData Not Found", className);
			throw new DataNotFoundException();
		}
		if (type.equals(PrintType.TICKET) && !bookData.getStatus().equals(BookingStatus.CONFIRMED)) {
			logger.error("[{}.print] Ticket not available for unconfirmed book", className);
			throw new RuntimeException();
		} else if (type.equals(PrintType.RECEIPT) && bookData.getStatus().equals(BookingStatus.CANCELED)) {
			logger.error("[{}.print] Receipt not available for canceled book", className);
			throw new RuntimeException();
		}
		String document;

		document = ticketingHelper.printReceipt(bookData);
		bookDataRepo.saveAndFlush(bookData);

		logger.info("[{}.print] Generate voucher complete", className);
		emailHelper.sendReceipt(bookingCode, document, bookData.getContactData().getEmail());
		return document;
	}

	@Override
	@Publish(allowAll = true)
	public String sendEmail(@NotBlank String email, @NotBlank String bookingCode) throws DataNotFoundException {
		var office = session.getOffice();
		var data = bookDataRepo.findByOfficeCodeAndBookingCode(office.getCode(), bookingCode);
		var bookData = data.get();
		if (data.isEmpty()) {
			logger.error("[{}.sendEmail] Booking Data Not Found", className);
			throw new DataNotFoundException();
		}
		if (!bookData.getStatus().equals(BookingStatus.CONFIRMED)) {
			logger.error("[{}.print] Receipt not available for unconfirmed book", className);
			throw new RuntimeException();
		} else if (bookData.getStatus().equals(BookingStatus.CANCELED)) {
			logger.error("[{}.print] Receipt not available for canceled book", className);
			throw new RuntimeException();
		}
		String document;

		document = ticketingHelper.printReceipt(bookData);
		bookDataRepo.saveAndFlush(bookData);

		logger.info("[{}.sendEmail] Send Email Complete", className);
		emailHelper.sendReceipt(bookingCode, document, email);
		return "Send Email Success";
	}

	@Override
	@Publish(allowAll = true)
	@Transactional(readOnly = true)
	public String exportTransactions(@NotNull Date from, @NotNull Date to) throws DataNotFoundException {
		logger.info("[{}.exportTransactions] request: {from={}, to={}}", this.getClass().getCanonicalName(), from, to);

		var fromStr = new SimpleDateFormat("dd-MMM-yyyy").format(from);
		var toStr = new SimpleDateFormat("dd-MMM-yyyy").format(to);
		logger.info("[{}.exportTransactions] from: {} to: {} by: {}", this.getClass().getCanonicalName(), fromStr,
				toStr, session.getUser().getPrincipal());
		Date toPlusOneDay = DateUtils.addDays(to, 1);
		List<BookData> datas = bookDataRepo.getAllByOfficeCodeAndBookDateAndStatusAndProductType(
				session.getOffice().getCode(), from, toPlusOneDay, Sort.by(Sort.Order.desc("bookDate")));

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet1");
		Row headerRow = sheet.createRow(0);
		List<String> headers = new ArrayList<String>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

		headers.addAll(Arrays.asList("Kode Pemesanan", "Nama Pelanggan", "Nomor Pelanggan", "Kode Referensi",
				"Jenis Produk", "Kode Produk", "Nama Produk", "Deskripsi Produk", "Tanggal Pemesanan",
				"Status Pemesanan", "Total Pembayaran", "Tanggal Pembayaran"));
		for (Integer i = 0; i < headers.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers.get(i));
		}

		int rowNum = 1;
		for (int i = 0; i < datas.size(); i++) {
			Row row = sheet.createRow(rowNum++);
			BookData dataIdx = datas.get(i);

			row.createCell(0).setCellValue(dataIdx.getBookingCode());
			if (dataIdx.getCustomerName() != null) {
				row.createCell(1).setCellValue(dataIdx.getCustomerName());
			} else {
				row.createCell(1).setCellValue("-");
			}
			row.createCell(2).setCellValue(dataIdx.getCustomerProductNumber());
			row.createCell(3).setCellValue(dataIdx.getRefNum());
			row.createCell(4).setCellValue(dataIdx.getProductDetail().getProductCode());
			row.createCell(5).setCellValue(dataIdx.getProductDetail().getCode());
			row.createCell(6).setCellValue(dataIdx.getProductDetail().getVoucherName());
			row.createCell(7).setCellValue(dataIdx.getProductDetail().getDescription());
			row.createCell(8).setCellValue(dateFormat.format(dataIdx.getBookDate()));
			row.createCell(9).setCellValue(dataIdx.getStatus().toString());
			if (dataIdx.getTotalPayment() != null) {
				row.createCell(10).setCellValue(numberFormat.format(dataIdx.getTotalPayment()).replace("Rp", "IDR "));
			} else {
				row.createCell(10).setCellValue("-");
			}
			if (dataIdx.getPaymentDate() != null) {
				row.createCell(11).setCellValue(dateFormat.format(dataIdx.getPaymentDate()));
			} else {
				row.createCell(11).setCellValue("-");
			}
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'_time_'HH:mm");
		String currentDateTime = LocalDateTime.now().format(dateTimeFormatter).replace(":", "-");
		FileOutputStream outputStream = null;
		String docs = null;
		String fileName = "transaction_list_" + currentDateTime + ".xlsx";

		try {
			outputStream = new FileOutputStream(fileName);
			workbook.write(outputStream);
			docs = convertWorkbookToBase64(workbook);
			workbook.close();
			outputStream.close();
			deleteFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docs;

	}

	private void deleteFile(String fileName) {
		try {
			Path filePath = Paths.get(fileName);
			Files.delete(filePath);
			logger.info("File deleted successfully: {}", fileName);
		} catch (IOException e) {
			logger.error("Error deleting file: {}", fileName, e);
		}
	}

	private String convertWorkbookToBase64(XSSFWorkbook workbook) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			byte[] excelBytes = bos.toByteArray();

			// Encode to Base64
			byte[] encodedBytes = Base64.getEncoder().encode(excelBytes);

			return new String(encodedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error("Error converting workbook to Base64", e);
			return "";
		}
	}

}
