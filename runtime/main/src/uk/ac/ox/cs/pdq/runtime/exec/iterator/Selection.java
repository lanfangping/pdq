package uk.ac.ox.cs.pdq.runtime.exec.iterator;

import java.util.NoSuchElementException;

import uk.ac.ox.cs.pdq.algebra.predicates.Predicate;
import uk.ac.ox.cs.pdq.util.Tuple;

import com.google.common.base.Preconditions;

/**
 * Selection operator.
 * 
 * @author Julien Leblay
 */
public class Selection extends UnaryIterator {

	/** The predicate associated with this selection. */
	private final Predicate predicate;

	/** The next Tuple to return */
	private Tuple nextTuple = null;

	/**
	 * Instantiates a new selection.
	 * @param p Predicate
	 * @param child TupleIterator
	 */
	public Selection(Predicate p, TupleIterator child) {
		super(child);
		Preconditions.checkArgument(p != null);
		this.predicate = p;
	}

	/**
	 * Prepares the next tuple to be returned. If the end of the iterator
	 * was reached, this.nextTuple is null.
	 */
	private void nextTuple() {
		while (this.child.hasNext()) {
			Tuple next = this.child.next();
			if (this.predicate.isSatisfied(next)) {
				this.nextTuple = next;
				return;
			}
		}
		this.nextTuple = null;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.runtime.exec.iterator.UnaryIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		Preconditions.checkState(this.open != null && this.open);
		if (this.interrupted) {
			return false;
		}
		if (this.nextTuple != null) {
			return true;
		}
		this.nextTuple();
		return this.nextTuple != null;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.runtime.exec.iterator.TupleIterator#next()
	 */
	@Override
	public Tuple next() {
		if (this.eventBus != null) {
			this.eventBus.post(this);
		}
		this.hasNext();
		if (this.nextTuple == null) {
			throw new NoSuchElementException();
		}
		Tuple result = this.nextTuple;
		this.nextTuple();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.runtime.exec.iterator.TupleIterator#deepCopy()
	 */
	@Override
	public Selection deepCopy() {
		return new Selection(this.predicate, this.child.deepCopy());
	}

	/**
	 * @return Predicate
	 */
	public Predicate getPredicate() {
		return this.predicate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getSimpleName());
		result.append('{').append(this.predicate).append('}');
		result.append('(').append(this.child.toString()).append(')');
		return result.toString();
	}
}
