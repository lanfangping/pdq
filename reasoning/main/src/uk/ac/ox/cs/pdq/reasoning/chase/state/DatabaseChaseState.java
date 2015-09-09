package uk.ac.ox.cs.pdq.reasoning.chase.state;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.pdq.db.Constraint;
import uk.ac.ox.cs.pdq.fol.Conjunction;
import uk.ac.ox.cs.pdq.fol.Query;
import uk.ac.ox.cs.pdq.reasoning.Match;
import uk.ac.ox.cs.pdq.reasoning.homomorphism.DBHomomorphismManager;
import uk.ac.ox.cs.pdq.reasoning.homomorphism.HomomorphismConstraint;

import com.google.common.base.Preconditions;

/**
 *
 * Top-level class for database-backed chase states.
 * This implementation keeps the facts of this state in a database.
 *
 * @author Efthymia Tsamoura
 *
 */
public abstract class DatabaseChaseState implements ChaseState {

	/** Queries and updates the database of facts **/
	protected DBHomomorphismManager manager;

	/**
	 * Constructor for DatabaseChaseState.
	 * @param manager DBHomomorphismManager
	 */
	public DatabaseChaseState(
			DBHomomorphismManager manager) {
		Preconditions.checkNotNull(manager);
		this.manager = manager;
	}

	/**
	 * @return DBHomomorphismManager
	 */
	public DBHomomorphismManager getManager() {
		return this.manager;
	}

	/**
	 * @param manager DBHomomorphismManager
	 */
	public void setManager(DBHomomorphismManager manager) {
		this.manager = manager;
	}

	/**
	 * Calls the manager to detect homomorphisms of the input query to facts in this state.
	 * The manager detects homomorphisms using a database backend.
	 * @param query Query
	 * @return List<Match>
	 * @see uk.ac.ox.cs.pdq.chase.state.ChaseState#getMatches(Query)
	 */
	@Override
	public List<Match> getMatches(Query<?> query) {
		return this.manager.getMatches(
				query,
				HomomorphismConstraint.topK(1),
				HomomorphismConstraint.factScope(Conjunction.of(this.getFacts())),
				HomomorphismConstraint.satisfies(query.getFree2Canonical()));
	}
	
	/**
	 * Calls the manager to detect homomorphisms of the input query to facts in this state.
	 * The manager detects homomorphisms using a database backend.
	 * @param query Query
	 * @return List<Match>
	 * @see uk.ac.ox.cs.pdq.chase.state.ChaseState#getMatches(Query)
	 */
	@Override
	public List<Match> getMatches(Query<?> query, HomomorphismConstraint... constraints) {
		HomomorphismConstraint[] c = new HomomorphismConstraint[constraints.length+1];
		System.arraycopy(constraints, 0, c, 0, constraints.length);
		c[constraints.length] = HomomorphismConstraint.factScope(Conjunction.of(this.getFacts()));
		return this.manager.getMatches(query, c);
	}
	
	/**
	 * Calls the manager to detect homomorphisms of the input dependency to facts in this state.
	 * The manager detects homomorphisms using a database backend.
	 * @param dependency Constraint
	 * @param constraints HomomorphismConstraint...
	 * @return List<Match>
	 * @see uk.ac.ox.cs.pdq.reasoning.chase.state.ChaseState#getHomomorphisms(Constraint)
	 */
	@Override
	public List<Match> getMaches(Constraint dependency, HomomorphismConstraint... constraints) {
		HomomorphismConstraint[] c = new HomomorphismConstraint[constraints.length+1];
		System.arraycopy(constraints, 0, c, 0, constraints.length);
		c[constraints.length] = HomomorphismConstraint.factScope(Conjunction.of(this.getFacts()));
		return this.manager.getMatches(dependency, c);
	}

	/**
	 * Calls the manager to detect homomorphisms of the input dependencies to facts in this state.
	 * The manager detects homomorphisms using a database backend.
	 * @param dependencies Collection<D>
	 * @param constraints HomomorphismConstraint...
	 * @return Map<D,List<Match>>
	 * @see uk.ac.ox.cs.pdq.chase.state.ChaseState#getHomomorphisms(Collection<D>)
	 */
	@Override
	public List<Match> getMaches(Collection<? extends Constraint> dependencies, HomomorphismConstraint... constraints) {
		HomomorphismConstraint[] c = new HomomorphismConstraint[constraints.length+1];
		System.arraycopy(constraints, 0, c, 0, constraints.length);
		c[constraints.length] = HomomorphismConstraint.factScope(Conjunction.of(this.getFacts()));
		return this.manager.getMatches(dependencies, c);
	}

	/**
	 * @return DatabaseChaseState
	 * @see uk.ac.ox.cs.pdq.reasoning.chase.state.ChaseState#clone()
	 */
	@Override
	public abstract DatabaseChaseState clone();
}
