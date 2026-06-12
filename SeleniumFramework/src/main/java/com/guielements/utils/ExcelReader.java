package com.guielements.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private static final Logger log = LogManager.getLogger(ExcelReader.class);

    // Returns all rows as a list of maps (header -> value)
    public static List<Map<String, String>> getAllData(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int c = 0; c < colCount; c++) {
                    String header = getCellValue(headerRow.getCell(c));
                    String value  = getCellValue(row.getCell(c));
                    rowMap.put(header, value);
                }
                dataList.add(rowMap);
            }
            log.info("Excel loaded: " + filePath + " | Sheet: " + sheetName + " | Rows: " + dataList.size());

        } catch (IOException e) {
            log.error("Excel read failed: " + e.getMessage());
            throw new RuntimeException("Excel file not found: " + filePath);
        }
        return dataList;
    }

    // Returns first data row as a map
    public static Map<String, String> getFirstRow(String filePath, String sheetName) {
        return getAllData(filePath, sheetName).get(0);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }
}
