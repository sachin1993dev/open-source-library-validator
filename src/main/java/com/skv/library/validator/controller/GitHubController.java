package com.skv.library.validator.controller;


import com.skv.library.validator.service.ExcelServiceV1;
import com.skv.library.validator.service.GitHubService1;
import com.skv.library.validator.service.GitHubServiceV1;
import com.skv.library.validator.serviceImpl.ExcelReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService1 gitHubService1;
    @Autowired
    private GitHubServiceV1  gitHubServiceV1;

    @Autowired
    public ExcelServiceV1 excelServiceV1;

    private final ExcelReportService excelReportService;
    @Autowired
    public GitHubController(GitHubService1 gitHubService1, ExcelReportService excelReportService) {
        this.gitHubService1 = gitHubService1;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/validate/{owner}/{repo}")
    public String validateRepository(@PathVariable String owner, @PathVariable String repo) {
        boolean isGoodToUse = gitHubService1.isGoodToUse(owner, repo);
        if (isGoodToUse) {
            return "The GitHub repository " + owner + "/" + repo + " is good to use!";
        } else {
            return "The GitHub repository " + owner + "/" + repo + " does not meet the criteria.";
        }
    }

    @GetMapping("/api/report")
    public ResponseEntity<byte[]> generateExcelReport(
            @RequestParam List<String> owners,
            @RequestParam List<String> repoNames) {
        try {
            byte[] report = excelReportService.generateReport(owners, repoNames);

            String filePath = "C:/path/to/your/file.xlsx";


            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=report.xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(report);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/api/report/v2")
    public ResponseEntity<byte[]> generateExcelReportV2(
            @RequestParam String owners,
            @RequestParam String repoNames) {
        try {
            byte[] report = excelReportService.generateReportClassification(owners, repoNames);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=report.xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(report);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<InputStreamResource> getMetrics( @RequestParam String owners,
                                                           @RequestParam String repoNames) {
        try {
            Map<String, Object> metrics = gitHubServiceV1.fetchRepositoryMetrics(owners, repoNames);
            ByteArrayInputStream in = excelServiceV1.generateExcelReport(metrics);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=github_metrics.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
