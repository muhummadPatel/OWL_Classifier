package com.oe;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import uk.ac.man.cs.img.owl.validation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OWL1ProfileChecker {
    public String test;

    public static final List<String> PROFILE_NAMES = Collections.unmodifiableList(Arrays.asList("OWL 1 Lite", "OWL 1 DL", "OWL 1 Full"));

    /**
     * Calculates the a report of the OWL profiles which can be used to determine if an ontology falls within that profile and the getViolations (if any) that
     * occurred
     *
     * @param ontologyFileURI to the ontology to generate the reports for
     * @return a hashmap of profile names that link to profile reports
     */
    public static HashMap<String, OWL1ProfileReport> calculateOntologyProfileReports(URI ontologyFileURI) {
        HashMap<String, OWL1ProfileReport> profileMap = new HashMap<>();
        String test;
        SpeciesValidator speciesValidator = null;
        try {
            SpeciesValidatorReporter speciesValidatorReporter = new SpeciesValidatorReporter() {
                @Override
                public void ontology(OWLOntology owlOntology) {

                }

                @Override
                public void done(String s) {

                }

                @Override
                public void message(String s) {
                    System.out.println(s);
                }

                @Override
                public void explain(int l, int code, String s) {
                    String message = level(l) + " [" + SpeciesValidator.readableCode(code) + "]:\t" + s;
                    System.out.println(message);
                }
            };
            // Create a stream to redirect standard output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(outputStream);
            PrintStream old = System.out;
            System.setOut(ps);

            // OWL 1 Lite
            speciesValidator = new SpeciesValidator();
            OWL1ProfileReport profileReportLite = new OWL1ProfileReport();
            profileReportLite.setIsInProfile(speciesValidator.isOWLLite(ontologyFileURI));
            profileMap.put(PROFILE_NAMES.get(0), profileReportLite);
            profileReportLite.setViolations(outputStream.toString());
            outputStream.reset();

            // OWL 1 DL
            speciesValidator = new SpeciesValidator();
            OWL1ProfileReport profileReportDL = new OWL1ProfileReport();
            profileReportDL.setIsInProfile(speciesValidator.isOWLDL(ontologyFileURI));
            profileMap.put(PROFILE_NAMES.get(1), profileReportDL);
            profileReportDL.setViolations(outputStream.toString());
            outputStream.reset();

            // OWL 1 Full
            speciesValidator = new SpeciesValidator();
            OWL1ProfileReport profileReportFull = new OWL1ProfileReport();
            profileReportFull.setIsInProfile(speciesValidator.isOWLFull(ontologyFileURI));
            profileMap.put(PROFILE_NAMES.get(2), profileReportFull);
            speciesValidator.setReporter(speciesValidatorReporter);
            profileReportFull.setViolations(outputStream.toString());
            outputStream.reset();

            // Reset standard output
            System.out.flush();
            System.setOut(old);

        } catch (OWLException e) {
            e.printStackTrace();
        }

        return profileMap;
    }

    private static String level(int l) {
        if (l == SpeciesValidator.LITE) {
            return PROFILE_NAMES.get(0);
        } else if (l == SpeciesValidator.DL) {
            return PROFILE_NAMES.get(1)+ "  ";
        } else if (l == SpeciesValidator.FULL) {
            return PROFILE_NAMES.get(2);
        } else {
            return "OTHER     ";
        }

    }

}
