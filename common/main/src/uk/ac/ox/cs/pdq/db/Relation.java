package uk.ac.ox.cs.pdq.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.ox.cs.pdq.db.AccessMethod.Types;
import uk.ac.ox.cs.pdq.db.metadata.RelationMetadata;
import uk.ac.ox.cs.pdq.fol.Predicate;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.fol.Variable;
import uk.ac.ox.cs.pdq.util.TupleType;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

// TODO: Auto-generated Javadoc
/**
 * A schema relation.
 *
 * @author Efthymia Tsamoura
 * @author Julien Leblay
 */
public abstract class Relation extends Predicate implements Serializable {

	/** The log. */
	protected static Logger log = Logger.getLogger(Relation.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9222721018270749836L;

	/** The global id. */
	protected static int globalId = 0;

	/**
	 * The relation's unique identifier.
	 */
	protected final int relationId;

	/**
	 * The Enum PropertyKeys.
	 */
	public static enum PropertyKeys { 
 /** The metadata. */
 METADATA }

	/**  The relation's properties. */
	protected final Properties properties = new Properties();

	/**  The relation's attributes. */
	protected final List<Attribute> attributes;

	/**
	 * The map attributes names to the positions. This map is initialized lazily
	 * to minimise memory overhead
	 */
	protected final Map<String, Integer> attributePositions;

	/**
	 * The relation's access methods, i.e. the positions of the inputAttributes
	 * which require input values.
	 */
	protected Map<String, AccessMethod> accessMethods;
	
	/** The am view. */
	protected List<AccessMethod> amView;

	/**
	 * The relation's foreign keys.
	 */
	protected List<ForeignKey> foreignKeys;
	
	/** The key. */
	protected List<Attribute> key = Lists.newArrayList();
	
	/** The key positions. */
	protected List<Integer> keyPositions = null;


	/**
	 * Constructor with input method.
	 *
	 * @param name Relation's name
	 * @param attributes List<? extends Attribute>
	 * @param accessMethods Relation's binding methods
	 * @param foreignKeys List<ForeignKey>
	 */
	public Relation(String name, List<? extends Attribute> attributes,
			List<AccessMethod> accessMethods, List<ForeignKey> foreignKeys) {
		this(name, attributes, accessMethods, foreignKeys, false);
	}

	/**
	 * Constructor with input method.
	 *
	 * @param name Relation's name
	 * @param attributes List<? extends Attribute>
	 * @param accessMethods Relation's binding methods
	 * @param foreignKeys List<ForeignKey>
	 * @param isEquality the is equality
	 */
	public Relation(String name, List<? extends Attribute> attributes,
			List<AccessMethod> accessMethods, List<ForeignKey> foreignKeys,
			boolean isEquality) {
		super(name, attributes.size(), isEquality);
		this.relationId = Relation.globalId++;
		this.attributes = ImmutableList.copyOf(attributes);
		Map<String, Integer> positions = new LinkedHashMap<>();
		int i = 0;
		for (Attribute a : this.attributes) {
			positions.put(a.getName(), i++);
		}
		this.attributePositions = ImmutableMap.copyOf(positions);
		this.accessMethods = new LinkedHashMap<>();
		this.amView = new ArrayList<>();
		this.setAccessMethods(accessMethods);
		this.foreignKeys = new ArrayList<>(foreignKeys);
		this.rep = null;
	}

	/**
	 * Constructor with input method.
	 *
	 * @param name Relation's name
	 * @param attributes List<? extends Attribute>
	 * @param accessMethods Relation's binding methods
	 */
	public Relation(String name, List<? extends Attribute> attributes, List<AccessMethod> accessMethods) {
		this(name, attributes, accessMethods, false);
	}

	/**
	 * Constructor with input method.
	 *
	 * @param name Relation's name
	 * @param attributes List<? extends Attribute>
	 * @param accessMethods Relation's binding methods
	 * @param isEquality the is equality
	 */
	public Relation(String name, List<? extends Attribute> attributes,
			List<AccessMethod> accessMethods, boolean isEquality) {
		this(name, attributes, accessMethods, new ArrayList<ForeignKey>(), isEquality);
	}

	/**
	 * Constructor without input method.
	 *
	 * @param name Relation's name
	 * @param attributes List<? extends Attribute>
	 * @param isEquality the is equality
	 */
	public Relation(String name, List<? extends Attribute> attributes, boolean isEquality) {
		this(name, attributes, new ArrayList<AccessMethod>(), isEquality);
	}

	/**
	 * Constructor without input method.
	 *
	 * @param name Relation's name
	 * @param attributes List<? extends Attribute>
	 */
	public Relation(String name, List<? extends Attribute> attributes) {
		this(name, attributes, new ArrayList<AccessMethod>(), false);
	}

	/**
	 * Copy constructor.
	 *
	 * @param r Relation
	 */
	public Relation(Relation r) {
		this(r.getName(), r.getAttributes(), r.getAccessMethods(),
				r.getForeignKeys(), r.isEquality());
	}

	/**
	 * Gets the name.
	 *
	 * @return String
	 */
	@Override
	public String getName() {
		return this.name;

	}

	/**
	 * Gets the arity.
	 *
	 * @return the arity of the relation.
	 */
	@Override
	public int getArity() {
		return this.attributes.size();
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the relation's inputAttributes
	 */
	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	/**
	 * Gets the type.
	 *
	 * @return the relation's type
	 */
	public TupleType getType() {
		return TupleType.DefaultFactory.createFromTyped(getAttributes());
	}
	
	/**
	 * Gets the input attributes.
	 *
	 * @param b the b
	 * @return 		the relation's input attributes for input binding
	 */
	public List<Attribute> getInputAttributes(AccessMethod b) {
		Preconditions.checkArgument(this.accessMethods.containsKey(b.getName()));
		List<Attribute> result = new ArrayList<>(b.getInputs().size());
		for (Integer i: b.getInputs()) {
			result.add(this.getAttribute(i - 1));
		}
		return result;
	}

	/**
	 * Gets the attribute.
	 *
	 * @param index int
	 * @return the relation's attribute at position index
	 */
	public Attribute getAttribute(int index) {
		return this.attributes.get(index);
	}

	/**
	 * Gets the attribute index.
	 *
	 * @param attributeName the attribute name
	 * @return the index of the attribute whose name is given as parameter,
	 *         returns -1 iff the relation has no such attribute
	 */
	public int getAttributeIndex(String attributeName) {
		return this.attributePositions.get(attributeName);
	}

	/**
	 * Gets the attribute.
	 *
	 * @param attributeName the attribute name
	 * @return the index of the attribute whose name is given as parameter,
	 *         returns -1 iff the relation has no such attribute
	 */
	public Attribute getAttribute(String attributeName) {
		Integer position = this.attributePositions.get(attributeName);
		if (position != null && position >= 0) {
			return this.attributes.get(position);
		}
		return null;
	}

	/**
	 * Gets the access methods.
	 *
	 * @return the relation's accessMethods.
	 */
	public List<AccessMethod> getAccessMethods() {
		return this.amView;
	}

	/**
	 * Adds the foreign key.
	 *
	 * @param foreingKey ForeignKey
	 */
	public void addForeignKey(ForeignKey foreingKey) {
		this.foreignKeys.add(foreingKey);
		this.rep = null;
	}

	/**
	 * Adds the foreign keys.
	 *
	 * @param foreingKeys List<ForeignKey>
	 */
	public void addForeignKeys(List<ForeignKey> foreingKeys) {
		this.foreignKeys.addAll(foreingKeys);
		this.rep = null;
	}

	/**
	 * Gets the foreign key.
	 *
	 * @param index int
	 * @return ForeignKey
	 */
	public ForeignKey getForeignKey(int index) {
		return this.foreignKeys.get(index);
	}

	/**
	 * Gets the foreign keys.
	 *
	 * @return List<ForeignKey>
	 */
	public List<ForeignKey> getForeignKeys() {
		return this.foreignKeys;
	}

	/**
	 * Gets the id.
	 *
	 * @return int
	 */
	public int getId() {
		return this.relationId;
	}

	/**
	 * Adds the access method.
	 *
	 * @param bm AccessMethod
	 */
	public void addAccessMethod(AccessMethod bm) {
		for (int b : bm.getInputs()) {
			if (!(1 <= b && b <= this.attributes.size())) {
				throw new IllegalArgumentException(
						"Attempting to instantiation a relation with inconsistent binding method.");
			}
		}
		if (this.accessMethods.put(bm.getName(), bm) == null) {
			this.amView.add(bm);
		}
		this.rep = null;
	}

	/**
	 * Sets the access methods.
	 *
	 * @param bindingMethods List<AccessMethod>
	 * @param clearFirst boolean
	 */
	public void setAccessMethods(List<AccessMethod> bindingMethods, boolean clearFirst) {
		if (clearFirst) {
			this.accessMethods.clear();
			this.amView.clear();
			this.rep = null;
		}
		if (bindingMethods != null) {
			for (AccessMethod bm : bindingMethods) {
				this.addAccessMethod(bm);
			}
		}
	}

	/**
	 * Sets the access methods.
	 *
	 * @param bindingMethods List<AccessMethod>
	 */
	public void setAccessMethods(List<AccessMethod> bindingMethods) {
		this.setAccessMethods(bindingMethods, false);
	}

	/**
	 * Gets the access method.
	 *
	 * @param name String
	 * @return AccessMethod
	 */
	public AccessMethod getAccessMethod(String name) {
		return this.accessMethods.get(name);
	}

	/**
	 * Creates the atoms.
	 *
	 * @return an atom corresponding to this relation.
	 */
	public DatabasePredicate createAtoms() {
		List<Term> variableTerms = new ArrayList<>();
		for (Attribute attribute : this.attributes) {
			variableTerms.add(new Variable(attribute.getName()));
		}
		return new DatabasePredicate(this, variableTerms);
	}

	/**
	 * Checks for free access.
	 *
	 * @return Boolean
	 */
	public Boolean hasFreeAccess() {
		if (this.accessMethods != null) {
			for (AccessMethod binding: this.accessMethods.values()) {
				if (binding.getType() == Types.FREE) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(List<Attribute> key) {
		this.key = key;
	}
	
	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public List<Attribute> getKey() {
		return this.key;
	}
	
	/**
	 * Gets the key positions.
	 *
	 * @return the key positions
	 */
	public List<Integer> getKeyPositions() {
		if(this.keyPositions == null) {
			this.keyPositions = Lists.newArrayList();
			for(Attribute key:this.key) {
				this.keyPositions.add(this.attributes.indexOf(key));
			}
		}
		return this.keyPositions;
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
		return Relation.class.isInstance(o)
				&& this.name.equals(((Relation) o).name)
				&& this.attributes.equals(((Relation) o).attributes)
				&& this.amView.equals(((Relation) o).amView);
	}

	/**
	 * Hash code.
	 *
	 * @return int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.attributes, this.amView);
	}

	/**
	 * To string.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		if (this.rep == null) {
			StringBuilder result = new StringBuilder();
			result.append(this.name).append('(');
			result.append(Joiner.on(",").join(this.attributes)).append(')');

			if (this.accessMethods != null && !this.accessMethods.isEmpty()) {
				char sep = '{';
				if (this.accessMethods != null && !this.accessMethods.isEmpty()) {
					for (AccessMethod c : this.accessMethods.values()) {
						result.append(sep).append(c);
						RelationMetadata md = this.getMetadata();
						if (md != null) {
							result.append('/').append(md.getPerInputTupleCost(c));
						}
						sep = ',';
					}
					result.append('}');
				}
			}

			if (this.foreignKeys != null && !this.foreignKeys.isEmpty()) {
				char sep = '{';
				if (this.foreignKeys != null && !this.foreignKeys.isEmpty()) {
					for (ForeignKey c : this.foreignKeys) {
						result.append(sep).append(c);
						sep = ',';
					}
					result.append('}');
				}
			}
			this.rep = result.toString();
		}
		return this.rep;
	}

	/**
	 * Returns properties associated with this relation, these may be SQL
	 * connection parameters, web service settings, etc. depending on the
	 * underlying implementation.
	 * If no properties are defined, this return an empty Properties instance
	 * (not null).
	 * @return the properties associated with this relation.
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * Gets the metadata.
	 *
	 * @return RelationMetadata
	 */
	public RelationMetadata getMetadata() {
		return (RelationMetadata) this.properties.get(PropertyKeys.METADATA);
	}

	/**
	 * Sets the metadata.
	 *
	 * @param metadata RelationMetadata
	 */
	public void setMetadata(RelationMetadata metadata) {
		this.properties.put(PropertyKeys.METADATA, metadata);
	}
}
