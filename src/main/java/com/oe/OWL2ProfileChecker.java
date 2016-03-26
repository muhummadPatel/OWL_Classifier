package com.oe;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.*;

import java.util.*;

/**
 * Encapsulates functionality to check the profile of an OWL 2 ontology
 *
 * @author Brian Mc George
 */
public class OWL2ProfileChecker {
    public static final List<String> PROFILE_NAMES = Collections.unmodifiableList(Arrays.asList(new OWL2Profile().getName(), new OWL2ELProfile().getName(),
            new OWL2DLProfile().getName(), new OWL2QLProfile().getName(), new OWL2RLProfile().getName()));

    public static final List<Class<? extends org.semanticweb.owlapi.profiles.OWLProfile>> OWL_PROFILES = Collections.unmodifiableList(Arrays.asList(OWL2Profile
            .class, OWL2ELProfile.class, OWL2DLProfile.class, OWL2QLProfile.class, OWL2RLProfile.class));

    /**
     * Calculates the a report of the OWL profiles which can be used to determine if an ontology falls within that profile and the getViolations (if any) that
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
