package com.skv.library.validator.model;

public class PullRequest {

    private String state;

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    private boolean merged;
    // Getters and setters

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
