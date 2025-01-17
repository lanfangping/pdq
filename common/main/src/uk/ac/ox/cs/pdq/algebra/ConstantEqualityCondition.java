// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.algebra;

import com.google.common.base.Preconditions;

import uk.ac.ox.cs.pdq.db.tuple.Tuple;
import uk.ac.ox.cs.pdq.fol.TypedConstant;

/**
 * Represents a constant equality condition, that can be used to create -for example- a SelectionTerm.
 *  
 * @author Unknown
 * @author Gabor
 * @author Stefano
 */
public class ConstantEqualityCondition extends SimpleCondition {
	private static final long serialVersionUID = 1040523371703814834L;

	/**  The position of the tuple, whose value we will compare. */
	protected final Integer position;

	/**  The value to which the tuple must equal at the given position. */
	protected final TypedConstant constant;

	private ConstantEqualityCondition(int position, TypedConstant constant) {
		super(position);
		assert (position >= 0 && constant != null);
		this.position = position;
		this.constant = constant;
	}

	public int getPosition() {
		return this.position;
	}

	public TypedConstant getConstant() {
		return this.constant;
	}
	
    public static ConstantEqualityCondition create(int position, TypedConstant constant) {
        return Cache.constantEqualityCondition.retrieve(new ConstantEqualityCondition(position, constant));
    }

	@Override
	public String toString() {
		String position = this.position.toString();
		StringBuilder result = new StringBuilder();
		result.append('#').append(position).append('=');
		result.append(this.constant);
		return result.toString();
	}


	// Return true iff the tuple value at the 'position' index is equal to that
	// of the constant value in this condition.
	@Override
	public boolean isSatisfied(Tuple tuple) {
		Preconditions.checkArgument(tuple.size() > this.position);

		Object value = tuple.getValue(this.position);
		Object targetValue = this.getConstant().getValue();
		if (value == null)
			return targetValue == null;
		return this.compareValues(value, targetValue);
	}

}
