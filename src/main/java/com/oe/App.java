package com.oe;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;

public class App{
    public static void main(String[] args){
        System.out.println("\n\n--------------START MAIN---------------\n\n\n");

        String filename = "AirIncidentOntology.owl";
        // String filename = "pizza.owl";

        System.out.println("Attempting to load ontology: " + filename);
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try{
            File ontFile = new File(filename);
            ontology = m.loadOntologyFromOntologyDocument(ontFile);
        }catch(org.semanticweb.owlapi.model.OWLOntologyCreationException ex){
            System.out.println("Ontology creation Error:\n" + ex.getMessage());
            System.exit(0);
        }
        System.out.println(filename + " loaded.");

        // DLExpressivityChecker takes in a Set, so we need to build a set
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        ontologies.add(ontology);

        DLExpressivityChecker expCheck = new DLExpressivityChecker(ontologies);
        String dlName = expCheck.getDescriptionLogicName();
        System.out.println("Ontology Expressivity: " + dlName);

        System.out.println("\n\n\n---------------END MAIN----------------\n\n");
    }
}
