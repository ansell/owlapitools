package decomposition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.MultiMap;

public class SigIndex {
    /** map between entities and axioms that contains them in their signature */
    MultiMap<OWLEntity, AxiomWrapper> Base = new MultiMap<OWLEntity, AxiomWrapper>();
    /** locality checker */
    LocalityChecker Checker;
    /** sets of axioms non-local wrt the empty signature */
    Set<AxiomWrapper> NonLocalTrue = new HashSet<AxiomWrapper>();
    Set<AxiomWrapper> NonLocalFalse = new HashSet<AxiomWrapper>();
    /** empty signature to test the non-locality */
    TSignature emptySig = new TSignature();
    /** number of registered axioms */
    int nRegistered = 0;
    /** number of registered axioms */
    int nUnregistered = 0;
    private AxiomStructure as;

    // access to statistics
    /** @return number of ever processed axioms */
    public int nProcessedAx() {
        return nRegistered;
    }

    /** add axiom AX to the non-local set with top-locality value TOP */
    private void checkNonLocal(AxiomWrapper ax, boolean top) {
        emptySig.setLocality(top);
        Checker.setSignatureValue(emptySig);
        if (!Checker.local(ax.getAxiom())) {
            if (top) {
                NonLocalFalse.add(ax);
            } else {
                NonLocalTrue.add(ax);
            }
        }
    }

    /** empty c'tor */
    public SigIndex(LocalityChecker c, AxiomStructure as) {
        Checker = c;
        this.as = as;
    }

    // work with axioms
    /** register an axiom */
    private void registerAx(AxiomWrapper ax) {
        for (OWLEntity p : ax.getAxiom().getSignature()) {
            Base.put(p, ax);
        }
        // check whether the axiom is non-local
        checkNonLocal(ax, false);
        checkNonLocal(ax, true);
        ++nRegistered;
    }

    /** unregister an axiom AX */
    private void unregisterAx(AxiomWrapper ax) {
        for (OWLEntity p : ax.getAxiom().getSignature()) {
            Base.get(p).remove(ax);
        }
        // remove from the non-locality
        NonLocalFalse.remove(ax);
        NonLocalTrue.remove(ax);
        ++nUnregistered;
    }

    /** process an axiom wrt its Used status */
    public void processAx(AxiomWrapper ax) {
        if (ax.isUsed()) {
            registerAx(ax);
        } else {
            unregisterAx(ax);
        }
    }

    // / preprocess given set of axioms
    public void preprocessOntology(Collection<AxiomWrapper> axioms) {
        for (AxiomWrapper ax : axioms) {
            processAx(ax);
        }
    }

    // / clear internal structures
    public void clear() {
        Base.clear();
        NonLocalFalse.clear();
        NonLocalTrue.clear();
    }

    // get the set by the index
    /** given an entity, return a set of all axioms that tontain this entity in */
    // a signature
    public Collection<AxiomWrapper> getAxioms(OWLEntity entity) {
        return Base.get(entity);
    }

    /** get the non-local axioms with top-locality value TOP */
    public Set<AxiomWrapper> getNonLocal(boolean top) {
        return top ? NonLocalFalse : NonLocalTrue;
    }
}
