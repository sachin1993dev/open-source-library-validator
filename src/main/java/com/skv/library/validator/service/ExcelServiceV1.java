package com.skv.library.validator.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class ExcelServiceV1 {

    public ByteArrayInputStream generateExcelReport(Map<String, Object> metrics) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("OSS Parameters");

            // Define parent headers and their associated metrics
            String[] parentHeaders = {
                    "Community",
                    "Legal",
                    "Meta",
                    "Reliability",
                    "Portability",
                    "Usability",
                    "Security"
            };

            // Updated metrics data: removed "License Type" from the "Community" section
            String[][] metricsData = {
                    {"Stars", "Forks", "Subscribers", "Watchers"},  // Community metrics
                    {"License Type", "License Name", "OsiApproved", "FsfLibre", "IsDeprecatedLicenseId"},  // Legal metrics
                    {"Owner", "Name", "Description", "Topics", "API URL","Recent release version"},  // Meta metrics
                    {"Age", "Last updated", "Created At", "Release count"},  // Reliability metrics
                    {"Language"},  // Portability metrics
                    {"Wiki", "Documentation","has_downloads"},  // Usability metrics
                    {"Total open Issue count","Issue count By Labels","Issue count By Assignees","Issue count By Date","Issue count By Comments"}  // Security metrics
            };

            int currentColumn = 0;

            // Create styles
            CellStyle parentHeaderStyle = workbook.createCellStyle();
            parentHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            parentHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font parentHeaderFont = workbook.createFont();
            parentHeaderFont.setColor(IndexedColors.WHITE.getIndex());
            parentHeaderFont.setBold(true);
            parentHeaderStyle.setFont(parentHeaderFont);
            parentHeaderStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            parentHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            parentHeaderStyle.setWrapText(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setWrapText(true);

            // Write parent headers in the first row
            Row parentHeaderRow = sheet.createRow(0);
            Row childMetricsRow = sheet.createRow(1);
            Row childValuesRow = sheet.createRow(2);

            for (int i = 0; i < parentHeaders.length; i++) {
                int metricsCount = metricsData[i].length;

                // Write parent header
                Cell parentHeaderCell = parentHeaderRow.createCell(currentColumn);
                parentHeaderCell.setCellValue(parentHeaders[i]);
                parentHeaderCell.setCellStyle(parentHeaderStyle);

                // Write metrics headers under parent header
                for (int j = 0; j < metricsCount; j++) {
                    Cell metricsHeaderCell = childMetricsRow.createCell(currentColumn + j);
                    metricsHeaderCell.setCellValue(metricsData[i][j]);
                    metricsHeaderCell.setCellStyle(headerStyle);
                }

                // Write metrics values under metrics headers
                for (int j = 0; j < metricsCount; j++) {
                    Cell metricsValueCell = childValuesRow.createCell(currentColumn + j);
                    metricsValueCell.setCellValue(metrics.getOrDefault(metricsData[i][j], "N/A").toString());
                    metricsValueCell.setCellStyle(dataStyle);
                }

                // Merge cells for the parent header if there are metrics
                if (metricsCount > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, currentColumn, currentColumn + metricsCount - 1));
                }

                // Move to the next column for the next parent header
                currentColumn += metricsCount;

                // Add a column separator if necessary (not strictly required here)
                currentColumn += 1; // Optional: Add space between groups if needed
            }

            // Auto-size columns for better visibility
            for (int i = 0; i < currentColumn; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\91990\\github_metrics.xlsx")) {
                workbook.write(fileOut);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
