package com.skv.library.validator.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skv.library.validator.config.GitHubConfig;
import com.skv.library.validator.model.RepositoryMetrics;
import com.skv.library.validator.model.SpdxLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private GitHubConfig gitHubConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getGitHubData(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + gitHubConfig.getToken());
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    public RepositoryMetrics getRepositoryMetrics(String owner, String repo) throws IOException {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        String repoData = getGitHubData(repoUrl);

        RepositoryMetrics metrics = parseRepositoryData(repoData);

        // Fetch topics/tags
        String topicsUrl = repoUrl + "/topics";
        String topicsData = getGitHubData(topicsUrl);
        metrics.setTopics(parseTopics(topicsData));

        // Fetch additional usability data
        metrics.setDocumentation(fetchDocumentationStatus(owner, repo));
        metrics.setSubscriberCount(fetchSubscriberCount(owner,repo));
        metrics.setWiki(fetchWikiStatus(owner, repo));
       // metrics.setStackExchangeQAs(fetchStackExchangeQAs(owner, repo));
       // metrics.setReadmeQuality(fetchReadmeQuality(owner, repo));
      //  metrics.setExamplesAndTutorials(fetchExamplesAndTutorials(owner, repo));
      //  metrics.setIssueResponseTime(fetchIssueResponseTime(owner, repo));
       // metrics.setPullRequestResponseTime(fetchPullRequestResponseTime(owner, repo));
        metrics.setContributingGuide(fetchContributingGuide(owner, repo));
        metrics.setCodeOfConduct(fetchCodeOfConduct(owner, repo));
       // metrics.setApiDocumentation(fetchApiDocumentation(owner, repo));
       // metrics.setCommunityActivity(fetchCommunityActivity(owner, repo));
       /// metrics.setEaseOfSetup(fetchEaseOfSetup(owner, repo));

        // Fetch additional reliability data
      //  metrics.setAverageTimeToRelease(fetchAverageTimeToRelease(owner, repo));
        metrics.setLastUpdated(fetchLastUpdated(owner, repo));
        metrics.setOpenIssues(fetchOpenIssues(owner, repo));
        metrics.setClosedIssues(fetchClosedIssues(owner, repo));
        metrics.setActiveRecentReleases(fetchActiveRecentReleases(owner, repo));
        metrics.setCommitActivity(fetchCommitActivity(owner, repo));
       // metrics.setCommitMaturity(fetchCommitMaturity(owner, repo));
       /// metrics.setCommitEvolution(fetchCommitEvolution(owner, repo));
      //  metrics.setTestCoverage(fetchTestCoverage(owner, repo));
      //  metrics.setBuildStatus(fetchBuildStatus(owner, repo));
      //  metrics.setDependencyUpdates(fetchDependencyUpdates(owner, repo));
       // metrics.setSecurityIssues(fetchSecurityIssues(owner, repo));
       // metrics.setReleaseStability(fetchReleaseStability(owner, repo));

        // Fetch additional community data
       // metrics.setCommunitySize(fetchCommunitySize(owner, repo));
        metrics.setContributorsCount(fetchContributorsCount(owner, repo));
        metrics.setCommitsCount(fetchCommitsCount(owner, repo));
        metrics.setPullRequestsCount(fetchPullRequestsCount(owner, repo));
        metrics.setIssuesCount(fetchIssuesCount(owner, repo));
        metrics.setForksCount(fetchForksCount(owner, repo));
        metrics.setWatchersCount(fetchWatchersCount(owner, repo));

        // Fetch additional legal data
        metrics.setLicense(fetchLicense(owner, repo));
        metrics.setNameLicense(fetchLicenseName(owner, repo));
        metrics.setSpdxLicense(fetchLicenseInfo(owner,repo));


        // Fetch additional meta data
        metrics.setLanguage(fetchLanguage(owner, repo));

        // Fetch additional portability data
       // metrics.setPlatformCompatibility(fetchPlatformCompatibility(owner, repo));
       // metrics.setBuildInstructions(fetchBuildInstructions(owner, repo));
       // metrics.setInstallationInstructions(fetchInstallationInstructions(owner, repo));
      //  metrics.setUsageExamples(fetchUsageExamples(owner, repo));

        return metrics;
    }

    private RepositoryMetrics parseRepositoryData(String data) throws IOException {
        JsonNode node = objectMapper.readTree(data);
        RepositoryMetrics metrics = new RepositoryMetrics();

        metrics.setStargazersCount(node.path("stargazers_count").asInt());
        metrics.setForksCount(node.path("forks_count").asInt());
        metrics.setWatchersCount(node.path("watchers_count").asInt());
        metrics.setOwner(node.path("owner").path("login").asText());
        metrics.setName(node.path("name").asText());
        metrics.setDescription(node.path("description").asText());
        metrics.setApiUrl(node.path("url").asText());

        // Calculate the age of the repository
        String creationDateStr = node.path("created_at").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        LocalDate creationDate = LocalDate.parse(creationDateStr, formatter.withZone(java.time.ZoneId.of("UTC")));
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(creationDate, currentDate);
        String ageString = age.getYears() + " years, " + age.getMonths() + " months";
        metrics.setAge(ageString);

        return metrics;
    }

    private String[] parseTopics(String data) throws IOException {
        JsonNode node = objectMapper.readTree(data);
        JsonNode topicsNode = node.path("names");
        List<String> topics = new ArrayList<>();
        for (JsonNode topic : topicsNode) {
            topics.add(topic.asText());
        }
        return topics.toArray(new String[0]);
    }

    // Usability methods
    private String fetchDocumentationStatus(String owner, String repo) {
        // Check if the repository contains a documentation file (e.g., a README)
        String readmeUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/";
        try {
            String data = getGitHubData(readmeUrl);
            return data.contains("readme") ? "Available" : "Not Available";
        } catch (Exception e) {
            return "Not Available";
        }
    }

    private String fetchWikiStatus(String owner, String repo) {
        // Check if the wiki is enabled for the repository
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        String data = getGitHubData(repoUrl);
        JsonNode node;
        try {
            node = objectMapper.readTree(data);
            boolean hasWiki = node.path("has_wiki").asBoolean();
            return hasWiki ? "Enabled" : "Disabled";
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private String fetchSubscriberCount(String owner, String repo) {
        // Check if the wiki is enabled for the repository
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        String data = getGitHubData(repoUrl);
        JsonNode node;
        try {
            node = objectMapper.readTree(data);
            return node.path("subscribers_count").asText();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    /*private int fetchStackExchangeQAs(String owner, String repo) {
        // Placeholder method; actual implementation would require querying Stack Exchange API or other sources
        return 50; // Placeholder
    }

    private String fetchReadmeQuality(String owner, String repo) {
        // Placeholder method; actual implementation would require analyzing README content
        return "Good"; // Placeholder
    }

    private String fetchExamplesAndTutorials(String owner, String repo) {
        // Placeholder method; actual implementation would require checking repository for examples and tutorials
        return "Extensive"; // Placeholder
    }

    private String fetchIssueResponseTime(String owner, String repo) {
        // Placeholder method; actual implementation would require calculating average issue response time
        return "2 days"; // Placeholder
    }

    private String fetchPullRequestResponseTime(String owner, String repo) {
        // Placeholder method; actual implementation would require calculating average pull request response time
        return "3 days"; // Placeholder
    }*/

    private String fetchContributingGuide(String owner, String repo) {
        // Check if a CONTRIBUTING.md file exists
        String contributingUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/CONTRIBUTING.md";
        try {
            String data = getGitHubData(contributingUrl);
            return data.contains("CONTRIBUTING.md") ? "Available" : "Not Available";
        } catch (Exception e) {
            return "Not Available";
        }
    }

    private String fetchCodeOfConduct(String owner, String repo) {
        // Check if a CODE_OF_CONDUCT.md file exists
        String codeOfConductUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/CODE_OF_CONDUCT.md";
        try {
            String data = getGitHubData(codeOfConductUrl);
            return data.contains("CODE_OF_CONDUCT.md") ? "Available" : "Not Available";
        } catch (Exception e) {
            return "Not Available";
        }
    }

    /*private String fetchApiDocumentation(String owner, String repo) {
        // Placeholder method; actual implementation would require checking for API documentation presence
        return "Detailed"; // Placeholder
    }

    private String fetchCommunityActivity(String owner, String repo) {
        // Placeholder method; actual implementation would require checking recent activities
        return "High"; // Placeholder
    }

    private String fetchEaseOfSetup(String owner, String repo) {
        // Placeholder method; actual implementation would require checking setup instructions
        return "Easy"; // Placeholder
    }

    // Reliability methods
    private String fetchAverageTimeToRelease(String owner, String repo) {
        // Placeholder method; actual implementation would require calculating average time to release
        return "1 month"; // Placeholder
    }*/

    private String fetchLastUpdated(String owner, String repo) {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        String data = getGitHubData(repoUrl);
        JsonNode node;
        try {
            node = objectMapper.readTree(data);
            return node.path("updated_at").asText();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private int fetchOpenIssues(String owner, String repo) {
        String issuesUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/issues?state=open";
        try {
            String data = getGitHubData(issuesUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private int fetchClosedIssues(String owner, String repo) {
        String issuesUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/issues?state=closed";
        try {
            String data = getGitHubData(issuesUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private String fetchActiveRecentReleases(String owner, String repo) {
        String releasesUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";
        try {
            String data = getGitHubData(releasesUrl);
            JsonNode node = objectMapper.readTree(data);
            return "Recent releases: " + node.size();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private String fetchCommitActivity(String owner, String repo) {
        String commitsUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/commits";
        try {
            String data = getGitHubData(commitsUrl);
            JsonNode node = objectMapper.readTree(data);
            return "Recent commits: " + node.size();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    /*private String fetchCommitMaturity(String owner, String repo) {
        // Placeholder method; actual implementation would require analyzing commit history
        return "Stable"; // Placeholder
    }

    private String fetchCommitEvolution(String owner, String repo) {
        // Placeholder method; actual implementation would require analyzing commit patterns
        return "Increasing"; // Placeholder
    }

    private String fetchTestCoverage(String owner, String repo) {
        // Placeholder method; actual implementation would require checking test coverage reports
        return "80%"; // Placeholder
    }

    private String fetchBuildStatus(String owner, String repo) {
        // Placeholder method; actual implementation would require checking build status from CI tools
        return "Passing"; // Placeholder
    }

    private String fetchDependencyUpdates(String owner, String repo) {
        // Placeholder method; actual implementation would require checking dependency update frequency
        return "Regular"; // Placeholder
    }*/

    private String fetchSecurityIssues(String owner, String repo) {
        String vulnerabilityUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/vulnerability-alerts";
        try {
            String data = getGitHubData(vulnerabilityUrl);
            JsonNode node = objectMapper.readTree(data);
            return "Security issues: " + node.size();
        } catch (IOException e) {
            return "Unknown";
        }
    }

   /* private String fetchReleaseStability(String owner, String repo) {
        // Placeholder method; actual implementation would require analyzing release stability
        return "High"; // Placeholder
    }

    // Community methods
    private String fetchCommunitySize(String owner, String repo) {
        // Placeholder method; actual implementation would require checking community size indicators
        return "Large"; // Placeholder
    }*/

    private int fetchContributorsCount(String owner, String repo) {
        String contributorsUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contributors";
        try {
            String data = getGitHubData(contributorsUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private int fetchCommitsCount(String owner, String repo) {
        String commitsUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/commits";
        try {
            String data = getGitHubData(commitsUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private int fetchPullRequestsCount(String owner, String repo) {
        String pullRequestsUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls";
        try {
            String data = getGitHubData(pullRequestsUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private int fetchIssuesCount(String owner, String repo) {
        String issuesUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/issues";
        try {
            String data = getGitHubData(issuesUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private int fetchForksCount(String owner, String repo) {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        try {
            String data = getGitHubData(repoUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.path("forks_count").asInt();
        } catch (IOException e) {
            return 0;
        }
    }

    private int fetchWatchersCount(String owner, String repo) {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        try {
            String data = getGitHubData(repoUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.path("watchers_count").asInt();
        } catch (IOException e) {
            return 0;
        }
    }

    // Legal methods
    private String fetchLicense(String owner, String repo) {
        String licenseUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/license";
        try {
            String data = getGitHubData(licenseUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.path("license").path("spdx_id").asText();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private SpdxLicense fetchLicenseInfo(String owner, String repo){
        String licenseUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/license";
        try {
            String data = getGitHubData(licenseUrl);
            JsonNode node = objectMapper.readTree(data);
            String spdx_id = node.path("license").path("spdx_id").asText();
            String spdxUrl = "https://spdx.org/licenses/" + spdx_id + ".json";
            SpdxLicense spdxLicense = restTemplate.getForObject(spdxUrl, SpdxLicense.class);

            return spdxLicense;

        } catch (IOException e) {
            return new SpdxLicense();
        }
    }

    private String fetchLicenseName(String owner, String repo) {
        String licenseUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/license";
        try {
            String data = getGitHubData(licenseUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.path("license").path("name").asText();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    // Meta methods
    private String fetchLanguage(String owner, String repo) {
        String repoUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        try {
            String data = getGitHubData(repoUrl);
            JsonNode node = objectMapper.readTree(data);
            return node.path("language").asText();
        } catch (IOException e) {
            return "Unknown";
        }
    }

    // Portability methods
  /*  private String fetchPlatformCompatibility(String owner, String repo) {
        // Placeholder method; actual implementation would require checking platform compatibility
        return "Windows, Mac, Linux"; // Placeholder
    }

    private String fetchBuildInstructions(String owner, String repo) {
        // Placeholder method; actual implementation would require checking build instructions in repository
        return "Available"; // Placeholder
    }

    private String fetchInstallationInstructions(String owner, String repo) {
        // Placeholder method; actual implementation would require checking installation instructions in repository
        return "Available"; // Placeholder
    }

    private String fetchUsageExamples(String owner, String repo) {
        // Placeholder method; actual implementation would require checking usage examples in repository
        return "Available"; // Placeholder
    }*/
}
