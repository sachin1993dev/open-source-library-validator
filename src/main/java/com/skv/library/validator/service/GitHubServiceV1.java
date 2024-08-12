package com.skv.library.validator.service;

import com.skv.library.validator.config.GitHubConfig;
import com.skv.library.validator.model.ReadMe;
import com.skv.library.validator.model.Release;
import com.skv.library.validator.model.SpdxLicense;
import org.json.JSONObject;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitHubServiceV1 {

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private ReportService reportService;

    @Autowired
    private GitHubService gitHubService;

    private final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}";

    @Autowired
    private GitHubConfig gitHubConfig;

    public Map<String, Object> fetchRepositoryMetrics(String owner, String repo) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String url = GITHUB_API_URL.replace("{owner}", owner).replace("{repo}", repo);

        // Set up headers with the GitHub token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);


        // Set up the entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API request
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String response = responseEntity.getBody();

        String content = extractReadMe(restTemplate, url, entity);

        Release release = getLatestRelease(restTemplate, url, entity);

        JSONObject jsonObject = new JSONObject(response);

        // String spdxUrl = "https://spdx.org/licenses/" + spdx_id + ".json";
        // SpdxLicense spdxLicense = restTemplate.getForObject(spdxUrl, SpdxLicense.class);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("Stars", jsonObject.getInt("stargazers_count"));
        metrics.put("Forks", jsonObject.getInt("forks_count"));
        metrics.put("Subscribers", jsonObject.optInt("subscribers_count", 0));
        metrics.put("Watchers", jsonObject.optInt("watchers_count", 0));
        metrics.put("has_downloads", jsonObject.optBoolean("has_downloads", false));

        getOpenIssueCountUsingGitClient(owner, repo, metrics);
        // License Type
        JSONObject license = jsonObject.optJSONObject("license");
        String licenseName = "";
        SpdxLicense spdxLicense = null;
        if (null != license && null != license.get("spdx_id")) {
            String spdxUrl = "https://spdx.org/licenses/" + license.get("spdx_id") + ".json";
            spdxLicense = restTemplate.getForObject(spdxUrl, SpdxLicense.class);
        }

        if (null != license && null != license.get("name")) {
            licenseName = license.getString("name");
        }

        metrics.put("License Type", spdxLicense.getLicenseId());

        // Additional Metrics
        metrics.put("License Name", licenseName);
        metrics.put("OsiApproved", spdxLicense.isOsiApproved());
        metrics.put("FsfLibre", spdxLicense.isFsfLibre());
        metrics.put("IsDeprecatedLicenseId", spdxLicense.isDeprecatedLicenseId());

        metrics.put("Owner", jsonObject.optJSONObject("owner").optString("login", "Unknown"));
        metrics.put("Name", jsonObject.optString("name", "Unknown"));
        metrics.put("Description", jsonObject.optString("description", "No description"));
        metrics.put("Topics", jsonObject.optJSONArray("topics") != null ? jsonObject.optJSONArray("topics").toList() : "No topics");
        metrics.put("API URL", url);
        metrics.put("Recent release version", release.getTagName());

        String createdAt = jsonObject.optString("created_at", "Unknown");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDate creationDate = LocalDate.parse(createdAt, formatter);
        int age = Period.between(creationDate, LocalDate.now()).getYears();

        metrics.put("Age", age);
        metrics.put("Last updated", jsonObject.optString("updated_at", "Unknown"));
        metrics.put("Created At", jsonObject.optString("created_at", "Unknown"));

        metrics.put("Language", jsonObject.optString("language", "Unknown"));

        metrics.put("Wiki", jsonObject.optString("has_wiki", "Unknown"));
        metrics.put("Documentation", content);


        metrics.put("Total open Issue count", jsonObject.optString("open_issues_count", "0"));

        classifyAndCountIssuesByAssignees(owner,repo,metrics);

        return metrics;
    }

    private static String extractReadMe(RestTemplate restTemplate, String url, HttpEntity<String> entity) {
        String urlReadMe = url + "/readme";

        ResponseEntity<ReadMe> responseReadMe = restTemplate.exchange(urlReadMe, HttpMethod.GET, entity, ReadMe.class);

        String content = null;

        if (responseReadMe.getStatusCode() == HttpStatus.OK && responseReadMe.getBody() != null) {
            String encoded = responseReadMe.getBody().getContent();
            content = new String(Base64.getDecoder().decode(encoded.replace("\n", "").replace("\r", "")));
        }
        return content;
    }

    public Release getLatestRelease(RestTemplate restTemplate, String url, HttpEntity<String> entity) {
        String urlRelease = url + "/releases/latest";
        ResponseEntity<Release> reponseRelease = restTemplate.exchange(urlRelease, HttpMethod.GET, entity, Release.class);
        return reponseRelease.getBody();
    }

    public void getOpenIssueCountUsingGitClient(String owner, String repo, Map<String, Object> metrics) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(gitHubConfig.getToken()).build();
        GHRepository repository = github.getRepository(owner + "/" + repo);
        metrics.put("Release count", repository.listReleases().toList().size());
    }


    public void classifyAndCountIssuesByAssignees(String owner, String repo, Map<String, Object> metrics) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(gitHubConfig.getToken()).build();
        GHRepository repository = github.getRepository(owner + "/" + repo);

        List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);

        // Classify and count by labels
        Map<String, Long> labelsCount = issues.stream()
                .flatMap(issue -> issue.getLabels().stream().map(label -> label.getName()))
                .collect(Collectors.groupingBy(label -> label, Collectors.counting()));

        // Classify and count by assignees
        Map<String, Long> assigneesCount = issues.stream()
                .flatMap(issue -> issue.getAssignees().stream().map(assignee -> assignee.getLogin()))
                .collect(Collectors.groupingBy(assignee -> assignee, Collectors.counting()));

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

        // Classify and count by comments
        Map<String, Long> commentsCount = issues.stream()
                .collect(Collectors.groupingBy(issue -> {
                    int comments = issue.getCommentsCount();
                    if (comments == 0) return "No Comments";
                    if (comments <= 5) return "1-5 Comments";
                    return "More than 5 Comments";
                }, Collectors.counting()));

        metrics.put("Issue count By Labels", labelsCount);
        metrics.put("Issue count By Assignees", assigneesCount);
        metrics.put("Issue count By Date", dateCount);
        metrics.put("Issue count By Comments", commentsCount);
    }
}
