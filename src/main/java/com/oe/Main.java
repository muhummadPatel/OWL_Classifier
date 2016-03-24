package com.oe;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.util.*;

public class Main {
    public static void main(String[] args){
        System.out.println("--------------START MAIN---------------\n");

        // The OWL file to be loaded
        String filePath = "AirIncidentOntology.owl";

        // Example calls to OntologyLoader for single or a set of ontologies
        OWLOntology mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
        Set<OWLOntology> ontologies = OntologyLoader.loadOntology(filePath, true);

        OWL2QLProfile profile = new OWL2QLProfile();
        System.out.println("OWL2DL: " + profile.checkOntology(mainOntology));
        System.out.println(mainOntology.getLogicalAxioms());

        DLExpressivityChecker expCheck = new DLExpressivityChecker(ontologies);
        String dlName = expCheck.getDescriptionLogicName();
        System.out.println("Ontology Expressivity: " + dlName);

        System.out.println("\n---------------END MAIN----------------");
    }
}
