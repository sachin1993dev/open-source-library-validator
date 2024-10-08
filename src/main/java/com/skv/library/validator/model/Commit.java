package com.skv.library.validator.model;

public class Commit {

    private String sha;
    private CommitAuthor author;

    // Getters and setters

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public CommitAuthor getAuthor() {
        return author;
    }

    public void setAuthor(CommitAuthor author) {
        this.author = author;
    }
}
