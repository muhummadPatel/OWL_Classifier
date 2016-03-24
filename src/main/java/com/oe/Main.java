package com.oe;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.util.*;

public class Main {
    public static void main(String[] args){
        Main main = new Main();
        main.doStuff();
    }

    public void doStuff() {
        System.out.println("\n\n--------------START MAIN---------------\n\n\n");

        boolean usesImports = true;
        String filename = "AirIncidentOntology.owl";
        // String filename = "Beverages.owl";

        System.out.println("Attempting to load ontology: " + filename);
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntologyManager m2 = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        OWLOntology ontology2 = null;
        // DLExpressivityChecker takes in a Set, so we need to build a set
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        try{
            File ontFile = new File(filename);
            ontology = m.loadOntologyFromOntologyDocument(ontFile);
            ontologies.add(ontology);
            if(usesImports) {
                addImports(ontologies, ontology);
            }

        }catch(org.semanticweb.owlapi.model.OWLOntologyCreationException ex){
            System.out.println("Ontology creation Error:\n" + ex.getMessage());
            System.exit(0);
        }
        System.out.println(filename + " loaded.");

        OWL2QLProfile profile = new OWL2QLProfile();
        //System.out.println("OWL2DL: " + profile.checkOntology(ontology));
        System.out.println(ontology.getLogicalAxioms());

        DLExpressivityChecker expCheck = new DLExpressivityChecker(ontologies);
        String dlName = expCheck.getDescriptionLogicName();
        System.out.println("Ontology Expressivity: " + dlName);

        System.out.println("\n\n\n---------------END MAIN----------------\n\n");
    }

    public void addImports(Set<OWLOntology> ontologies, OWLOntology ontology) {
        Set<OWLOntology> importOntologies = ontology.getImports();
        for(OWLOntology singleImport : importOntologies) {
            ontologies.add(singleImport);
            addImports(ontologies, singleImport);
        }
    }
}
