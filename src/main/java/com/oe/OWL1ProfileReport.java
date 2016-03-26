package com.oe;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a report entailing if an OWL 1 ontology adheres to a specific profile
 */
public class OWL1ProfileReport {
    private List<String> violations;
    private boolean isInProfile;

    public OWL1ProfileReport() {
        violations = new ArrayList<>();
        isInProfile = false;
    }

    public void addViolation(String violation) {
        violations.add(violation);
    }

    public List<String> getViolations() {
        return new ArrayList<String>(violations);
    }

    public boolean isInProfile() {
        return isInProfile;
    }

    public void setIsInProfile(boolean isInProfile) {
        this.isInProfile = isInProfile;
    }
}
