package uk.ac.ox.cs.pdq.plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import uk.ac.ox.cs.pdq.algebra.predicates.AttributeEqualityPredicate;
import uk.ac.ox.cs.pdq.algebra.predicates.ConjunctivePredicate;
import uk.ac.ox.cs.pdq.algebra.predicates.Predicate;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.TGD;
import uk.ac.ox.cs.pdq.util.Table;
import uk.ac.ox.cs.pdq.util.Typed;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Efthymia Tsamoura
 *
 */
public class JoinCommand implements Command{

	/** The left input table **/
	private final Table left;
	
	/** The right input table **/
	private final Table right;
	
	/** The output table **/
	private final Table output;
	
	/** The join predicates**/
	private final Predicate predicates;
	
	/** Caches the constraint that captures this access command **/
	private final TGD command;
	
	public JoinCommand(Table left, Table right) {
		this(left, right, initNaturalJoin(left, right));
	}
	
	/**
	 * Creates a join command with the given input tables and input join predicates
	 * @param left
	 * @param right
	 * @param predicates
	 */
	public JoinCommand(Table left, Table right, Predicate predicates) {
		Preconditions.checkNotNull(left);
		Preconditions.checkNotNull(right);
		Preconditions.checkNotNull(predicates);
		this.left = left;
		this.right = right;
		List<Attribute> attributes = Lists.newArrayList();
		attributes.addAll((List<Attribute>)this.left.getHeader());
		attributes.addAll((List<Attribute>)this.right.getHeader());
		this.output = new Table(attributes);
		this.predicates = predicates;
		this.command = new CommandToTGDTranslator().toTGD(this);
	}
	
	/**
	 * Finds the equijoin predicates based on the attribute names of the input tables
	 * @param ltable
	 * @param rtable
	 * @return
	 */
	private static Predicate initNaturalJoin(Table ltable, Table rtable) {
		Multimap<Typed, Integer> joinVariables = LinkedHashMultimap.create();
		int totalCol = 0;
		// Cluster patterns by variables
		Set<Typed> inChild = Sets.newLinkedHashSet();
		for (Table child : Lists.newArrayList(ltable,rtable)) {
			inChild.clear();
			for (int i = 0, l = child.getHeader().size(); i < l; i++) {
				Typed col = child.getHeader().get(i);
				if (!inChild.contains(col)) {
					joinVariables.put(col, totalCol);
					inChild.add(col);
				}
				totalCol++;
			}
		}
		Collection<AttributeEqualityPredicate> equalities = new ArrayList<>();
		// Remove clusters containing only one pattern
		for (Iterator<Typed> keys = joinVariables.keySet().iterator(); keys.hasNext();) {
			Collection<Integer> cluster = joinVariables.get(keys.next());
			if (cluster.size() < 2) {
				keys.remove();
			} else {
				Iterator<Integer> i = cluster.iterator();
				Integer left = i.next();
				while (i.hasNext()) {
					Integer right = i.next();
					equalities.add(new AttributeEqualityPredicate(left, right));
				}
			}
		}
		return new ConjunctivePredicate<>(equalities);
	}

	@Override
	public Table getOutput() {
		return this.output;
	}
	
	public Table getLeft() {
		return this.left;
	}
	
	public Table getRight() {
		return this.right;
	}

	public Predicate getPredicates() {
		return predicates;
	}
	
	/**
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
		return JoinCommand.class.isInstance(o)
				&& this.left.equals(((JoinCommand) o).left)
				&& this.right.equals(((JoinCommand) o).right)
				&& this.predicates.equals(((JoinCommand) o).predicates);
	}

	/**
	 * @return int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.left, this.right, this.predicates);
	}

	/**
	 * @return String
	 */
	@Override
	public String toString() {
		return this.command.toString();
	}
}