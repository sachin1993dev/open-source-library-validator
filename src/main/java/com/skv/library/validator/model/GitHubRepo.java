package com.skv.library.validator.model;

import java.util.List;
import java.util.Map;

public class GitHubRepo {

    // Basic Information
    private String fullName;
    private String description;
    private String language;
    private String createdAt;
    private String updatedAt;
    private String pushedAt;
    private int size;


    // Counts
    private int stargazersCount;
    private int forksCount;
    private int watchersCount;
    private int openIssuesCount;

    // Commit Activity
    private int totalCommits;

    // Pull Requests
    private Map<String, Integer> pullRequests; // Open, Closed, Merged counts

    // Issues
    private List<Issue> issues;

    // Contributors
    private List<Contributor> contributors;

    // Releases
    private List<Release> releases;

    // README Content
    private String readmeContent;

    // Branches
    private List<String> branches;

    // Tags
    private List<Tag> tags;

    // Topics
    private List<String> topics;

    // Code Frequency
    private List<CodeFrequency> codeFrequency;

    // Contributors Statistics
    private List<ContributorStats> contributorStats;

    // Commit Counts
    private Participation participation;

    // Clones
    private Traffic clones;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(String pushedAt) {
        this.pushedAt = pushedAt;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



    public int getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(int stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(int watchersCount) {
        this.watchersCount = watchersCount;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public void setOpenIssuesCount(int openIssuesCount) {
        this.openIssuesCount = openIssuesCount;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public Map<String, Integer> getPullRequests() {
        return pullRequests;
    }

    public void setPullRequests(Map<String, Integer> pullRequests) {
        this.pullRequests = pullRequests;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public String getReadmeContent() {
        return readmeContent;
    }

    public void setReadmeContent(String readmeContent) {
        this.readmeContent = readmeContent;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<CodeFrequency> getCodeFrequency() {
        return codeFrequency;
    }

    public void setCodeFrequency(List<CodeFrequency> codeFrequency) {
        this.codeFrequency = codeFrequency;
    }

    public List<ContributorStats> getContributorStats() {
        return contributorStats;
    }

    public void setContributorStats(List<ContributorStats> contributorStats) {
        this.contributorStats = contributorStats;
    }

    public Participation getParticipation() {
        return participation;
    }

    public void setParticipation(Participation participation) {
        this.participation = participation;
    }

    public Traffic getClones() {
        return clones;
    }

    public void setClones(Traffic clones) {
        this.clones = clones;
    }

    public Traffic getViews() {
        return views;
    }

    public void setViews(Traffic views) {
        this.views = views;
    }

    // Views
    private Traffic views;

    // Getters and Setters
    // Omitted for brevity
}
