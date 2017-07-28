package uk.ac.ox.cs.pdq.algebra;

import org.junit.Assert;

import uk.ac.ox.cs.pdq.InterningManager;
import uk.ac.ox.cs.pdq.db.TypedConstant;


public class ConstantEqualityCondition implements SimpleCondition {
	private static final long serialVersionUID = 1040523371703814834L;

	/**  The position of the tuple, whose value we will compare. */
	protected final Integer position;

	/**  The value to which the tuple must equal at the given position. */
	protected final TypedConstant constant;

	private ConstantEqualityCondition(int position, TypedConstant constant) {
		Assert.assertTrue(position >= 0 && constant != null);
		this.position = position;
		this.constant = constant;
	}

	public int getPosition() {
		return this.position;
	}

	public TypedConstant getConstant() {
		return this.constant;
	}
	
    protected static final InterningManager<ConstantEqualityCondition> s_interningManager = new InterningManager<ConstantEqualityCondition>() {
        protected boolean equal(ConstantEqualityCondition object1, ConstantEqualityCondition object2) {
            if (object1.position != object2.position || !object1.constant.equals(object1.constant))  
                return false;
            return true;
        }

        protected int getHashCode(ConstantEqualityCondition object) {
            int hashCode = object.position.hashCode() + object.constant.hashCode() * 7;
            return hashCode;
        }
    };
    
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ConstantEqualityCondition create(int position, TypedConstant constant) {
        return s_interningManager.intern(new ConstantEqualityCondition(position, constant));
    }

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('#').append(this.position).append('=');
		result.append(this.constant);
		return result.toString();
	}
}