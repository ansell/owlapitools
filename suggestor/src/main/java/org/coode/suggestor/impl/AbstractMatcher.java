package org.coode.suggestor.impl;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

abstract class AbstractMatcher<R extends OWLPropertyRange, F extends R, P extends OWLPropertyExpression<R, P>>
		implements Matcher<R, F, P> {
	public AbstractMatcher() {}

	@Override
    public final boolean isMatch(OWLClassExpression c, P p, R f, boolean direct) {
		if (!direct) {
			return isMatch(c, p, f);
		}
		if (!isMatch(c, p, f)) {
			return false;
		}
		final NodeSet<F> directSubs = getDirectSubs(f);
		for (Node<F> node : directSubs) {
			final F representativeElement = node.getRepresentativeElement();
			if (representativeElement == null) {
				System.out.println("FillerSuggestorImpl.AbstractMatcher.isMatch() " + f);
			}
			if (isMatch(c, p, representativeElement)) {
				return false;
			}
		}
		return true;
	}

	@Override
    public final NodeSet<F> getLeaves(OWLClassExpression c, P p, R start, boolean direct) {
		Set<Node<F>> nodes = new HashSet<Node<F>>();
		if (isMatch(c, p, start)) {
			for (Node<F> sub : getDirectSubs(start)) {
				nodes.addAll(getLeaves(c, p, sub.getRepresentativeElement(), direct)
						.getNodes());
			}
			if (!direct || (nodes.isEmpty() && !start.isTopEntity())) {
				nodes.add(getEquivalents(start)); // non-optimal as we already had the node before recursing
			}
		}
		return createNodeSet(nodes);
	}

	@Override
    public final NodeSet<F> getRoots(OWLClassExpression c, P p, R start, boolean direct) {
		Set<Node<F>> nodes = new HashSet<Node<F>>();
		for (Node<F> sub : getDirectSubs(start)) {
			if (isMatch(c, p, sub.getRepresentativeElement())) {
				nodes.add(sub);
				if (!direct) {
					nodes.addAll(getRoots(c, p, sub.getRepresentativeElement(), direct)
							.getNodes());
				}
			}
		}
		return createNodeSet(nodes);
	}

	protected abstract NodeSet<F> getDirectSubs(R f);

	protected abstract Node<F> getEquivalents(R f);

	protected abstract NodeSet<F> createNodeSet(Set<Node<F>> nodes);
}