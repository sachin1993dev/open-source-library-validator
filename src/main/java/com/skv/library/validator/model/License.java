package com.skv.library.validator.model;

public class License {

    private String spdx_id;


    public boolean isDeprecatedLicenseId() {
        return isDeprecatedLicenseId;
    }

    public void setDeprecatedLicenseId(boolean deprecatedLicenseId) {
        isDeprecatedLicenseId = deprecatedLicenseId;
    }

    private boolean isDeprecatedLicenseId;

    public String getGit_url() {
        return git_url;
    }

    public void setGit_url(String git_url) {
        this.git_url = git_url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String git_url;

    public boolean isHas_wiki() {
        return has_wiki;
    }

    public void setHas_wiki(boolean has_wiki) {
        this.has_wiki = has_wiki;
    }

    private boolean has_wiki;

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    private String node_id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    // Getters and setters

    public String getSpdx_id() {
        return spdx_id;
    }

    public void setSpdx_id(String spdx_id) {
        this.spdx_id = spdx_id;
    }

    public boolean isOsiApproved() {
        return osiApproved;
    }

    public boolean isFsfLibre() {
        return fsfLibre;
    }

    private boolean osiApproved;
    private boolean fsfLibre;
    public void setOsiApproved(boolean osiApproved) {
    }

    public void setFsfLibre(boolean fsfLibre) {
    }
}
