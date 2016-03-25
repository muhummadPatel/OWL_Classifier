package com.oe;

import java.util.ArrayList;
import java.util.List;

public class OWL1ProfileReport {
    private String violations;
    private boolean isInProfile;

    public OWL1ProfileReport() {
        violations = null;
        isInProfile = false;
    }

    public void setViolations(String violation) {
        violations = violation;
    }

    public String getViolations() {
        return violations.trim();
    }

    public boolean isInProfile() {
        return isInProfile;
    }

    public void setIsInProfile(boolean isInProfile) {
        this.isInProfile = isInProfile;
    }
}
