package com.skv.library.validator.service;

import com.skv.library.validator.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitHubService {


    private final RestTemplate restTemplate;

    public  GitHubService() {
        this.restTemplate = new RestTemplate();
        // Set OAuth token in Authorization header
        this.restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + "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");
            return execution.execute(request, body);
        }));
    }

    @Value("${github.api.url}")
    private String githubApiUrl;



    public GitHubRepo fetchRepositoryInfo(String owner, String repoName) {
        GitHubRepo repo = new GitHubRepo();

        // Fetch basic information
        String repoUrl = String.format("%s/repos/%s/%s", githubApiUrl, owner, repoName);
        ResponseEntity<GitHubRepo> response = restTemplate.getForEntity(repoUrl, GitHubRepo.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            repo = response.getBody();
        }

        // Additional information
        repo.setTotalCommits(fetchTotalCommits(owner, repoName));
        repo.setPullRequests(fetchPullRequests(owner, repoName));
        repo.setIssues(fetchIssues(owner, repoName));
        repo.setContributors(fetchContributors(owner, repoName));
        repo.setReleases(fetchReleases(owner, repoName));
        repo.setReadmeContent(fetchReadmeContent(owner, repoName));
        repo.setBranches(fetchBranches(owner, repoName));
        repo.setTags(fetchTags(owner, repoName));
        repo.setTopics(fetchTopics(owner, repoName));
      //  repo.setCodeFrequency(fetchCodeFrequency(owner, repoName));
        repo.setContributorStats(fetchContributorStats(owner, repoName));
        repo.setParticipation(fetchParticipation(owner, repoName));
      //  repo.setClones(fetchClones(owner, repoName));
     //   repo.setViews(fetchViews(owner, repoName));

        return repo;
    }

    private int fetchTotalCommits(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/stats/commit_activity", githubApiUrl, owner, repoName);
        ResponseEntity<List<CommitActivity>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<CommitActivity>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().stream().mapToInt(CommitActivity::getTotalCommits).sum();
        }
        return 0;
    }

    private Map<String, Integer> fetchPullRequests(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/pulls?state=all", githubApiUrl, owner, repoName);
        ResponseEntity<List<PullRequest>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<PullRequest>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            List<PullRequest> pullRequests = response.getBody();
            int openCount = 0, closedCount = 0, mergedCount = 0;
            for (PullRequest pr : pullRequests) {
                if (pr.getState().equalsIgnoreCase("open")) {
                    openCount++;
                } else if (pr.getState().equalsIgnoreCase("closed")) {
                    closedCount++;
                    if (pr.isMerged()) {
                        mergedCount++;
                    }
                }
            }
            // Create a map with the counts
            return Map.of("open", openCount, "closed", closedCount, "merged", mergedCount);
        }
        return Map.of("open", 0, "closed", 0, "merged", 0);
    }

    private List<Issue> fetchIssues(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/issues?state=all", githubApiUrl, owner, repoName);
        ResponseEntity<List<Issue>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Issue>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return List.of();
    }

    private List<Contributor> fetchContributors(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/contributors", githubApiUrl, owner, repoName);
        ResponseEntity<List<Contributor>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Contributor>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return List.of();
    }

    private List<Release> fetchReleases(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/releases", githubApiUrl, owner, repoName);
        ResponseEntity<List<Release>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Release>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return List.of();
    }

    private String fetchReadmeContent(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/readme", githubApiUrl, owner, repoName);
        ResponseEntity<ReadMe> response = restTemplate.exchange(url, HttpMethod.GET, null, ReadMe.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getContent();
        }
        return "";
    }

    private List<String> fetchBranches(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/branches", githubApiUrl, owner, repoName);
        ResponseEntity<List<Branch>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Branch>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Branch> branches = response.getBody();
            return branches.stream().map(Branch::getName).collect(Collectors.toList());
        }
        return List.of();
    }

    private List<Tag> fetchTags(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/tags", githubApiUrl, owner, repoName);
        ResponseEntity<List<Tag>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Tag>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return List.of();
    }

    private List<String> fetchTopics(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/topics", githubApiUrl, owner, repoName);
        ResponseEntity<Topics> response = restTemplate.exchange(url, HttpMethod.GET, null, Topics.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getNames();
        }
        return List.of();
    }

  /*  private List<CodeFrequency> fetchCodeFrequency(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/stats/code_frequency", githubApiUrl, owner, repoName);
        ResponseEntity<List<CodeFrequency>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<CodeFrequency>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return List.of();
    }*/

    private List<ContributorStats> fetchContributorStats(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/stats/contributors", githubApiUrl, owner, repoName);
        ResponseEntity<List<ContributorStats>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ContributorStats>>() {});
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return List.of();
    }

    private Participation fetchParticipation(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/stats/participation", githubApiUrl, owner, repoName);
        ResponseEntity<Participation> response = restTemplate.exchange(url, HttpMethod.GET, null, Participation.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        return new Participation();
    }

    private Traffic fetchClones(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/traffic/clones", githubApiUrl, owner, repoName);
        ResponseEntity<Traffic> response = restTemplate.exchange(url, HttpMethod.GET, null, Traffic.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        return new Traffic();
    }

    private Traffic fetchViews(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/traffic/views", githubApiUrl, owner, repoName);
        ResponseEntity<Traffic> response = restTemplate.exchange(url, HttpMethod.GET, null, Traffic.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        return new Traffic();
    }
}
