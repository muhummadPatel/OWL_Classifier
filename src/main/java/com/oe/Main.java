package com.oe;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

/**
 * Used for development and debugging purposes while UI is developed
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("--------------START MAIN---------------\n");

        // The OWL file to be loaded
        String filePath = "test_ontologies/Simple/pizza.owl";

        /**
         * Example calls to OntologyLoader for single or a set of ontologies
         */
        OWLOntology mainOntology = null;
        Set<OWLOntology> ontologies = null;
        try {
            mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
            ontologies = OntologyLoader.loadOntology(filePath, true);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        System.out.println();

        // Example call to ExpressivityChecker
        // First, here we get the description logic name and display it
        ExpressivityChecker expChecker = new ExpressivityChecker(ontologies);
        String expressivity = expChecker.getDescriptionLogicName();
        System.out.println("Ontology Expressivity: " + expressivity + "\n");

        // Here we get the axiom classifications and the explanation for how we
        // ended up at the final list of letters (why some letters were removed, etc)
        ExpressivityChecker.AxiomClassificationResult result = expChecker.getAxiomClassifications();
        HashMap<String, ArrayList<OWLAxiom>> axiomClassifications = result.classifications;
        System.out.println("Axiom Classifications:");
        for (String letter : axiomClassifications.keySet()) {
            System.out.println("=== " + letter + " ===");
            for (OWLAxiom axiom : axiomClassifications.get(letter)) {
                System.out.println(axiom);
            }
            System.out.println();
        }
        System.out.println("Axiom Classifications Explanations:\n" + result.explanation);

        /**
         * Example call to OWL2ProfileChecker
         * 1. The profile reports are generated
         * 2. The profile name is looked up using the PROFILE_NAMES list
         * 3. The profile is looked up and the getViolations printed out
         */
        HashMap<String, OWLProfileReport> ontologyProfileReports = OWL2ProfileChecker.calculateOntologyProfileReports(mainOntology);
        for (String profileName : OWL2ProfileChecker.PROFILE_NAMES) {
            System.out.println("Violations for profile " + profileName + ":");
            OWLProfileReport profileReport = ontologyProfileReports.get(profileName);
            System.out.println(profileReport.isInProfile());
            for (OWLProfileViolation violation : profileReport.getViolations()) {
                System.out.println(violation.toString());
            }
            System.out.println();
        }

        /**
         * Example call to OWL1ProfileChecker
         */
        // Calculate the report profiles
        HashMap<String, OWL1ProfileReport> owl1ontologyProfileReports = OWL1ProfileChecker.calculateOntologyProfileReports(OntologyLoader.getOntologyURI
                (filePath));
        for (String profileName : OWL1ProfileChecker.PROFILE_NAMES) {
            // Display profile
            System.out.println("Violations for profile " + profileName + ":");

            // Get the report
            OWL1ProfileReport profileReport = owl1ontologyProfileReports.get(profileName);

            // Check if ontology falls within that profile
            System.out.println(profileReport.isInProfile());

            // Display violations
            // It was done like this for technical (implementation) reasons
            for (String violation : profileReport.getViolations()) {
                System.out.println(violation);
            }


            System.out.println();
        }

        System.out.println("\n---------------END MAIN----------------");
    }
}
