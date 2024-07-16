package com.skv.library.validator.serviceImpl;

import com.skv.library.validator.model.GitHubRepo;
import com.skv.library.validator.service.GitHubService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelReportService {

    @Autowired
    private GitHubService gitHubService;

    public byte[] generateReport(List<String> owners, List<String> repoNames) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("GitHub Repositories");

        // Create header row
        String[] headers = {"Owner", "Repository", "Description", "Language", "Created At", "Updated At", "Pushed At", "Size", "License",
                "Stars", "Forks", "Watchers", "Open Issues", "Total Commits", "Open PRs", "Closed PRs", "Merged PRs",
                "Issues", "Contributors", "Releases", "ReadMe", "Branches", "Tags", "Topics", "Code Frequency",
                "Contributor Stats", "Commit Counts", "Clones", "Views", "network_count", "subscribers_count"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Fetch details and populate rows
        int rowNum = 1;
        for (int i = 0; i < owners.size(); i++) {
            GitHubRepo repo = gitHubService.fetchRepositoryInfo(owners.get(i), repoNames.get(i));
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(owners.get(i));
            row.createCell(1).setCellValue(repo.getFullName());
            row.createCell(2).setCellValue(repo.getDescription());
            row.createCell(3).setCellValue(repo.getLanguage());
            row.createCell(4).setCellValue(repo.getCreated_at());
            row.createCell(5).setCellValue(repo.getUpdated_at());
            row.createCell(6).setCellValue(repo.getPushed_at());
            row.createCell(7).setCellValue(repo.getSize());
            row.createCell(8).setCellValue(repo.getLicense().getName());
            row.createCell(9).setCellValue(repo.getStargazersCount());
            row.createCell(10).setCellValue(repo.getForks_count());
            row.createCell(11).setCellValue(repo.getWatchers_count());
            row.createCell(12).setCellValue(repo.getOpen_issues_count());
            row.createCell(13).setCellValue(repo.getTotalCommits());
            row.createCell(14).setCellValue(repo.getPullRequests().get("open"));
            row.createCell(15).setCellValue(repo.getPullRequests().get("closed"));
            row.createCell(16).setCellValue(repo.getPullRequests().get("merged"));

            if(null!=repo.getIssues()) {
                row.createCell(17).setCellValue(repo.getIssues().size());
            }



            if(null!=repo.getContributors()) {
                row.createCell(18).setCellValue(repo.getContributors().size());
            }
            if(null!=repo.getContributors()) {
                row.createCell(19).setCellValue(repo.getContributors().size());
            }


            row.createCell(20).setCellValue(repo.getReadmeContent());

            if(null!=repo.getBranches()) {
                row.createCell(21).setCellValue(repo.getBranches().size());
            }

            if(null!=repo.getTags()) {
                row.createCell(22).setCellValue(repo.getTags().size());
            }

            if(null!=repo.getTopics()) {
                row.createCell(23).setCellValue(repo.getTopics().size());

            }



            if(null!=repo.getCodeFrequency()) {
                row.createCell(24).setCellValue(repo.getCodeFrequency().size());
            }


                row.createCell(26).setCellValue(repo.getTotalCommits());




            if(null!=repo.getClones()) {
                row.createCell(27).setCellValue(repo.getClones().getCount());
            }

            if(null!=repo.getViews()) {
                row.createCell(28).setCellValue(repo.getViews().getCount());
            }
            if(null!=repo.getSubscribers_count()) {
                row.createCell(29).setCellValue(repo.getSubscribers_count());
            }
            if(null!=repo.getNetwork_count()) {
                row.createCell(30).setCellValue(repo.getNetwork_count());
            }








        }

        // Write the output to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);

        try (FileOutputStream fos = new FileOutputStream("C:/Users/91990/report.xlsx")) {
            workbook.write(fos);
        }


        workbook.close();
        return bos.toByteArray();
    }
}
