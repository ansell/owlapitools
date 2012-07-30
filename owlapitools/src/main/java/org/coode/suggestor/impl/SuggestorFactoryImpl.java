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

import org.coode.suggestor.api.FillerSuggestor;
import org.coode.suggestor.api.PropertySuggestor;
import org.coode.suggestor.api.SuggestorFactory;
import org.kohsuke.MetaInfServices;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Implementation binding.
 */
@MetaInfServices(org.coode.suggestor.api.SuggestorFactory.class)
public class SuggestorFactoryImpl implements SuggestorFactory {
	private final OWLReasoner r;

	public SuggestorFactoryImpl(OWLReasoner r) {
		if (r == null) {
			throw new IllegalArgumentException("Reasoner cannot be null");
		}
		this.r = r;
	}

	@Override
	public final PropertySuggestor getPropertySuggestor() {
		return new PropertySuggestorImpl(r);
	}

    @Override
	public final FillerSuggestor getFillerSuggestor() {
		return new FillerSuggestorImpl(r);
	}
}
