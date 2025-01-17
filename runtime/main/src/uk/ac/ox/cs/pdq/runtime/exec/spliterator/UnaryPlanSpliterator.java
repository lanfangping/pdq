// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.runtime.exec.spliterator;

import java.util.Spliterator;

import uk.ac.ox.cs.pdq.db.tuple.Tuple;

public abstract class UnaryPlanSpliterator implements Spliterator<Tuple> {

	protected Spliterator<Tuple> childSpliterator;
	
	public UnaryPlanSpliterator(Spliterator<Tuple> childSpliterator) {
		this.childSpliterator = childSpliterator;
	}
	
	@Override
	public Spliterator<Tuple> trySplit() {
		return childSpliterator.trySplit();
	}

	@Override
	public long estimateSize() {
		return childSpliterator.estimateSize();
	}

	@Override
	public int characteristics() {
		return childSpliterator.characteristics();
	}
}
