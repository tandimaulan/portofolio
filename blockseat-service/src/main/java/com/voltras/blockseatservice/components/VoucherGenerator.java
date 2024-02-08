package com.voltras.blockseatservice.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.lowagie.text.pdf.codec.Base64;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class VoucherGenerator {
	public String generate(String template, Map<String, Object> parameters) {
		ClassPathResource rsc = new ClassPathResource("./" + template + ".jrxml");
		try {
			InputStream inputStream = rsc.getInputStream();
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			return exportPdf(jasperPrint);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] generateToBytes(String template, Map<String, Object> parameters) {
		ClassPathResource rsc = new ClassPathResource("./" + template + ".jrxml");
		try {
			InputStream inputStream = rsc.getInputStream();
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			return exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String exportPdf(JasperPrint jasperPrint) throws JRException, IOException {

		return Base64.encodeBytes(exportReportToPdf(jasperPrint)).replaceAll("\n", "");
	}

	protected byte[] exportReportToPdf(JasperPrint jasperPrint) throws JRException, IOException {
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
		exporter.exportReport();

		return bos.toByteArray();
	}

}