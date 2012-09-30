package decomposition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/** atomical decomposer of the ontology */
public class AtomicDecomposer {
    /** atomic structure to build */
    AOStructure AOS = null;
    /** modularizer to build modules */
    TModularizer Modularizer;
    /** tautologies of the ontology */
    List<OWLAxiom> Tautologies = new ArrayList<OWLAxiom>();
    /** progress indicator */
    ProgressIndicatorInterface PI = null;
    /** fake atom that represents the whole ontology */
    TOntologyAtom rootAtom = null;
    /** module type for current AOS creation */
    ModuleType type;
    private AxiomStructure as;

    public AtomicDecomposer(TModularizer c, AxiomStructure as) {
        Modularizer = c;
        this.as = as;
    }

    public TModularizer getModularizer() {
        return Modularizer;
    }

    /** restore all tautologies back */
    void restoreTautologies() {
        for (OWLAxiom p : Tautologies) {
            as.use(p);
        }
    }

    /** set progress indicator to be PI */
    void setProgressIndicator(ProgressIndicatorInterface pi) {
        PI = pi;
    }

    // #define RKG_DEBUG_AD
    /** remove tautologies (axioms that are always local) from the ontology */
    // temporarily
    void removeTautologies(List<OWLAxiom> axioms) {
        // we might use it for another decomposition
        Tautologies.clear();
        long nAx = 0;
        for (OWLAxiom p : axioms) {
            final String string = p.toString();
            if (string.contains("Declaration")) {
                Modularizer.extract(p, new TSignature(p.getSignature()), type);
            }
            if (as.isUsed(p)) {
                // check whether an axiom is local wrt its own signature
                Modularizer.extract(p, new TSignature(p.getSignature()), type);
                if (Modularizer.isTautology(p, type)) {
                    Tautologies.add(p);
                    as.disuse(p);
                } else {
                    ++nAx;
                }
            }
        }
        if (PI != null) {
            PI.setLimit(nAx);
        }
    }

    public List<OWLAxiom> getTautologies() {
        return new ArrayList<OWLAxiom>(Tautologies);
    }

    /** build a module for given axiom AX; use parent atom's module as a base */
    // for the module search
    TOntologyAtom buildModule(TSignature sig, TOntologyAtom parent) {
        // build a module for a given signature
        Modularizer.extract(parent.getModule(), sig, type);
        List<OWLAxiom> Module = Modularizer.getModule();
        // if module is empty (empty bottom atom) -- do nothing
        if (Module.isEmpty()) {
            return null;
        }
        // here the module is created; report it
        if (PI != null) {
            PI.incIndicator();
        }
        // check if the module corresponds to a PARENT one; modules are the same
        // iff their sizes are the same
        if (parent != rootAtom && Module.size() == parent.getModule().size()) {
            return parent;
        }
        // create new atom with that module
        TOntologyAtom atom = AOS.newAtom();
        atom.setModule(Module);
        return atom;
    }

    /** create atom for given axiom AX; use parent atom's module as a base for */
    // the module search
    TOntologyAtom createAtom(OWLAxiom ax, TOntologyAtom parent) {
        // check whether axiom already has an atom
        TOntologyAtom atom = as.getAtom(ax);
        if (atom != null) {
            return atom;
        }
        // build an atom: use a module to find atomic dependencies
        atom = buildModule(new TSignature(ax.getSignature()), parent);
        // no empty modules should be here
        assert atom != null;
        // register axiom as a part of an atom
        atom.addAxiom(ax, as);
        // if atom is the same as parent -- nothing more to do
        if (atom == parent) {
            return parent;
        }
        // not the same as parent: for all atom's axioms check their atoms and
        // make ATOM depend on them
        // #ifdef RKG_DEBUG_AD
        /** / do cycle via set to keep the order */
        // typedef std::set<TDLAxiom*> AxSet;
        // Set<Axiom> M=new HashSet<Axiom> ( atom.getModule() );
        // for ( Axiom q : M )
        // #else
        for (OWLAxiom q : atom.getModule()) {
            // #endif
            if (!q.equals(ax)) {
                atom.addDepAtom(createAtom(q, atom));
            }
        }
        return atom;
    }

    public AOStructure getAOS() {
        return AOS;
    }

    /** get the atomic structure for given module type T */
    public AOStructure getAOS(List<OWLAxiom> axioms, ModuleType t) {
        // remember the type of the module
        type = t;
        // prepare a new AO structure
        AOS = new AOStructure();
        // init semantic locality checker
        // init semantic locality checker
        Modularizer.preprocessOntology(axioms);
        // we don't need tautologies here
        removeTautologies(axioms);
        // init the root atom
        rootAtom = new TOntologyAtom();
        rootAtom.setModule(new HashSet<OWLAxiom>(axioms));
        // build the "bottom" atom for an empty signature
        TOntologyAtom BottomAtom = buildModule(new TSignature(), rootAtom);
        if (BottomAtom != null) {
            for (OWLAxiom q : BottomAtom.getModule()) {
                BottomAtom.addAxiom(q, as);
            }
        }
        // create atoms for all the axioms in the ontology
        for (OWLAxiom p : axioms) {
            if (as.isUsed(p) && as.getAtom(p) == null) {
                createAtom(p, rootAtom);
            }
        }
        // restore tautologies in the ontology
        restoreTautologies();
        // System.out.println("AtomicDecomposer.getAOS()\nThere were "
        // + Modularizer.getNNonLocal() + " non-local axioms out of "
        // + Modularizer.getNChecks() + " totally checked");
        // clear the root atom
        rootAtom = null;
        // reduce graph
        AOS.reduceGraph();
        return AOS;
    }
}
