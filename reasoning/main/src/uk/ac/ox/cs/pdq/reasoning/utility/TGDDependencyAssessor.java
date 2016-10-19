package uk.ac.ox.cs.pdq.reasoning.utility;

import java.util.Collection;

import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.reasoning.chase.state.ChaseInstance;

// TODO: Auto-generated Javadoc
/**
	Finds for each chase round which dependencies
	are most likely to be fired and returns those dependencies.
 *    
 * @author Efthymia Tsamoura
 *
 */

public interface TGDDependencyAssessor {

	/**
	 * Gets the dependencies.
	 *
	 * @param state 		A collection of chase facts
	 * @return 		the dependencies that are most likely to be fired in the next chase round.
	 */
	Collection<? extends Dependency> getDependencies(ChaseInstance state);
	
}
