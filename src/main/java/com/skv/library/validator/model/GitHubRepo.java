package com.skv.library.validator.model;

import java.util.List;
import java.util.Map;

public class GitHubRepo {

    // Basic Information

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String fullName;

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    private Release release;


    public String getSubscribers_count() {
        return subscribers_count;
    }

    public void setSubscribers_count(String subscribers_count) {
        this.subscribers_count = subscribers_count;
    }

    public String getNetwork_count() {
        return network_count;
    }

    public void setNetwork_count(String network_count) {
        this.network_count = network_count;
    }

    private String subscribers_count;

    private String network_count;

    private String description;
    private String language;
    private String created_at;
    private String updated_at;

    public String getHas_issues() {
        return has_issues;
    }

    public void setHas_issues(String has_issues) {
        this.has_issues = has_issues;
    }

    private String has_issues;
    public String getHas_wiki() {
        return has_wiki;
    }

    public void setHas_wiki(String has_wiki) {
        this.has_wiki = has_wiki;
    }

    private String has_wiki;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPushed_at() {
        return pushed_at;
    }

    public void setPushed_at(String pushed_at) {
        this.pushed_at = pushed_at;
    }

    public int getForks_count() {
        return forks_count;
    }

    public void setForks_count(int forks_count) {
        this.forks_count = forks_count;
    }

    public int getWatchers_count() {
        return watchers_count;
    }

    public void setWatchers_count(int watchers_count) {
        this.watchers_count = watchers_count;
    }

    public int getOpen_issues_count() {
        return open_issues_count;
    }

    public void setOpen_issues_count(int open_issues_count) {
        this.open_issues_count = open_issues_count;
    }

    private String pushed_at;
    private int size;

    // Counts
    private int stargazersCount;
    private int forks_count;
    private int watchers_count;
    private int open_issues_count;

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    private License license;

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
