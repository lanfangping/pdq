// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.algebra;

import com.google.common.base.Preconditions;

import uk.ac.ox.cs.pdq.fol.TypedConstant;

public abstract class ConstantComparisonCondition extends SimpleCondition {

	private static final long serialVersionUID = 6311780941534105378L;
	
	/**  The value to which the tuple must equal at the given position. */
	protected final TypedConstant constant;

	protected ConstantComparisonCondition(int position, TypedConstant constant) {
		super(position);
		Preconditions.checkNotNull(constant);
		this.constant = constant;
	}

	public TypedConstant getConstant() {
		return this.constant;
	}

	@Override
	public String toString() {
		String position = this.position.toString();
		StringBuilder result = new StringBuilder();
		result.append('#').append(position).append('=');
		result.append(this.constant);
		return result.toString();
	}

}
