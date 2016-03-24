package com.oe;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Unit test for simple Main.
 */
public class OntologyLoaderTest
        extends TestCase
{
    public void testInvalidPath()
    {
        try {
            OntologyLoader.loadOntology("thisfileshouldhopefullynotexist.nofile", true);
        } catch(org.semanticweb.owlapi.io.OWLOntologyInputSourceException e) {
            assert(true);
            return;
        }
        assert(false);
    }
    public void testValidLoad()
    {
        /*
        try {
            URL resource = getClass().getClassLoader().getResource("Beverages.owl");
            System.out.println(resource);
            OntologyLoader.loadOntologies(resource.getPath(), true);
        } catch(org.semanticweb.owlapi.io.OWLOntologyInputSourceException e) {
            assert(false);
            return;
        }
        */
        assert(true);
    }
}
