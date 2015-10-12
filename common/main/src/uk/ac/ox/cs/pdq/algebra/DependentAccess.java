package uk.ac.ox.cs.pdq.algebra;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import uk.ac.ox.cs.pdq.db.AccessMethod;
import uk.ac.ox.cs.pdq.db.AccessMethod.Types;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.TypedConstant;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.plan.AccessOperator;
import uk.ac.ox.cs.pdq.util.TupleType;
import uk.ac.ox.cs.pdq.util.Utility;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;


/**
 * Logical operator representation of a dependent access.
 *
 * @author Efthymia Tsamoura
 * @author Julien Leblay
 */
public class DependentAccess  extends RelationalOperator implements AccessOperator {

	/** The input table of the access. */
	private final Relation relation;

	/** The access method to use. */
	private final AccessMethod accessMethod;

	/** The output columns */
	protected final List<Term> columns;

	/** The input terms*/
	protected final List<Term> inputTerms;

	/** The constants used to call the underlying access method */
	protected final Map<Integer, TypedConstant<?>> staticInputs;

	/** The output terms of this access*/
	protected final List<Term> outputTerms;

	/**
	 *
	 * @param relation
	 * @param accessMethod
	 */
	public DependentAccess(Relation relation, AccessMethod accessMethod) {
		this(relation, accessMethod, Utility.typedToTerms(attributesOf(relation)));
	}

	/**
	 *
	 * @param relation
	 * @param accessMethod
	 * @param terms
	 */
	public DependentAccess(Relation relation, AccessMethod accessMethod, List<Term> terms) {
		this(relation, accessMethod, terms, inferStaticInput(terms, accessMethod));
	}

	/**
	 * Instantiates a new projection.
	 *
	 * @param relation Relation
	 * @param accessMethod AccessMethod
	 * @param staticInput Map<Integer,TypedConstant<?>>
	 */
	public DependentAccess(Relation relation, AccessMethod accessMethod, Map<Integer, TypedConstant<?>> staticInput) {
		this(relation, accessMethod, Utility.typedToTerms(attributesOf(relation)), staticInput);
	}

	/**
	 * Instantiates a new projection.
	 *
	 * @param relation Relation
	 * @param accessMethod AccessMethod
	 * @param staticInput Map<Integer,TypedConstant<?>>
	 */
	public DependentAccess(Relation relation, AccessMethod accessMethod, List<Term> outputTerms, Map<Integer, TypedConstant<?>> staticInput) {
		super(inferInputType(attributesOf(relation), accessMethod, keySetOf(staticInput)),
				TupleType.DefaultFactory.createFromTyped(attributesOf(relation)));
		Preconditions.checkArgument(relation.getAccessMethods().contains(accessMethod));
		Preconditions.checkArgument(accessMethod.getType() == Types.FREE ? staticInput.isEmpty() : true);
		this.relation = relation;
		this.accessMethod = accessMethod;
		this.columns = Utility.typedToTerms(attributesOf(relation));
		this.staticInputs = staticInput;
		this.inputTerms = Utility.typedToTerms(inferInputTerms(attributesOf(relation), accessMethod, keySetOf(staticInput)));
		this.outputTerms = Lists.newArrayList(outputTerms);

	}

	private static List<Attribute> attributesOf(Relation relation) {
		Preconditions.checkArgument(relation != null);
		return relation.getAttributes();
	}

	private static Set<Integer> keySetOf(Map<Integer, TypedConstant<?>> inputs) {
		Preconditions.checkArgument(inputs != null);
		return inputs.keySet();
	}

	/**
	 * @param columns List<Term>
	 * @param accessMethod AccessMethod
	 * @return Map<Integer,TypedConstant<?>>
	 */
	private static Map<Integer, TypedConstant<?>> inferStaticInput(List<Term> columns, AccessMethod accessMethod) {
		Preconditions.checkArgument(columns != null);
		Preconditions.checkArgument(accessMethod != null);
		Map<Integer, TypedConstant<?>> result = new LinkedHashMap<>();
		for(Integer i: accessMethod.getInputs()) {
			Term t = columns.get(i - 1);
			if (!(t.isSkolem() || t.isVariable())) {
				result.put(i - 1, ((TypedConstant) t));
			}
		}
		return result;
	}

	/**
	 * @param columns List<Attribute>
	 * @param accessMethod AccessMethod
	 * @param exclude Set<Integer>
	 * @return TupleType
	 */
	private static TupleType inferInputType(List<Attribute> columns, AccessMethod accessMethod, Set<Integer> exclude) {
		Preconditions.checkArgument(columns != null);
		Preconditions.checkArgument(accessMethod != null);
		Preconditions.checkArgument(exclude != null);
		return TupleType.DefaultFactory.createFromTyped(inferInputTerms(columns, accessMethod, exclude));
	}

	/**
	 * @param columns List<Attribute>
	 * @param binding AccessMethod
	 * @param exclude Set<Integer>
	 * @return List<Attribute>
	 */
	private static List<Attribute> inferInputTerms(List<Attribute> columns, AccessMethod binding, Set<Integer> exclude) {
		List<Attribute> result = new ArrayList<>();
		for(Integer i: binding.getInputs()) {
			if (!exclude.contains(i - 1)) {
				result.add(columns.get(i - 1));
			}
		}
		return result;
	}

	/**
	 * @return Integer
	 */
	@Override
	public Integer getDepth() {
		return 1;
	}

	/**
	 * @return the static inputs of this access.
	 */
	public Map<Integer, TypedConstant<?>> getStaticInputs() {
		return this.staticInputs;
	}

	/**
	 * @return the accessed relation
	 * @see uk.ac.ox.cs.pdq.plan.AccessOperator#getRelation()
	 */
	@Override
	public Relation getRelation() {
		return this.relation;
	}

	/**
	 * @return the access method used
	 * @see uk.ac.ox.cs.pdq.plan.AccessOperator#getAccessMethod()
	 */
	@Override
	public AccessMethod getAccessMethod() {
		return this.accessMethod;
	}


	/**
	 * @param i int
	 * @return Term
	 */
	@Override
	public Term getColumn(int i) {
		Preconditions.checkArgument(0 <= i && i < this.columns.size()) ;
		return this.columns.get(i);
	}

	/**
	 * @return List<Term>
	 */
	@Override
	public List<Term> getColumns() {
		return this.columns;
	}

	/**
	 * @return List<Term>
	 */
	@Override
	public List<Term> getInputTerms() {
		return this.inputTerms;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.plan.relational.logical.LogicalOperator#deepCopy()
	 */
	@Override
	public DependentAccess deepCopy() throws RelationalOperatorException {
		return new DependentAccess(this.relation, this.accessMethod, this.outputTerms, this.staticInputs);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getSimpleName()).append('{');
		result.append(this.relation.getName()).append('[');
		result.append(Joiner.on(",").join(this.accessMethod.getInputs()));
		result.append(']').append(this.outputTerms).append('}');
		return result.toString();
	}

	/**
	 * @param o Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object o) {
		return super.equals(o)
				&& this.getClass().isInstance(o)
				&& this.relation.equals(((DependentAccess) o).relation)
				&& this.accessMethod.equals(((DependentAccess) o).accessMethod)
				&& this.columns.equals(((DependentAccess) o).columns);

	}

	/**
	 * @return int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.outputType, this.inputType, this.columns,
				this.relation, this.accessMethod, this.metadata);
	}

	/**
	 * @return boolean
	 */
	@Override
	public boolean isClosed() {
		return this.inputTerms.isEmpty();
	}

	/**
	 * @return boolean
	 */
	@Override
	public boolean isQuasiLeaf() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.plan.relational.logical.LogicalOperator#isLeftDeep()
	 */
	@Override
	public boolean isLeftDeep() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.plan.relational.logical.LogicalOperator#isRightDeep()
	 */
	@Override
	public boolean isRightDeep() {
		return true;
	}
}
