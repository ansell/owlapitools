/*
 * Date: Jan 13, 2012
 *
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)
 *
 * copyright 2012, Ignazio Palmisano, University of Manchester
 *
 */
package utils.reasonercomparator;

import org.kohsuke.MetaInfServices;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactoryRegistry;

@MetaInfServices(org.semanticweb.owlapi.reasoner.OWLReasonerFactory.class)
public class ComparatorReasonerFactory implements OWLReasonerFactory {
	public static final String HERMIT = "HermiT";
	public static final String FACT = "FaCT++";
	public static final String JFACT = "JFact";

	private static OWLReasonerFactory getFactory(String s) {
		try {
			return OWLReasonerFactoryRegistry.getInstance().getReasonerFactory(s);
		} catch (Exception e) {
			throw new RuntimeException("Problem instantiating factory: " + s, e);
		}
	}

	public String getReasonerName() {
		return "ComparatorReasoner";
	}

	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
		return new ComparisonReasoner(ontology, null, getFactory(HERMIT),
				getFactory(JFACT), getFactory(FACT));
	}

	public OWLReasoner createReasoner(OWLOntology ontology) {
		return new ComparisonReasoner(ontology, null, getFactory(HERMIT),
				getFactory(JFACT), getFactory(FACT));
	}

	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config) throws IllegalConfigurationException {
		return new ComparisonReasoner(ontology, config, getFactory(HERMIT),
				getFactory(JFACT), getFactory(FACT));
	}

	public OWLReasoner createReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config) throws IllegalConfigurationException {
		return new ComparisonReasoner(ontology, config, getFactory(HERMIT),
				getFactory(JFACT), getFactory(FACT));
	}
}
