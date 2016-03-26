package com.oe;

import java.io.Serializable;
import java.util.*;
import javax.annotation.Nonnull;

import static org.semanticweb.owlapi.model.parameters.Imports.EXCLUDED;
import static org.semanticweb.owlapi.util.DLExpressivityChecker.Construct.*;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.verifyNotNull;

import org.semanticweb.owlapi.model.*;

/**
 * An extension of the owlapi DLExpressivityChecker which add functionality to collect the letters used to construct
 * the expressivity statement and the reason why those letters occurred.
 * It further adds functionality to explain how the final expressivity string was constructed.
 * <p>
 * The code in this class to calculate the expressivity of the ontology is based entirely on the DLExpressivityChecker code which can be found here:
 * https://github.com/owlcs/owlapi/blob/version4/api/src/main/java/org/semanticweb/owlapi/util/DLExpressivityChecker.java
 *
 * @author ⁠⁠⁠⁠Muhummad Patel⁠⁠⁠⁠⁠
 */
public class ExpressivityChecker extends org.semanticweb.owlapi.util.DLExpressivityChecker {

    private final Set<Construct> constructsSet; // the original set, we needed the list below because we need to see letters being added
    private final ArrayList<Construct> constructs; // So we can examine which letter each axiom is adding
    private final HashMap<String, ArrayList<OWLAxiom>> axiomClassifications; // to classify the axioms by letter
    private final Set<OWLOntology> ontologies;
    private String axiomClassificationExplanation; // to explain the actions of the pruneConstructs method


    // ============ New/augmented methods ================
    /**
     * @param ontologies ontologies
     */
    public ExpressivityChecker(Set<OWLOntology> ontologies) {
        super(ontologies);
        this.ontologies = ontologies;
        constructsSet = new HashSet<>();
        constructs = new ArrayList<>();
        axiomClassifications = new HashMap<>();
        axiomClassificationExplanation = "";
    }
    /**
     * Returns an AxiomClassificationResult (static class nested in this class)
     * that contains a hashmap mapping from expressivity letters to the axioms
     * in the ontology. It also contains a string explaining how the Expressivity
     * was altered by the pruneConstructs method. Interesting to see how the constructs
     * consume and replace each other.
     */
    public AxiomClassificationResult getAxiomClassifications() {
        if (axiomClassifications.isEmpty()) {
            getOrderedConstructs();
        }

        return (new AxiomClassificationResult(axiomClassifications, axiomClassificationExplanation));
    }

    /**
     * This method (based on the original in the base class) has been edited to
     * write the explanations for the expressivity string simplifications to the
     * explanation String for later presentation to the user.
     */
    private void pruneConstructs() {
        String explanation = "";
        String indent = "    * ";
        if (constructsSet.contains(AL)) {
            // AL + U + E can be represented using ALC
            if (constructsSet.contains(C)) {
                // Remove existential because this can be represented
                // with AL + Neg
                if (constructsSet.remove(E)) {
                    explanation += "~ E removed because ALC is present\n";
                    explanation += indent + "E can be represented with AL + negation\n";
                }
                // Remove out union (intersection + negation (demorgan))
                if (constructsSet.remove(U)) {
                    explanation += "~ U removed because ALC is present\n";
                    explanation += indent + "U can be represented with intersection + negation\n";
                }
            } else if (constructsSet.contains(E) && constructsSet.contains(U)) {
                // Simplify to ALC
                constructsSet.add(AL);
                constructsSet.add(C);
                if (constructsSet.remove(E)) {
                    explanation += "~ E ";
                    if (constructsSet.remove(U)) {
                        explanation += " and U replaced with ALC\n";
                    } else {
                        explanation += "replaced with ALC\n";
                    }
                } else if (constructsSet.remove(U)) {
                    explanation += "~ U replaced with ALC\n";
                }
            }
        }
        if (constructsSet.contains(N) || constructsSet.contains(Q)) {
            if (constructsSet.remove(F)) {
                explanation += "~ F removed because N and/or Q is present\n";
            }
        }
        if (constructsSet.contains(Q)) {
            if (constructsSet.remove(N)) {
                explanation += "~ N removed because Q is present\n";
                explanation += indent + "Q is more general than N\n";
            }
        }
        if (constructsSet.contains(AL) && constructsSet.contains(C)
                && constructsSet.contains(TRAN)) {
            if (constructsSet.remove(AL) | constructsSet.remove(C) | constructsSet.remove(TRAN)) {
                explanation += "~ ALC and + replaced with S\n";
            }
            constructsSet.add(S);
        }
        if (constructsSet.contains(R)) {
            if (constructsSet.remove(H)) {
                explanation += "~ H removed because R is present\n";
                explanation += indent + "R allows for H\n";
            }
        }

        axiomClassificationExplanation = explanation;
    }


    /**
     * This method has been edited from the original to keep track of which axioms
     * are adding which letters to the expressivity string. The axioms are placed
     * in the appropriate buckets in the axiomClassifications hashtable based on which
     * letter they added to the expressivity string.
     */
    private List<Construct> getOrderedConstructs() {
        axiomClassifications.clear();
        constructsSet.clear();
        constructs.clear();
        constructs.add(AL);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                ax.accept(this);

                // Classify this axiom into the appropriate construct letter bucket
                String lastAdded = constructs.get(constructs.size() - 1).toString();
                if (axiomClassifications.containsKey(lastAdded)) {
                    axiomClassifications.get(lastAdded).add(ax);
                } else {
                    ArrayList<OWLAxiom> axiomsList = new ArrayList<>();
                    axiomsList.add(ax);
                    axiomClassifications.put(lastAdded, axiomsList);
                }
            }
        }

        // put the constructs arraylist into the constructs set (remove duplicates)
        for (Construct c : constructs) {
            constructsSet.add(c);
        }

        pruneConstructs();
        List<Construct> cons = new ArrayList<>(constructsSet);
        Collections.sort(cons, new ConstructComparator());
        return cons;
    }

    /**
     * Used to return the axiomClassifications and the explanation for why some
     * letters were added/removed by the pruneConstructs method.
     */
    public static class AxiomClassificationResult {
        public HashMap<String, ArrayList<OWLAxiom>> classifications;
        public String explanation;

        AxiomClassificationResult(HashMap<String, ArrayList<OWLAxiom>> c, String e) {
            classifications = c;
            explanation = e;
        }
    }

    // ============ From base class ================
    // These methods are in the base class, but we need to reproduce them here
    // because they access private members of the base class.

    /**
     * @return ordered constructs
     */
    public List<Construct> getConstructs() {
        return getOrderedConstructs();
    }

    /**
     * @return DL name
     */
    @Nonnull
    public String getDescriptionLogicName() {
        StringBuilder s = new StringBuilder();
        for (Construct c : getOrderedConstructs()) {
            s.append(c);
        }
        return verifyNotNull(s.toString());
    }

    /**
     * A comparator that orders DL constucts to produce a traditional DL name.
     */
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
