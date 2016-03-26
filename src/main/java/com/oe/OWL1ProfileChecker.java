package com.oe;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import uk.ac.man.cs.img.owl.validation.*;

import java.io.*;
import java.net.URI;
import java.util.*;

public class OWL1ProfileChecker {
    public static final List<String> PROFILE_NAMES = Collections.unmodifiableList(Arrays.asList("OWL 1 Lite", "OWL 1 DL", "OWL 1 Full"));
    public static final String NOT_OWL = "OTHER";
    public static final int PADDING = 15;

    /**
     * Calculates a report of the OWL profiles which can be used to determine if an ontology falls within that profile and the violations (if any) that
     * occurred
     *
     * @param ontologyFileURI to the ontology to generate the reports for
     * @return a hashmap of profile names that link to profile reports
     */
    public static HashMap<String, OWL1ProfileReport> calculateOntologyProfileReports(URI ontologyFileURI) {
        HashMap<String, OWL1ProfileReport> profileMap = new HashMap<>();
        SpeciesValidator speciesValidator;
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
                    String message = level(l) + "[" + SpeciesValidator.readableCode(code).trim() + "]: " + s.replaceAll("\t", "");
                    ;
                    System.out.println(message);
                }
            };

            /* The OWL 1 validator api cannot return a list of violations.
            The standard output is temporally redirected to a byte array output stream which can be converted into a string.
            The newlines are then used to separate it into an array. The output from the validator api has been
            set to a specific format (the code above) which is used to calculate at which profile the explanation is labeled.
            This is then used to filter the messages for each profile check.
            The violation message is then cleaned up and added to the profile report.
            Standard output is restored after all the profiles are calculated.*/

            // Create a stream to redirect standard output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(outputStream);
            PrintStream old = System.out;
            System.setOut(ps);

            // OWL 1 Lite
            speciesValidator = new SpeciesValidator();
            speciesValidator.setReporter(speciesValidatorReporter);
            OWL1ProfileReport profileReportLite = new OWL1ProfileReport();
            profileReportLite.setIsInProfile(speciesValidator.isOWLLite(ontologyFileURI));
            profileMap.put(PROFILE_NAMES.get(0), profileReportLite);

            BufferedReader rdr = new BufferedReader(new StringReader(outputStream.toString()));
            for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                if (line.contains(PROFILE_NAMES.get(1)) || line.contains(PROFILE_NAMES.get(2)) || line.contains(NOT_OWL)) {
                    profileReportLite.addViolation(removePadding(line));
                }
            }
            rdr.close();
            outputStream.reset();

            // OWL 1 DL
            speciesValidator = new SpeciesValidator();
            speciesValidator.setReporter(speciesValidatorReporter);
            OWL1ProfileReport profileReportDL = new OWL1ProfileReport();
            profileReportDL.setIsInProfile(speciesValidator.isOWLDL(ontologyFileURI));
            profileMap.put(PROFILE_NAMES.get(1), profileReportDL);
            rdr = new BufferedReader(new StringReader(outputStream.toString()));
            for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                if (line.contains(PROFILE_NAMES.get(2)) || line.contains(NOT_OWL)) {
                    profileReportDL.addViolation(removePadding(line));
                }
            }
            rdr.close();
            outputStream.reset();

            // OWL 1 Full
            speciesValidator = new SpeciesValidator();
            speciesValidator.setReporter(speciesValidatorReporter);
            OWL1ProfileReport profileReportFull = new OWL1ProfileReport();
            profileReportFull.setIsInProfile(speciesValidator.isOWLFull(ontologyFileURI));
            profileMap.put(PROFILE_NAMES.get(2), profileReportFull);
            speciesValidator.setReporter(speciesValidatorReporter);
            rdr = new BufferedReader(new StringReader(outputStream.toString()));
            for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                // This may still require tweaking
                if (line.contains(NOT_OWL)) {
                    profileReportFull.addViolation(removePadding(line));
                }
            }
            rdr.close();
            outputStream.reset();

            // Reset standard output
            System.out.flush();
            System.setOut(old);

        } catch (OWLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return profileMap;
    }

    private static String removePadding(String paddedLevelMessage) {
        return paddedLevelMessage.substring(PADDING);
    }

    public static String level(int l) {
        String level = NOT_OWL;
        if (l == SpeciesValidator.LITE) {
            level = PROFILE_NAMES.get(0);
        } else if (l == SpeciesValidator.DL) {
            level = PROFILE_NAMES.get(1);
        } else if (l == SpeciesValidator.FULL) {
            level = PROFILE_NAMES.get(2);
        }

        // Pad the level to ensure that the length is always == padding
        int len = (PADDING - level.length());
        for (int i = 0; i < len; ++i) {
            level += " ";
        }

        return level;
    }
}
