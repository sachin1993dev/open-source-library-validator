package com.skv.library.validator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Issue {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(Object pullRequest) {
        this.pullRequest = pullRequest;
    }

    @JsonProperty("pull_request")
    private Object pullRequest;
    private String state;

    // Getters and setters

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
