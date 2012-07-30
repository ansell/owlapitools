/*
 * Date: Jul 13, 2011
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
package org.coode.suggestor.test;

import java.util.Arrays;

import org.coode.suggestor.util.ReasonerHelper;
import org.junit.Assert;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactoryRegistry;
import org.semanticweb.owlapi.vocab.OWLFacet;

public class UtilTest extends AbstractSuggestorTest {
	public void testGetSubDatatypes() throws Exception {
		OWLOntology ont = createOntology();
        final OWLReasonerFactory reasonerFactory = OWLReasonerFactoryRegistry.getInstance().getReasonerFactory("JFact");
        Assert.assertNotNull("Unable to find JFact reasoner for testing", reasonerFactory);
        OWLReasoner r = reasonerFactory.createNonBufferingReasoner(ont);
//		SuggestorFactory fac = new SuggestorFactory(r);
//		PropertySuggestor ps = fac.getPropertySuggestor();
//		FillerSuggestor fs = fac.getFillerSuggestor();
		// this is not supported by reasoners
		final ReasonerHelper helper = new ReasonerHelper(r);
		OWLDataProperty p = df.getOWLDataProperty(IRI.create("http://example.com/p"));
		OWLDatatype integer = df.getIntegerOWLDatatype();
		OWLDatatype flt = df.getFloatOWLDatatype();
		OWLDatatype dbl = df.getDoubleOWLDatatype();
		mngr.applyChanges(Arrays.asList(
				new AddAxiom(ont, df.getOWLFunctionalDataPropertyAxiom(p)), new AddAxiom(
						ont, df.getOWLDeclarationAxiom(integer)),
				new AddAxiom(ont, df.getOWLDeclarationAxiom(dbl)),
				new AddAxiom(ont, df.getOWLDeclarationAxiom(flt))));
		NodeSet<OWLDatatype> subs = helper.getSubtypes(df.getTopDatatype());
		System.out.println(subs);
		assertEquals(3, subs.getNodes().size());
		assertTrue(subs.containsEntity(integer));
		assertTrue(subs.containsEntity(flt));
		assertTrue(subs.containsEntity(dbl));
		NodeSet<OWLDatatype> subsOfFloat = helper.getSubtypes(dbl);
		System.out.println(subsOfFloat);
		assertEquals(0, subsOfFloat.getNodes().size());
		// between incomparable datatypes
		assertFalse(helper.isSubtype(flt, integer));
		// between a range and a named type
		assertTrue(helper.isSubtype(
				df.getOWLDatatypeRestriction(integer,
						df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 2)), integer));
		assertFalse(helper
				.isSubtype(
						df.getOWLDatatypeRestriction(flt,
								df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 2.0f)),
						integer));
		// between two integer ranges
		assertTrue(helper.isSubtype(
				df.getOWLDatatypeRestriction(integer,
						df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 4)),
				df.getOWLDatatypeRestriction(integer,
						df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 2))));
	}

	public void testAssertedRange() {}
}
