/*
 * Date: Dec 17, 2007
 *
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)
 *
 * copyright 2007, The University of Manchester
 *
 * Author: Nick Drummond
 * http://www.cs.man.ac.uk/~drummond/
 * Bio Health Informatics Group
 * The University Of Manchester
 */
package org.coode.suggestor.impl;

import org.coode.suggestor.api.PropertySanctionRule;
import org.coode.suggestor.api.PropertySuggestor;
import org.coode.suggestor.util.RestrictionAccumulator;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Check the restrictions on the class for min cardi zeros
 */
public class MinCardinalitySanctionRule implements PropertySanctionRule {
	private OWLReasoner r;

	public void setSuggestor(PropertySuggestor ps) {
		this.r = ps.getReasoner();
	}

	public boolean meetsSanction(OWLClassExpression c, OWLObjectPropertyExpression p) {
		RestrictionAccumulator acc = new RestrictionAccumulator(r);
		for (OWLObjectMinCardinality restr : acc.getRestrictions(c, p,
				OWLObjectMinCardinality.class)) {
			if (restr.getCardinality() == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean meetsSanction(OWLClassExpression c, OWLDataProperty p) {
		RestrictionAccumulator acc = new RestrictionAccumulator(r);
		for (OWLDataMinCardinality restr : acc.getRestrictions(c, p,
				OWLDataMinCardinality.class)) {
			if (restr.getCardinality() == 0) {
				return true;
			}
		}
		return false;
	}
}
