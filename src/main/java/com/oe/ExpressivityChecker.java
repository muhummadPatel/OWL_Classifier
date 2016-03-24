package com.oe;

import java.util.*;

import org.semanticweb.owlapi.model.*;

public class ExpressivityChecker extends org.semanticweb.owlapi.util.DLExpressivityChecker{

    public ExpressivityChecker(Set<OWLOntology> ontologies){
        super(ontologies);
    }
}
