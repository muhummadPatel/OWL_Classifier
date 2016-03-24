package com.oe;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.*;

import java.util.*;

public class ProfileChecker {
    public static final List<Class<? extends org.semanticweb.owlapi.profiles.OWLProfile>> OWL_PROFILES = Collections.unmodifiableList(Arrays.asList(OWL2Profile
            .class, OWL2ELProfile.class, OWL2DLProfile.class, OWL2QLProfile.class, OWL2RLProfile.class));
    public static final List<String> PROFILE_NAMES = Collections.unmodifiableList(Arrays.asList("OWL 2", "OWL 2 EL", "OWL 2 DL", "OWL 2 QL", "OWL 2 RL"));

    /**
     * Calculates the a report of the OWL profiles which can be used to determine if an ontology falls within that profile and the violations (if any) that
     * occurred
     *
     * @param ontology the ontology to generate the reports for
     * @return a hashmap of profile names that link to profile reports
     */
    public static HashMap<String, OWLProfileReport> calculateOntologyProfileReports(OWLOntology ontology) {
        HashMap<String, OWLProfileReport> profileMap = new HashMap<>();
        for (int i = 0; i < OWL_PROFILES.size(); ++i) {
            try {
                org.semanticweb.owlapi.profiles.OWLProfile profile = OWL_PROFILES.get(i).newInstance();
                profileMap.put(profile.getName(), profile.checkOntology(ontology));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return profileMap;
    }
}
