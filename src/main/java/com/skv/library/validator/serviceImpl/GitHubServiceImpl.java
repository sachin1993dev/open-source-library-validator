package com.skv.library.validator.serviceImpl;

import com.skv.library.validator.config.ThresholdsProperties;
import com.skv.library.validator.model.*;
import com.skv.library.validator.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class GitHubServiceImpl implements GitHubService {

    private final RestTemplate restTemplate;
    private final ThresholdsProperties thresholds;

    @Autowired
    public GitHubServiceImpl(RestTemplate restTemplate, ThresholdsProperties thresholds) {
        this.restTemplate = restTemplate;
        this.thresholds = thresholds;
    }

    @Override
    public boolean isGoodToUse(String owner, String repo) {
        RepositoryInfo repoInfo = getRepositoryInfo(owner, repo);
        Contributor[] contributors = getRepositoryContributors(owner, repo);
        Commit[] commits = getRepositoryCommits(owner, repo);
        Release[] releases = getRepositoryReleases(owner, repo);
        Issue[] issues = getRepositoryIssues(owner, repo);
        PullRequest[] pullRequests = getRepositoryPullRequests(owner, repo);

        // Extract relevant metrics
        int stars = repoInfo.getStars();
        int forks = repoInfo.getForks();
        int openIssuesCount = countIssues(issues, "open");
        int closedIssuesCount = countIssues(issues, "closed");
        int openPullRequestsCount = countPullRequests(pullRequests, "open");
        int closedPullRequestsCount = countPullRequests(pullRequests, "closed");
        int contributorsCount = contributors.length;
        int commitsCount = commits.length;
        int releasesCount = releases.length;
        int repositorySize = repoInfo.getSize();

        // Check if the repository meets the criteria using thresholds
        boolean hasRecentActivity = repoInfo.getLastCommitDate() != null && !repoInfo.getLastCommitDate().isEmpty();
        boolean hasLicense = repoInfo.getLicense() != null && repoInfo.getLicense().getSpdxId()!=null &&  !repoInfo.getLicense().getSpdxId().isEmpty();
        boolean hasDocumentation = hasDocumentation(owner, repo);

        return stars >= thresholds.getStar() &&
                forks >= thresholds.getFork() &&
                openIssuesCount <= thresholds.getOpenIssues() &&
                closedIssuesCount >= thresholds.getClosedIssues() &&
                openPullRequestsCount <= thresholds.getOpenPullRequests() &&
                closedPullRequestsCount >= thresholds.getClosedPullRequests() &&
                contributorsCount >= thresholds.getContributors() &&
                commitsCount >= thresholds.getCommits() &&
                releasesCount >= thresholds.getReleases() &&
                hasRecentActivity &&
                hasLicense &&
                hasDocumentation &&
                repositorySize >= thresholds.getSize();
    }

    private RepositoryInfo getRepositoryInfo(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo;
        return restTemplate.getForObject(url, RepositoryInfo.class);
    }

    private Contributor[] getRepositoryContributors(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contributors";
        return restTemplate.getForObject(url, Contributor[].class);
    }

    private Commit[] getRepositoryCommits(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/commits";
        return restTemplate.getForObject(url, Commit[].class);
    }

    private Release[] getRepositoryReleases(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";
        return restTemplate.getForObject(url, Release[].class);
    }

    private Issue[] getRepositoryIssues(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/issues";
        return restTemplate.getForObject(url, Issue[].class);
    }

    private PullRequest[] getRepositoryPullRequests(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls";
        return restTemplate.getForObject(url, PullRequest[].class);
    }

    private int countIssues(Issue[] issues, String state) {
        return (int) Arrays.stream(issues)
                .filter(issue -> state.equals(issue.getState()))
                .count();
    }

    private int countPullRequests(PullRequest[] pullRequests, String state) {
        return (int) Arrays.stream(pullRequests)
                .filter(pr -> state.equals(pr.getState()))
                .count();
    }

    private boolean hasDocumentation(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents";
        RepositoryContent[] contents = restTemplate.getForObject(url, RepositoryContent[].class);

        if (contents != null) {
            for (RepositoryContent content : contents) {
                if ("file".equals(content.getType()) && "README.md".equalsIgnoreCase(content.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
