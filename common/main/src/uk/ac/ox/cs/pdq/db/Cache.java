package uk.ac.ox.cs.pdq.db;

import uk.ac.ox.cs.pdq.InterningManager;
import uk.ac.ox.cs.pdq.fol.Constant;
import uk.ac.ox.cs.pdq.fol.Variable;

/**
 * Creates and maintains a cache of each object type in this package. The
 * purpose of this class is it make sure objects are never duplicated.
 * 
 * @author Gabor
 *
 */
public class Cache {
	protected static InterningManager<AccessMethod> accessMethod = null;
	protected static InterningManager<Attribute> attribute = null;
	protected static InterningManager<Match> match = null;
	protected static InterningManager<PrimaryKey> primaryKey = null;
	protected static InterningManager<TypedConstant> typedConstant = null;
	protected static InterningManager<Relation> relation = null;

	static {
		startCaches();
	}

	/**
	 * Needed in case we want to work with multiple schemas. Most commonly in case
	 * of unit testing.
	 */
	public static void reStartCaches() {
		accessMethod = null;
		attribute = null;
		match = null;
		primaryKey = null;
		typedConstant = null;
		relation = null;
		
		startCaches();
	}

	private static void startCaches() {
		accessMethod = new InterningManager<AccessMethod>() {
			protected boolean equal(AccessMethod object1, AccessMethod object2) {
				if (!object1.name.equals(object2.name) || object1.inputs.length != object2.inputs.length)
					return false;
				for (int index = object1.inputs.length - 1; index >= 0; --index)
					if (!object1.inputs[index].equals(object2.inputs[index]))
						return false;
				return true;
			}

			protected int getHashCode(AccessMethod object) {
				int hashCode = object.name.hashCode();
				for (int index = object.inputs.length - 1; index >= 0; --index)
					hashCode = hashCode * 7 + object.inputs[index].hashCode();
				return hashCode;
			}
		};
		
		attribute  = new InterningManager<Attribute>() {
			protected boolean equal(Attribute object1, Attribute object2) {
				if (!object1.name.equals(object2.name) || object1.type != object2.type)
					return false;
				return true;
			}

			protected int getHashCode(Attribute object) {
				int hashCode = object.name.hashCode() + object.type.hashCode() * 7;
				return hashCode;
			}
		};
		
	    match  = new InterningManager<Match>() {
	        protected boolean equal(Match object1, Match object2) {
	            if (!object1.formula.equals(object2.formula) || 
	            		object1.mapping.size() != object2.mapping.size())
	                return false;
	            for(java.util.Map.Entry<Variable, Constant> entry:object1.mapping.entrySet()) {
	            	if(!object2.mapping.containsKey(entry.getKey()) || object2.mapping.get(entry.getKey()).equals(entry.getValue())) 
	            		return false;
	            }
	            return true;
	        }

	        protected int getHashCode(Match object) {
	            int hashCode = object.formula.hashCode();
	            for(java.util.Map.Entry<Variable, Constant> entry:object.mapping.entrySet()) 
	                hashCode = hashCode * 8 + entry.getKey().hashCode() * 9 + entry.getValue().hashCode() * 10;
	            return hashCode;
	        }
	    };

		primaryKey  = new InterningManager<PrimaryKey>() {
			protected boolean equal(PrimaryKey object1, PrimaryKey object2) {
				if (object1.attributes.length != object2.attributes.length)
					return false;
				for (int index = object1.attributes.length - 1; index >= 0; --index)
					if (!object1.attributes[index].equals(object2.attributes[index]))
						return false;
				return true;
			}

			protected int getHashCode(PrimaryKey object) {
				int hashCode = 0;
				for (int index = object.attributes.length - 1; index >= 0; --index)
					hashCode = hashCode * 7 + object.attributes[index].hashCode();
				return hashCode;
			}
		};
		
	    typedConstant  = new InterningManager<TypedConstant>() {
	        protected boolean equal(TypedConstant object1, TypedConstant object2) {
	            return object1.value.equals(object2.value);
	        }

	        protected int getHashCode(TypedConstant object) {
	            return object.value.hashCode() * 7;
	        }
	    };
		relation  = new InterningManager<Relation>() {
	        protected boolean equal(Relation object1, Relation object2) {
	            if (!object1.getName().equals(object2.getName()) || object1.attributes.length != object2.attributes.length || 
	            		object1.accessMethods.length != object2.accessMethods.length)
	                return false;
	            for (int index = object1.attributes.length - 1; index >= 0; --index)
	                if (!object1.attributes[index].equals(object2.attributes[index]))
	                    return false;
	            for (int index = object1.accessMethods.length - 1; index >= 0; --index)
	                if (!object1.accessMethods[index].equals(object2.accessMethods[index]))
	                    return false;
	            return true;
	        }
	
	        protected int getHashCode(Relation object) {
	            int hashCode = object.getName().hashCode();
	            for (int index = object.attributes.length - 1; index >= 0; --index)
	                hashCode = hashCode * 7 + object.attributes[index].hashCode();
	            for (int index = object.accessMethods.length - 1; index >= 0; --index)
	                hashCode = hashCode * 7 + object.accessMethods[index].hashCode();
	            return hashCode;
	        }
		};
	    
	}
}