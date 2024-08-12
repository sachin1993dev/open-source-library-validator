package com.skv.library.validator.service;

import com.skv.library.validator.config.GitHubConfig;
import com.skv.library.validator.model.SpdxLicense;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExcelServiceV2 {

    @Autowired
    private GitHubConfig gitHubConfig;

    public void generateXLSX(List<String> owners, List<String> repos) throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + gitHubConfig.getToken());




        // Set up the entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("OSS Parameters");

        String[] parentHeaders = {
                 "Meta",
                "Community",
                "Legal",
                "Reliability",
                "Portability",
                "Usability",
                "Security"
        };

        // Updated metrics data: removed "License Type" from the "Community" section
        String[][] metricsData = {
                {"Owner", "Name", "Description", "Topics", "API URL","Recent release version"},
                {"Stars", "Forks", "Subscribers", "Watchers"}, // Community metrics
                {"License Type", "License Name", "OsiApproved", "FsfLibre", "IsDeprecatedLicenseId"},  // Legal metrics
                {"Age", "Last updated", "Created At", "Release count"},  // Reliability metrics
                {"Language"},  // Portability metrics
                {"Wiki","has_downloads","Documentation"},  // Usability metrics
                {"Total open Issue count","Issue count By Labels","Issue count By Assignees","Issue count By Date","Issue count By Comments"}  // Security metrics
        };

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
      //  dataStyle.setWrapText(true);

        Row parentHeaderRow = sheet.createRow(0);
        Row childMetricsRow = sheet.createRow(1);

        int currentColumn = 0;
        for(int i=0;i< parentHeaders.length; i++) {
            int metricsCount = metricsData[i].length;
            Cell parentHeaderCell = parentHeaderRow.createCell(currentColumn);
            parentHeaderCell.setCellValue(parentHeaders[i]);
            parentHeaderCell.setCellStyle(parentHeaderStyle);

            for (int j = 0; j < metricsCount; j++) {
                Cell metricsHeaderCell = childMetricsRow.createCell(currentColumn + j);
                metricsHeaderCell.setCellValue(metricsData[i][j]);
                metricsHeaderCell.setCellStyle(headerStyle);
            }
            // Merge cells for the parent header if there are metrics
            if (metricsCount > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, currentColumn, currentColumn + metricsCount - 1));
            }
            currentColumn += metricsCount;

            // Add a column separator if necessary (not strictly required here)
            currentColumn += 1;

            for (int j = 0; j < currentColumn; j++) {
                sheet.autoSizeColumn(j);
            }

        }

        GitHub github = new GitHubBuilder().withOAuthToken(gitHubConfig.getToken()).build();

        GHRepository repository;

        int rowNumber = 2;

        for (int i = 0; i < owners.size(); i++) {
            repository = github.getRepository(owners.get(i) + "/" + repos.get(i));
            Row valueRow = sheet.createRow(rowNumber++);
            try {
                Cell cell1 = valueRow.createCell(0);
                cell1.setCellValue(repository.getOwner().getName());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = valueRow.createCell(1);
                cell2.setCellValue(repository.getName());
                cell2.setCellStyle(dataStyle);


                Cell cell3 = valueRow.createCell(2);
                cell3.setCellValue(repository.getDescription());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = valueRow.createCell(3);
                cell4.setCellValue(repository.getUrl().toString());
                cell4.setCellStyle(dataStyle);

                Cell cell5 = valueRow.createCell(4);
                cell5.setCellValue(repository.listTopics().toString());
                cell5.setCellStyle(dataStyle);

                Cell cell6 = valueRow.createCell(5);
                cell6.setCellValue(repository.getLatestRelease().getTagName());
                cell6.setCellStyle(dataStyle);

                Cell cell8 = valueRow.createCell(7);
                cell8.setCellValue(repository.getStargazersCount());
                cell8.setCellStyle(dataStyle);

                Cell cell9 = valueRow.createCell(8);
                cell9.setCellValue(repository.getForksCount());
                cell9.setCellStyle(dataStyle);

                Cell cell10 = valueRow.createCell(9);
                cell10.setCellValue(repository.getSubscribersCount());
                cell10.setCellStyle(dataStyle);

                Cell cell11 = valueRow.createCell(10);
                cell11.setCellValue(repository.getWatchersCount());
                cell11.setCellStyle(dataStyle);

                Cell cell13 = valueRow.createCell(12);
                cell13.setCellValue(repository.getLicense().getKey());
                cell13.setCellStyle(dataStyle);

                Cell cell15 = valueRow.createCell(13);
                cell15.setCellValue(repository.getLicense().getName());
                cell15.setCellStyle(dataStyle);

                String licenseType = repository.getLicense().getKey();

                SpdxLicense spdxLicense = null;

                try {
                    if (null != licenseType && !licenseType.isBlank()) {
                        String spdxUrl = "https://spdx.org/licenses/" + licenseType.toUpperCase() + ".json";
                        spdxLicense = restTemplate.getForObject(spdxUrl, SpdxLicense.class);

                        Cell cell16 = valueRow.createCell(14);
                        cell16.setCellValue(spdxLicense.isOsiApproved());
                        cell16.setCellStyle(dataStyle);

                        Cell cell17 = valueRow.createCell(15);
                        cell17.setCellValue(spdxLicense.isFsfLibre());
                        cell17.setCellStyle(dataStyle);

                        Cell cell18 = valueRow.createCell(16);
                        cell18.setCellValue(spdxLicense.isDeprecatedLicenseId());
                        cell18.setCellStyle(dataStyle);
                    }
                } catch (Exception e) {

                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
                ZonedDateTime createdDate = ZonedDateTime.parse(repository.getCreatedAt().toString(), formatter);
                LocalDate createdDate1 = createdDate.toLocalDate();
                LocalDate currentDate = LocalDate.now();
                Period age = Period.between(createdDate1, currentDate);


                Cell cell20 = valueRow.createCell(18);
                cell20.setCellValue(age.getYears());
                cell20.setCellStyle(dataStyle);

                Cell cell21 = valueRow.createCell(19);
                cell21.setCellValue(repository.getCreatedAt().toString());
                cell21.setCellStyle(dataStyle);

                Cell cell22 = valueRow.createCell(20);
                cell22.setCellValue(repository.getUpdatedAt().toString());
                cell22.setCellStyle(dataStyle);

                Cell cell23 = valueRow.createCell(21);
                cell23.setCellValue(repository.listReleases().toList().size());
                cell23.setCellStyle(dataStyle);

                Cell cell25 = valueRow.createCell(23);
                cell25.setCellValue(repository.getLanguage());
                cell25.setCellStyle(dataStyle);

                Cell cell27 = valueRow.createCell(25);
                cell27.setCellValue(repository.hasWiki());
                cell27.setCellStyle(dataStyle);

                Cell cell28 = valueRow.createCell(26);
                cell28.setCellValue(repository.hasDownloads());
                cell28.setCellStyle(dataStyle);

                Cell cell29 = valueRow.createCell(27);
                cell29.setCellValue(repository.getReadme().getContent());
                cell29.setCellStyle(dataStyle);

                Cell cell31 = valueRow.createCell(29);
                cell31.setCellValue(repository.getOpenIssueCount());
                cell31.setCellStyle(dataStyle);


                List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);
                // Classify and count by labels
                Map<String, Long> labelsCount = issues.stream()
                        .flatMap(issue -> issue.getLabels().stream().map(label -> label.getName()))
                        .collect(Collectors.groupingBy(label -> label, Collectors.counting()));

                Cell cell32 = valueRow.createCell(30);
                cell32.setCellValue(labelsCount.toString());
                cell32.setCellStyle(dataStyle);

                // Classify and count by assignees
                Map<String, Long> assigneesCount = issues.stream()
                        .flatMap(issue -> issue.getAssignees().stream().map(assignee -> assignee.getLogin()))
                        .collect(Collectors.groupingBy(assignee -> assignee, Collectors.counting()));

                Cell cell33 = valueRow.createCell(31);
                cell33.setCellValue(assigneesCount.toString());
                cell33.setCellStyle(dataStyle);


                // Classify and count by date
                Map<String, Long> dateCount = issues.stream()
                        .collect(Collectors.groupingBy(issue -> {
                            long daysOld = 0;
                            try {
                                daysOld = (System.currentTimeMillis() - issue.getCreatedAt().getTime()) / (1000 * 60 * 60 * 24);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (daysOld <= 7) return "Last 7 days";
                            if (daysOld <= 30) return "Last 30 days";
                            return "Older than 30 days";
                        }, Collectors.counting()));

                Cell cell34 = valueRow.createCell(32);
                cell34.setCellValue(dateCount.toString());
                cell34.setCellStyle(dataStyle);

                // Classify and count by comments
                Map<String, Long> commentsCount = issues.stream()
                        .collect(Collectors.groupingBy(issue -> {
                            int comments = issue.getCommentsCount();
                            if (comments == 0) return "No Comments";
                            if (comments <= 5) return "1-5 Comments";
                            return "More than 5 Comments";
                        }, Collectors.counting()));
                Cell cell35 = valueRow.createCell(33);
                cell35.setCellValue(commentsCount.toString());
                cell35.setCellStyle(dataStyle);
            } catch (Exception e) {

            }
        }

        for (int i = 0; i < sheet.getRow(1).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream("C:/Users/91990/report_new.xlsx")) {
            workbook.write(fos);
        }

        workbook.close();
    }
}
