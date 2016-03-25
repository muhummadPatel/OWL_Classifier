package com.oe;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import uk.ac.man.cs.img.owl.validation.*;

import java.net.URI;
import java.util.HashMap;

/**
 * Encapsulates functionality to
 */
public class OWL1ProfileChecker {
    public static HashMap<String, OWLProfileReport> calculateOntologyProfileReports(URI ontologyFileURI) {
        SpeciesValidator speciesValidator = null;
        try {
            speciesValidator = new SpeciesValidator();
        } catch (OWLException e) {
            e.printStackTrace();
        }
        System.out.println(speciesValidator.isOWLDL(ontologyFileURI));
        return null;
    }

}
