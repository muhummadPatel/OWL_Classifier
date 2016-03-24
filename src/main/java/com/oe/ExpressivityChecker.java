package com.oe;

import java.io.Serializable;
import java.util.*;
import javax.annotation.Nonnull;

import static org.semanticweb.owlapi.model.parameters.Imports.EXCLUDED;
import static org.semanticweb.owlapi.util.DLExpressivityChecker.Construct.*;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.verifyNotNull;
import static org.semanticweb.owlapi.util.DLExpressivityChecker.Construct;
import org.semanticweb.owlapi.model.*;

public class ExpressivityChecker extends org.semanticweb.owlapi.util.DLExpressivityChecker {

    private final Set<Construct> constructs;
    private final Set<OWLOntology> ontologies;

    /** @return ordered constructs */
    public List<Construct> getConstructs() {
        return getOrderedConstructs();
    }

    /**
     * @param ontologies
     *        ontologies
     */
    public ExpressivityChecker(Set<OWLOntology> ontologies) {
        super(ontologies);
        this.ontologies = ontologies;
        constructs = new HashSet<>();
    }

    /** @return DL name */
    @Nonnull
    public String getDescriptionLogicName() {
        StringBuilder s = new StringBuilder();
        for (Construct c : getOrderedConstructs()) {
            s.append(c);
        }
        return verifyNotNull(s.toString());
    }

    private void pruneConstructs() {
        if (constructs.contains(AL)) {
            // AL + U + E can be represented using ALC
            if (constructs.contains(C)) {
                // Remove existential because this can be represented
                // with AL + Neg
                constructs.remove(E);
                // Remove out union (intersection + negation (demorgan))
                constructs.remove(U);
            } else if (constructs.contains(E) && constructs.contains(U)) {
                // Simplify to ALC
                constructs.add(AL);
                constructs.add(C);
                constructs.remove(E);
                constructs.remove(U);
            }
        }
        if (constructs.contains(N) || constructs.contains(Q)) {
            constructs.remove(F);
        }
        if (constructs.contains(Q)) {
            constructs.remove(N);
        }
        if (constructs.contains(AL) && constructs.contains(C)
                && constructs.contains(TRAN)) {
            constructs.remove(AL);
            constructs.remove(C);
            constructs.remove(TRAN);
            constructs.add(S);
        }
        if (constructs.contains(R)) {
            constructs.remove(H);
        }
    }

    private List<Construct> getOrderedConstructs() {
        constructs.clear();
        constructs.add(AL);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                ax.accept(this);
            }
        }
        pruneConstructs();
        List<Construct> cons = new ArrayList<>(constructs);
        Collections.sort(cons, new ConstructComparator());
        return cons;
    }

    /** A comparator that orders DL constucts to produce a traditional DL name. */
    private static class ConstructComparator implements Comparator<Construct>,
            Serializable {

        private static final long serialVersionUID = 40000L;
        private final List<Construct> order = new ArrayList<>();

        ConstructComparator() {
            order.add(S);
            order.add(AL);
            order.add(C);
            order.add(U);
            order.add(E);
            order.add(R);
            order.add(H);
            order.add(O);
            order.add(I);
            order.add(N);
            order.add(Q);
            order.add(F);
            order.add(TRAN);
            order.add(D);
        }

        @Override
        public int compare(Construct o1, Construct o2) {
            return order.indexOf(o1) - order.indexOf(o2);
        }
    }

    // Property expression
    @Override
    public void visit(OWLObjectInverseOf property) {
        constructs.add(I);
    }

    @Override
    public void visit(OWLDataProperty property) {
        constructs.add(D);
    }

    // Data stuff
    @Override
    public void visit(OWLDataComplementOf node) {
        constructs.add(D);
    }

    @Override
    public void visit(OWLDataOneOf node) {
        constructs.add(D);
    }

    @Override
    public void visit(OWLDatatypeRestriction node) {
        constructs.add(D);
    }

    @Override
    public void visit(OWLLiteral node) {
        constructs.add(D);
    }

    @Override
    public void visit(OWLFacetRestriction node) {
        constructs.add(D);
    }

    // class expressions
    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        constructs.add(AL);
        for (OWLClassExpression operands : ce.getOperands()) {
            operands.accept(this);
        }
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        constructs.add(U);
        for (OWLClassExpression operands : ce.getOperands()) {
            operands.accept(this);
        }
    }

    private static boolean isTop(OWLClassExpression classExpression) {
        return classExpression.isOWLThing();
    }

    private boolean isAtomic(OWLClassExpression classExpression) {
        if (classExpression.isAnonymous()) {
            return false;
        } else {
            for (OWLOntology ont : ontologies) {
                if (!ont.getAxioms((OWLClass) classExpression, EXCLUDED)
                        .isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        if (isAtomic(ce)) {
            constructs.add(AL);
        } else {
            constructs.add(C);
        }
        ce.getOperand().accept(this);
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        if (isTop(ce.getFiller())) {
            constructs.add(AL);
        } else {
            constructs.add(E);
        }
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        constructs.add(AL);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(OWLObjectHasValue ce) {
        constructs.add(O);
        constructs.add(E);
        ce.getProperty().accept(this);
    }

    private void checkCardinality(OWLDataCardinalityRestriction restriction) {
        if (restriction.isQualified()) {
            constructs.add(Q);
        } else {
            constructs.add(N);
        }
        restriction.getFiller().accept(this);
        restriction.getProperty().accept(this);
    }

    private void checkCardinality(OWLObjectCardinalityRestriction restriction) {
        if (restriction.isQualified()) {
            constructs.add(Q);
        } else {
            constructs.add(N);
        }
        restriction.getFiller().accept(this);
        restriction.getProperty().accept(this);
    }

    @Override
    public void visit(OWLObjectMinCardinality ce) {
        checkCardinality(ce);
    }

    @Override
    public void visit(OWLObjectExactCardinality ce) {
        checkCardinality(ce);
    }

    @Override
    public void visit(OWLObjectMaxCardinality ce) {
        checkCardinality(ce);
    }

    @Override
    public void visit(OWLObjectHasSelf ce) {
        ce.getProperty().accept(this);
        constructs.add(R);
    }

    @Override
    public void visit(OWLObjectOneOf ce) {
        constructs.add(U);
        constructs.add(O);
    }

    @Override
    public void visit(OWLDataSomeValuesFrom ce) {
        constructs.add(E);
        ce.getFiller().accept(this);
        ce.getProperty().accept(this);
    }

    @Override
    public void visit(OWLDataAllValuesFrom ce) {
        ce.getFiller().accept(this);
        ce.getProperty().accept(this);
    }

    @Override
    public void visit(OWLDataHasValue ce) {
        constructs.add(D);
        ce.getProperty().accept(this);
    }

    @Override
    public void visit(OWLDataMinCardinality ce) {
        checkCardinality(ce);
    }

    @Override
    public void visit(OWLDataExactCardinality ce) {
        checkCardinality(ce);
    }

    @Override
    public void visit(OWLDataMaxCardinality ce) {
        checkCardinality(ce);
    }

    // Axioms
    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        axiom.getSubClass().accept(this);
        axiom.getSuperClass().accept(this);
    }

    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        constructs.add(R);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        constructs.add(R);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        constructs.add(C);
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        axiom.getDomain().accept(this);
        constructs.add(AL);
        constructs.add(D);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        constructs.add(AL);
        axiom.getDomain().accept(this);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        constructs.add(H);
        for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        constructs.add(U);
        constructs.add(O);
        constructs.add(C);
    }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        constructs.add(D);
        for (OWLDataPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        constructs.add(R);
        for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        constructs.add(AL);
        axiom.getRange().accept(this);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        constructs.add(F);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        constructs.add(H);
        axiom.getSubProperty().accept(this);
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        constructs.add(U);
        constructs.add(C);
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        constructs.add(I);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        constructs.add(AL);
        constructs.add(D);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        constructs.add(F);
        constructs.add(D);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        constructs.add(H);
        constructs.add(D);
        for (OWLDataPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        axiom.getClassExpression().accept(this);
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        constructs.add(D);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        constructs.add(TRAN);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        constructs.add(R);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        constructs.add(H);
        constructs.add(D);
    }

    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        constructs.add(I);
        constructs.add(F);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        constructs.add(O);
    }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        constructs.add(R);
        for (OWLObjectPropertyExpression prop : axiom.getPropertyChain()) {
            prop.accept(this);
        }
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        constructs.add(I);
    }
}
