package uk.ac.ox.cs.pdq.planner.dag.explorer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import uk.ac.ox.cs.pdq.algebra.RelationalOperator;
import uk.ac.ox.cs.pdq.cost.estimators.CostEstimator;
import uk.ac.ox.cs.pdq.db.DatabaseConnection;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.fol.ConjunctiveQuery;
import uk.ac.ox.cs.pdq.plan.DAGPlan;
import uk.ac.ox.cs.pdq.plan.PlanUtils;
import uk.ac.ox.cs.pdq.planner.Explorer;
import uk.ac.ox.cs.pdq.planner.PlannerException;
import uk.ac.ox.cs.pdq.planner.PlannerParameters;
import uk.ac.ox.cs.pdq.planner.accessibleschema.AccessibilityAxiom;
import uk.ac.ox.cs.pdq.planner.accessibleschema.AccessibleSchema;
import uk.ac.ox.cs.pdq.planner.dag.ApplyRule;
import uk.ac.ox.cs.pdq.planner.dag.DAGChaseConfiguration;
import uk.ac.ox.cs.pdq.planner.reasoning.chase.accessiblestate.AccessibleChaseState;
import uk.ac.ox.cs.pdq.planner.reasoning.chase.accessiblestate.AccessibleDatabaseListState;
import uk.ac.ox.cs.pdq.reasoning.ReasoningParameters;
import uk.ac.ox.cs.pdq.reasoning.chase.Chaser;
import uk.ac.ox.cs.pdq.reasoning.chase.state.ChaseInstance;
import uk.ac.ox.cs.pdq.reasoning.chase.state.DatabaseChaseInstance;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

// TODO: Auto-generated Javadoc
/**
 * Explores the space of DAG proofs.
 * Exploration proceeds roughly as follows.
 * First, create all unary configurations. Unary configuration correspond to single access plans.
 * Then in every exploration step, create a new binary configuration by combining two other configurations. 
 * Saturate the new configuration using the constraints of the accessible schema. 
 * Finally, check if the newly configuration matches the accessible query and update the best configuration appropriately.   
 * @author Efthymia Tsamoura
 *
 */
public abstract class DAGExplorer extends Explorer<DAGPlan> {

	/**  The input user query *. */
	protected final ConjunctiveQuery query;
	
	/**  The accessible counterpart of the user query *. */
	protected final ConjunctiveQuery accessibleQuery;

	/**  The input schema *. */
	protected final Schema schema;
	
	/**  The accessible counterpart of the input schema *. */
	protected final AccessibleSchema accessibleSchema;

	/**  Runs the chase algorithm *. */
	protected final Chaser chaser;

	/**  Detects homomorphisms during chasing*. */
	DatabaseConnection connection;

	/**  Estimates the cost of a plan *. */
	protected final CostEstimator<DAGPlan> costEstimator;

	/**  The minimum cost configuration. */
	protected DAGChaseConfiguration bestConfiguration = null;

	/** The parameters. */
	protected final PlannerParameters parameters;

	private ReasoningParameters reasoningParams; 

	/**
	 * Instantiates a new DAG explorer.
	 *
	 * @param eventBus the event bus
	 * @param collectStats the collect stats
	 * @param parameters the parameters
	 * @param query 		The input user query
	 * @param accessibleQuery 		The accessible counterpart of the user query
	 * @param schema 		The input schema
	 * @param accessibleSchema 		The accessible counterpart of the input schema
	 * @param chaser 		Saturates configurations using the chase algorithm
	 * @param detector 		Detects homomorphisms during chasing
	 * @param costEstimator 		Estimates the cost of a plan
	 * @param reasoningParameters 
	 */
	public DAGExplorer(EventBus eventBus, 
			boolean collectStats, 
			PlannerParameters parameters,
			ReasoningParameters reasoningParameters,
			ConjunctiveQuery query, 
			ConjunctiveQuery accessibleQuery,
			Schema schema,
			AccessibleSchema accessibleSchema, 
			Chaser chaser, 
			DatabaseConnection dbConn,
			CostEstimator<DAGPlan> costEstimator) {
		super(eventBus, collectStats);
		Preconditions.checkArgument(parameters != null);
		Preconditions.checkArgument(query != null);
		Preconditions.checkArgument(accessibleQuery != null);
		Preconditions.checkArgument(schema != null);
		Preconditions.checkArgument(accessibleSchema != null);
		Preconditions.checkArgument(chaser != null);
		Preconditions.checkArgument(dbConn != null);
		Preconditions.checkArgument(costEstimator != null);
		
		this.parameters = parameters;
		this.query = query;
		this.accessibleQuery = accessibleQuery;
		this.schema = schema;
		this.accessibleSchema = accessibleSchema;
		this.chaser = chaser;
		this.connection = dbConn;
		this.costEstimator = costEstimator;
		this.reasoningParams = reasoningParameters;
	}

	/**
	 * Updates the minimum cost configuration/plan.
	 *
	 * @param configuration the configuration
	 * @return true if the best configuration/plan is updated
	 */
	public boolean setBestPlan(DAGChaseConfiguration configuration) {
		if(this.bestConfiguration != null && configuration != null &&
				this.bestConfiguration.getPlan().getCost().lessOrEquals(configuration.getPlan().getCost())) {
			return false;
		}
		this.bestConfiguration = configuration;
		//Add the final projection to the best plan
		RelationalOperator project = PlanUtils.createFinalProjection(
				this.accessibleQuery,
				this.bestConfiguration.getPlan().getOperator());
		this.bestPlan = new DAGPlan(project);
		this.bestPlan.addChild(this.bestConfiguration.getPlan());
		this.bestPlan.setCost(this.bestConfiguration.getPlan().getCost());
		this.eventBus.post(this);
		this.eventBus.post(this.getBestPlan());
		log.trace("\t+ BEST CONFIGURATION	" + configuration + "\t" + configuration.getPlan().getCost());
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.planner.Explorer#getBestPlan()
	 */
	@Override
	public DAGPlan getBestPlan() {
		if (this.bestConfiguration == null) {
			return null;
		}
		return this.bestPlan;
	}

	/**
	 * Gets the best configuration.
	 *
	 * @return the best configuration
	 */
	public DAGChaseConfiguration getBestConfiguration() {
		return this.bestConfiguration;
	}

	/**
	 * Terminates.
	 *
	 * @return true if the planner terminates
	 */
	@Override
	protected boolean terminates() {
		return false;
	}

	/**
	 * Creates the initial configurations.
	 *
	 * @return a list of ApplyRule configurations based on the facts derived after chasing the input schema with the canonical database of the query
	 * @throws PlannerException the planner exception
	 * @throws SQLException 
	 */
	protected List<DAGChaseConfiguration> createInitialConfigurations() throws PlannerException, SQLException {
		AccessibleDatabaseListState state = null;
		state = new AccessibleDatabaseListState(this.reasoningParams, this.query, this.schema, this.connection, false);
		this.chaser.reasonUntilTermination(state, this.schema.getDependencies());

		List<DAGChaseConfiguration> collection = new ArrayList<>();
		Collection<Pair<AccessibilityAxiom,Collection<Atom>>> pairs = state.groupByBinding(this.accessibleSchema.getAccessibilityAxioms());
		for (Pair<AccessibilityAxiom, Collection<Atom>> pair: pairs) {
			ApplyRule applyRule = null;
			Collection<Collection<Atom>> bindings = new LinkedHashSet<>();
			switch (this.parameters.getFollowUpHandling()) {
			case MINIMAL:
				for (Atom p: pair.getRight()) {
					bindings.add(Sets.newHashSet(p));
				}
				break;
			default:
				bindings.add(pair.getRight());
				break;
			}
			for (Collection<Atom> binding:bindings) {
				AccessibleChaseState newState = (uk.ac.ox.cs.pdq.planner.reasoning.chase.accessiblestate.AccessibleChaseState) 
						new AccessibleDatabaseListState(this.reasoningParams, binding, this.connection, false);
				applyRule = new ApplyRule(
						newState,
						pair.getLeft(),
						Sets.newHashSet(binding)
						);
				applyRule.generate(this.chaser, this.query, this.accessibleSchema);
				collection.add(applyRule);
			}
		}
		return collection;
	}

}
