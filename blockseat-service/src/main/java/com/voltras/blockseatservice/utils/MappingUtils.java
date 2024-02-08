package com.voltras.blockseatservice.utils;

import org.apache.poi.ss.usermodel.Cell;

public class MappingUtils {
	public static String getCellValue(Cell cell, boolean numericAsString) {
		return cell == null ? "" : switch (cell.getCellType()) {
		case NUMERIC ->
			numericAsString ? String.valueOf(cell.getNumericCellValue()).replace("0.", "'0").replace(".", "")
					: String.valueOf(cell.getNumericCellValue());
		case STRING -> cell.getStringCellValue();
		case BLANK -> "";
		case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
		default -> throw new IllegalArgumentException("Unexpected value: " + cell.getCellType());
		};
	}

	public static void setCellValue(Cell cell, String value) {
		cell.setCellValue(value);
	}

	public static void setCellValue(Cell cell, Double value) {
		cell.setCellValue(value);
	}
}