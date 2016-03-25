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

        String filePath = "test_ontologies/OWL2_QL/iLog.owl";

        /**
         * Example calls to OntologyLoader for single or a set of ontologies
         */
        OWLOntology mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
        Set<OWLOntology> ontologies = OntologyLoader.loadOntology(filePath, true);
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
        for(String letter: axiomClassifications.keySet()){
            System.out.println("=== " + letter + " ===");
            for(OWLAxiom axiom: axiomClassifications.get(letter)){
                System.out.println(axiom);
            }
            System.out.println();
        }
        System.out.println("Axiom Classifications Explanations:\n" + result.explanation);

        /**
         * Example call to ProfileChecker
         * 1. The profile reports are generated
         * 2. The profile name is looked up using the PROFILE_NAMES list
         * 3. The profile is looked up and the violations printed out
         */
        HashMap<String, OWLProfileReport> ontologyProfileReports = ProfileChecker.calculateOntologyProfileReports(mainOntology);
        for(String profileName : ProfileChecker.PROFILE_NAMES) {
            System.out.println("Violations for profile " + profileName + ":");
            for (OWLProfileViolation violation : ontologyProfileReports.get(profileName).getViolations()) {
                System.out.println(violation.toString());
            }
            System.out.println();
        }

        System.out.println();
        System.out.println(OWL1ProfileChecker.calculateOntologyProfileReports(OntologyLoader.getOntologyURI(filePath)));

        System.out.println("\n---------------END MAIN----------------");
    }
}
