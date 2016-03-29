package uk.ac.ox.cs.pdq.fol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

// TODO: Auto-generated Javadoc
/**
 * A n-ary formula.
 *
 * @author Efthymia Tsamoura
 * @author Julien Leblay
 * @param <T> Type of input formulas
 */
public abstract class NaryFormula<T extends Formula> extends AbstractFormula implements Iterable<T> {

	/**  The formula's head operator. */
	protected final LogicalSymbols operator;

	/**  The subformulae. */
	protected final ImmutableList<T> children;

	/**
	 * Instantiates a new nary formula.
	 *
	 * @param operator 		Input head operator
	 * @param childen 		Input subformulas
	 */
	public NaryFormula(LogicalSymbols operator, Collection<T> childen) {
		super();
		this.operator = operator;
		this.children = ImmutableList.copyOf(childen);
	}

	/**
	 * Gets the children.
	 *
	 * @return Collection<T>
	 * @see uk.ac.ox.cs.pdq.fol.Formula#getSubFormulas()
	 */
	@Override
	public Collection<T> getChildren() {
		return this.children;
	}

	/**
	 * Gets the terms.
	 *
	 * @return List<Term>
	 * @see uk.ac.ox.cs.pdq.fol.Formula#getTerms()
	 */
	@Override
	public List<Term> getTerms() {
		List<Term> terms = new ArrayList<>();
		for (Formula atomics:this.children) {
			terms.addAll(atomics.getTerms());
		}
		return terms;
	}

	/**
	 * Gets the predicates.
	 *
	 * @return List<PredicateFormula>
	 * @see uk.ac.ox.cs.pdq.fol.Formula#getAtoms()
	 */
	@Override
	public List<Atom> getAtoms() {
		List<Atom> result = new ArrayList<>();
		for (Formula item: this.children) {
			result.addAll(item.getAtoms());
		}
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param o Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		return this.getClass().isInstance(o)
				&& this.operator.equals(((NaryFormula<T>) o).operator)
				&& this.children.equals(((NaryFormula<T>) o).children);
	}

	/**
	 * Hash code.
	 *
	 * @return int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.operator, this.children);
	}

	/**
	 * To string.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		return Joiner.on(this.operator.toString()).join(this.children);
	}

	/**
	 * Size.
	 *
	 * @return int
	 */
	public int size() {
		return this.children.size();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return boolean
	 */
	public boolean isEmpty() {
		return this.children.isEmpty();
	}

	/**
	 * Gets the symbol.
	 *
	 * @return LogicalSymbols
	 */
	public LogicalSymbols getSymbol() {
		return this.operator;
	}

	/**
	 * Iterator.
	 *
	 * @return Iterator<T>
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.children.iterator();
	}
}