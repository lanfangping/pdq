package uk.ac.ox.cs.pdq.planner.linear.explorer.pruning;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Preconditions;

import uk.ac.ox.cs.pdq.algebra.RelationalTerm;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.planner.PlannerException;
import uk.ac.ox.cs.pdq.planner.accessibleschema.AccessibleSchema;
import uk.ac.ox.cs.pdq.planner.linear.explorer.Candidate;
import uk.ac.ox.cs.pdq.planner.linear.explorer.SearchNode;
import uk.ac.ox.cs.pdq.planner.util.PlanTree;
import uk.ac.ox.cs.pdq.util.LimitReachedException;

/**
 * Removes the redundant accesses and the redundant follow-up joins from a successful configuration path.
 *
 * @author Efthymia Tsamoura
 */
public abstract class PostPruning {

	/**  The accessible counterpart of the input schema *. */
	protected final AccessibleSchema accessibleSchema;
	
	/**  True if the input path is pruned. */
	protected Boolean isPruned = false;
	
	/**  The pruned path and its corresponding plan. */
	protected List<SearchNode> path = null;
	
	/** The plan. */
	protected RelationalTerm plan = null;

	/**
	 * Instantiates a new post pruning.
	 *
	 * @param nodeFactory 		Factory of tree nodes
	 * @param accessibleSchema 		The accessible counterpart of the input schema
	 */
	public PostPruning(AccessibleSchema accessibleSchema) {
		Preconditions.checkArgument(accessibleSchema != null);
		this.accessibleSchema = accessibleSchema;
	}

	
	/**
	 * Post-prunes the input nodes path.
	 *
	 * @param root 		The root of the linear path tree
	 * @param searchNodePath 		The path of nodes to be prostpruned 
	 * @param factsInQueryMatch 		The facts of the query match
	 * @return true, if successful
	 * @throws PlannerException the planner exception
	 * @throws LimitReachedException the limit reached exception
	 */
	public boolean pruneSearchNodePath(SearchNode root, List<SearchNode> searchNodePath, Atom[] factsInQueryMatch) throws PlannerException, LimitReachedException {
		Preconditions.checkArgument(searchNodePath != null);
		Preconditions.checkArgument(factsInQueryMatch != null);
		this.isPruned = false;
		this.path = null;
		this.plan = null;
		Collection<Atom> qF = new LinkedHashSet<>();
		for(Atom queryFact: factsInQueryMatch) {
			if (queryFact.getPredicate().getName().startsWith(AccessibleSchema.inferredAccessiblePrefix)) 
				qF.add(queryFact);
			else 
				Preconditions.checkState(queryFact.getPredicate().getName().equals(AccessibleSchema.accessibleRelation.getName()));
		}
		Collection<Atom> factsToExpose = this.findExposedFactsThatAreSufficientToLeadToQueryMatch(searchNodePath, qF);
		if(this.isPruned) 
			this.createPathOfSearchNodesThatExposeInputFacts(root, searchNodePath, factsToExpose);
		return this.isPruned;
	}

	/**
	 * Gets the utilised candidates.
	 *
	 * @param candidates the candidates
	 * @param minimalFacts the minimal facts
	 * @return the candidates that produced the input facts
	 */
	protected static Set<Candidate> getCandidatesThatExposeInputFacts(Collection<Candidate> candidates, Collection<Atom> minimalFacts) {
		Set<Candidate> useful = new HashSet<>();
		for(Candidate candidate: candidates) {
			Atom inferredAccessibleFact = candidate.getInferredAccessibleFact();
			if (minimalFacts.contains(inferredAccessibleFact)) {
				useful.add(candidate);
			}
		}
		return useful;
	}

	
	/**
	 * Adds the pruned path to tree.
	 *
	 * @param planTree 		The input tree of paths
	 * @param parentNode 		The node below which we will add the input path
	 * @param newPath 		The path to the add to the input tree
	 */
	public void addPrunedPathToTree(PlanTree<SearchNode> planTree, SearchNode parentNode, List<SearchNode> newPath) {
		Preconditions.checkArgument(newPath != null);
		Preconditions.checkArgument(!newPath.isEmpty());
		
		for(int j = 0; j < newPath.size(); ++j) {
			try {
				planTree.addVertex(newPath.get(j));
				if(j == 0) {
					planTree.addEdge(parentNode, newPath.get(j), new DefaultEdge());
				}
				else {
					planTree.addEdge(newPath.get(j-1), newPath.get(j), new DefaultEdge());
				}
			} catch(Exception ex) {
				throw new IllegalStateException(ex);
			}
		}
	}
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public List<SearchNode> getPath() {
		return this.path;
	}
	
	/**
	 * Gets the plan.
	 *
	 * @return the plan
	 */
	public RelationalTerm getPlan() {
		return this.plan;
	}

	/**
	 * Find facts to expose.
	 *
	 * @param searchNodePath 		A successful path 
	 * @param factsInQueryMatch 		The facts in the query match 
	 * @return 		the facts that are sufficient to produce the input queryFacts
	 */
	protected abstract Collection<Atom> findExposedFactsThatAreSufficientToLeadToQueryMatch(List<SearchNode> searchNodePath, Collection<Atom> factsInQueryMatch);
	
	/**
	 * Creates a post-pruned query path .
	 *
	 * @param root 		The root of the plan tree
	 * @param searchNodePath 		The path that will be post-pruned 
	 * @param factsToExpose 		The facts that we will expose 
	 * @throws PlannerException the planner exception
	 * @throws LimitReachedException the limit reached exception
	 */
	protected abstract void createPathOfSearchNodesThatExposeInputFacts(SearchNode root, List<SearchNode> searchNodePath, Collection<Atom> factsToExpose) throws PlannerException, LimitReachedException;
}
