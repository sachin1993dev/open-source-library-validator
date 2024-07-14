package com.skv.library.validator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "github.thresholds")
public class ThresholdsProperties {

    private int star;
    private int fork;
    private int openIssues;
    private int closedIssues;
    private int openPullRequests;
    private int closedPullRequests;
    private int contributors;
    private int commits;
    private int releases;
    private int size;

    // Getters and setters for each threshold property

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getFork() {
        return fork;
    }

    public void setFork(int fork) {
        this.fork = fork;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public int getClosedIssues() {
        return closedIssues;
    }

    public void setClosedIssues(int closedIssues) {
        this.closedIssues = closedIssues;
    }

    public int getOpenPullRequests() {
        return openPullRequests;
    }

    public void setOpenPullRequests(int openPullRequests) {
        this.openPullRequests = openPullRequests;
    }

    public int getClosedPullRequests() {
        return closedPullRequests;
    }

    public void setClosedPullRequests(int closedPullRequests) {
        this.closedPullRequests = closedPullRequests;
    }

    public int getContributors() {
        return contributors;
    }

    public void setContributors(int contributors) {
        this.contributors = contributors;
    }

    public int getCommits() {
        return commits;
    }

    public void setCommits(int commits) {
        this.commits = commits;
    }

    public int getReleases() {
        return releases;
    }

    public void setReleases(int releases) {
        this.releases = releases;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
