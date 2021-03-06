package decomposition.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposer;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;

@SuppressWarnings("javadoc")
public class AtomicDecomposerDepedenciesTest {
    @Test
    public void atomicDecomposerDepedenciesTest() throws OWLOntologyCreationException {
        // given
        OWLOntology o = getOntology();
        assertEquals(3, o.getAxiomCount());
        AtomicDecomposer ad = new AtomicDecomposerOWLAPITOOLS(o);
        assertEquals(3, ad.getAtoms().size());
        Atom atom = ad.getBottomAtoms().iterator().next();
        assertNotNull(atom);
        // when
        Set<Atom> dependencies = ad.getDependencies(atom, true);
        Set<Atom> dependencies2 = ad.getDependencies(atom, false);
        dependencies2.remove(atom);
        // then
        assertEquals(2, dependencies2.size());
        assertEquals(1, dependencies.size());

    }

    private OWLOntology getOntology() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLClass powerYoga = f.getOWLClass(IRI.create("urn:test#PowerYoga"));
        OWLClass yoga = f.getOWLClass(IRI.create("urn:test#Yoga"));
        OWLClass relaxation = f.getOWLClass(IRI.create("urn:test#Relaxation"));
        OWLClass activity = f.getOWLClass(IRI.create("urn:test#Activity"));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(powerYoga, yoga));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(yoga, relaxation));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(relaxation, activity));
        return o;
    }
}
