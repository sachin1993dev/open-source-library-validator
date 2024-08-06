// SPDX License Model
package com.skv.library.validator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpdxLicense {
    private String licenseId;

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public boolean isOsiApproved() {
        return isOsiApproved;
    }

    public void setOsiApproved(boolean osiApproved) {
        isOsiApproved = osiApproved;
    }

    public boolean isFsfLibre() {
        return isFsfLibre;
    }

    public void setFsfLibre(boolean fsfLibre) {
        isFsfLibre = fsfLibre;
    }

    public boolean isDeprecatedLicenseId() {
        return isDeprecatedLicenseId;
    }

    public void setDeprecatedLicenseId(boolean deprecatedLicenseId) {
        isDeprecatedLicenseId = deprecatedLicenseId;
    }
    @JsonProperty("isOsiApproved")
    private boolean isOsiApproved;
    @JsonProperty("isFsfLibre")
    private boolean isFsfLibre;
    @JsonProperty("isDeprecatedLicenseId")
    private boolean isDeprecatedLicenseId;

    // Getters and Setters

}

// Extend LicenseService to include SPDX data

