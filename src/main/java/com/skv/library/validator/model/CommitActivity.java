package com.skv.library.validator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommitActivity {
    @JsonProperty("total")
    private int totalCommits;

    // getters and setters
    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }
}
