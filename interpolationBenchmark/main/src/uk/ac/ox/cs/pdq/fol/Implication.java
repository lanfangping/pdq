package uk.ac.ox.cs.pdq.fol;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class Implication extends Formula {

	protected final List<Formula> children;

	/**  The unary operator. */
	protected final LogicalSymbols operator = LogicalSymbols.IMPLIES;
	
	/**  Cashed string representation of the atom. */
	private String toString = null;

	/** The hash. */
	private Integer hash;

	/**  Cashed list of atoms. */
	private List<Atom> atoms = null;

	/**  Cashed list of terms. */
	private List<Term> terms = null;

	/**  Cashed list of free variables. */
	private List<Variable> freeVariables = null;

	/**  Cashed list of bound variables. */
	private List<Variable> boundVariables = null;
	
	public Implication(List<Formula> children) {
		Preconditions.checkArgument(children != null);
		Preconditions.checkArgument(children.size() == 2);
		this.children = ImmutableList.copyOf(children);
	}
	
	public Implication(Formula... children) {
		Preconditions.checkArgument(children != null);
		Preconditions.checkArgument(children.length == 2);
		this.children = ImmutableList.copyOf(children);
	}

	public static Implication of(Formula... children) {
		return new Implication(children);
	}

	public static Implication of(List<Formula> children) {
		return new Implication(children);
	}
	
	/**
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
				&& this.children.equals(((Implication) o).children);
	}

	@Override
	public int hashCode() {
		if(this.hash == null) {
			this.hash = Objects.hash(this.operator, this.children);
		}
		return this.hash;
	}
	
	/**
	 * To string.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		if(this.toString == null) {
			this.toString = "";
			this.toString += "(" + this.children.get(0).toString() + " --> " + this.children.get(1).toString() + ")";
		}
		return this.toString;
	}

	@Override
	public int getId() {
		return this.hashCode();
	}

	@Override
	public List<Formula> getChildren() {
		return this.children;
	}

	@Override
	public List<Atom> getAtoms() {
		if(this.atoms == null) {
			Set<Atom> atoms = Sets.newLinkedHashSet();
			atoms.addAll(this.children.get(0).getAtoms());
			atoms.addAll(this.children.get(1).getAtoms());
			this.atoms = Lists.newArrayList(atoms);
		}
		return this.atoms;
	}

	@Override
	public List<Term> getTerms() {
		if(this.terms == null) {
			Set<Term> terms = Sets.newLinkedHashSet();
			terms.addAll(this.children.get(0).getTerms());
			terms.addAll(this.children.get(1).getTerms());
			this.terms = Lists.newArrayList(terms);
		}
		return this.terms;
	}

	@Override
	public List<Variable> getFreeVariables() {
		if(this.freeVariables == null) {
			Set<Variable> variables = Sets.newLinkedHashSet();
			variables.addAll(this.children.get(0).getFreeVariables());
			variables.addAll(this.children.get(1).getFreeVariables());
			this.freeVariables = Lists.newArrayList(variables);
		}
		return this.freeVariables;
	}

	@Override
	public List<Variable> getBoundVariables() {
		if(this.boundVariables == null) {
			Set<Variable> variables = Sets.newLinkedHashSet();
			variables.addAll(this.children.get(0).getBoundVariables());
			variables.addAll(this.children.get(1).getBoundVariables());
			this.boundVariables = Lists.newArrayList(variables);
		}
		return this.boundVariables;
	}
}
