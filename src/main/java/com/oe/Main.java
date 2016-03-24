package com.oe;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.util.*;

public class Main {
    public static void main(String[] args){
        System.out.println("--------------START MAIN---------------\n");

        // The OWL file to be loaded
        String filePath = "AirIncidentOntology.owl";

        /**
         * Example calls to OntologyLoader for single or a set of ontologies
         */
        OWLOntology mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
        Set<OWLOntology> ontologies = OntologyLoader.loadOntology(filePath, true);

        /**
         * Example call to ProfileChecker
         * 1. The profile reports are generated
         * 2. The profile name is looked up using the PROFILE_NAMES list
         * 3. The profile is looked up and the violations printed out
         */
        HashMap<String, OWLProfileReport> ontologyProfileReports = ProfileChecker.calculateOntologyProfileReports(mainOntology);
        String profileName = ProfileChecker.PROFILE_NAMES.get(1);
        for(OWLProfileViolation violation : ontologyProfileReports.get(profileName).getViolations()) {
            System.out.println(violation.toString());
        }

        System.out.println("\n---------------END MAIN----------------");
    }
}
