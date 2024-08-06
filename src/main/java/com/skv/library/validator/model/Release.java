package com.skv.library.validator.model;

public class Release {
    private String tag_name;
    private String name;
    private String body;

    // Getters and setters

    public String getTagName() {
        return tag_name;
    }

    public void setTagName(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
