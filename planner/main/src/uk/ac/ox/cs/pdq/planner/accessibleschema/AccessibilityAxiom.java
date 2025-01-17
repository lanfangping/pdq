// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.planner.accessibleschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.ox.cs.pdq.db.AccessMethodDescriptor;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.fol.Formula;
import uk.ac.ox.cs.pdq.fol.TGD;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.fol.Variable;

/**
 * 
 * For an access method mt on relation R of arity n with input positions j1, ..., jm 
 * an accessibility axiom is a rule of the form
 * accessible(x_{j_1}) \wegde ... \wedge accessible(x_{j_m}) \wedge R(x_1, ..., x_n) -->
 * InferredAccessible R(x_1, ..., x_n) \wedge \Wedge_{j} accessible(x_j)
 * 
 *
 * @author Efthymia Tsamoura
 */
public class AccessibilityAxiom extends TGD {	
	private static final long serialVersionUID = 4888518579167846542L;

	/**  The access method that this axiom maps to *. */
	private final AccessMethodDescriptor method;
	
	private final Relation relation;

	/**
	 * Instantiates a new accessibility axiom.
	 *
	 * @param relation 		An inferred accessible relation
	 * @param method 		A method to access this relation
	 */
	public AccessibilityAxiom(Relation relation, AccessMethodDescriptor method) {
		super(createLeft(relation, method), createRight(relation, method));
		this.relation = relation;
		this.method = method;
	}

	/**
	 *
	 * @param relation 		A schema relation 
	 * @param method 		A method to access this relation
	 * @return 		the atoms of the left-hand side of the accessibility axiom that corresponds to the input relation and the input access method
	 */
	private static Atom[] createLeft(Relation relation, AccessMethodDescriptor method) {
		List<Formula> leftAtoms = new ArrayList<>();
		Integer[] inputPositions = method.getInputs();
		Atom atom = createAtom(relation);
		Term[] terms = atom.getTerms();
		for (int inputPosition: inputPositions) 
			leftAtoms.add(Atom.create(AccessibleSchema.accessibleRelation, terms[inputPosition]));
		leftAtoms.add(atom);
		return leftAtoms.toArray(new Atom[leftAtoms.size()]);
	}

	private static Atom createAtom(Relation relation) {
		Term[] terms = new Term[relation.getArity()];
		Attribute[] attributes = relation.getAttributes();
		for (int index = 0; index < relation.getArity(); ++index) 
			terms[index] = Variable.create(attributes[index].getName());
		return Atom.create(relation, terms);
	}

	/**
	 *
	 * @param relation the inf acc rel
	 * @param method the binding
	 * @return 		the atoms of the right-hand side of the accessibility axiom that corresponds to the input relation and the input access method
	 */
	private static Atom[] createRight(Relation relation, AccessMethodDescriptor method) {
		List<Formula> rightAtoms = new ArrayList<>();
		Integer[] inputPositions = method.getInputs();
		Atom f = createAtom(Relation.create(AccessibleSchema.inferredAccessiblePrefix + relation.getName(), relation.getAttributes(), new AccessMethodDescriptor[]{}, relation.isEquality()));
		Term[] terms = f.getTerms();
		for (int i = 0; i < terms.length; ++i) {
			if (!Arrays.asList(inputPositions).contains(i)) 
				rightAtoms.add(Atom.create(AccessibleSchema.accessibleRelation, terms[i]));
		}
		rightAtoms.add(f);
		return rightAtoms.toArray(new Atom[rightAtoms.size()]);
	}

	/**
	 *
	 * @return the base relation of the accessibility axiom.
	 */
	public Relation getBaseRelation() {
		return this.relation;
	}

	/**
	 *
	 * @return the access method of the accessibility axiom.
	 */
	public AccessMethodDescriptor getAccessMethod() {
		return this.method;
	}

	/**
	 *
	 * @return PredicateFormula
	 * @see uk.ac.ox.cs.pdq.fol.GuardedDependency#getGuard()
	 */
	public Atom getGuard() {
		return this.getBodyAtom(this.getNumberOfBodyAtoms()-1);
	}
}
