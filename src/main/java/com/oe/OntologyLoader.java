package com.oe;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Class is responsible for loading ontologies from file
 */
public class OntologyLoader{
    /**
     * Takes a path to a file and a boolean representing if imports should be included and returns a set of OWLOntology objects
     * @param filePath path to file being loaded
     * @param isLoadingImports should imports be included in the set
     * @return A set of OWLOntology objects
     */
    public static Set<OWLOntology> loadOntology(String filePath, boolean isLoadingImports) {
        String extra;
        if(isLoadingImports) {
            extra = " including imports";
        } else {
            extra = " without imports";
        }

        System.out.println("Attempting to load " + filePath + extra);
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        Set<OWLOntology> ontologies = new HashSet<>();

        try{
            File ontFile = new File(filePath);
            ontology = owlOntologyManager.loadOntologyFromOntologyDocument(ontFile);
            ontologies.add(ontology);
            if(isLoadingImports) {
                addImports(ontologies, ontology);
            }

            System.out.println(ontFile.getName() + " loaded" + extra);

        }catch(OWLOntologyCreationException ex){
            ex.printStackTrace();
            return null;
            //System.exit(1);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
            //System.exit(1);
        }

        return ontologies;
    }

    public static URI getOntologyURI(String filePath) {
        File ontFile = new File(filePath);
        return ontFile.toURI();
    }

    private static void addImports(Set<OWLOntology> ontologies, OWLOntology ontology) {
        Set<OWLOntology> importOntologies = ontology.getImports();
        for(OWLOntology singleImport : importOntologies) {
            ontologies.add(singleImport);
            addImports(ontologies, singleImport);
        }
    }
}
