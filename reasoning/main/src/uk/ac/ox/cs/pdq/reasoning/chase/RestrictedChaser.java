package uk.ac.ox.cs.pdq.reasoning.chase;

import java.util.Collection;
import java.util.List;

import uk.ac.ox.cs.pdq.db.Match;
import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.logging.performance.StatisticsCollector;
import uk.ac.ox.cs.pdq.reasoning.chase.state.ChaseInstance;
import uk.ac.ox.cs.pdq.reasoning.chase.state.DatabaseChaseInstance.LimitTofacts;
import uk.ac.ox.cs.pdq.reasoning.chase.state.TriggerProperty;
import uk.ac.ox.cs.pdq.reasoning.utility.DefaultTGDDependencyAssessor;
import uk.ac.ox.cs.pdq.reasoning.utility.TGDDependencyAssessor;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Preconditions;


// TODO: Auto-generated Javadoc
/**
 * (From modern dependency theory notes)
 * Runs the chase algorithm applying only active triggers. 
 * Consider an instance I, a set Base of values, and a TGD
 * \delta = \forall x  \sigma(\vec{x}) --> \exists y  \tau(\vec{x}, \vec{y})
 * a trigger for \delta in I is a homomorphism h of \sigma into I. A trigger is active if it
 * does not extend to a homomorphism h0 into I. Informally, a trigger is a tuple \vec{c}
 * satisfying \sigma, and it is active if there is no witness \vec{y} that makes \tau holds.
 * A chase step appends to I additional facts that were produced during grounding \delta. 
 * The output of the chase step is a new instance in which h is no longer an active trigger.
 * 
 * The facts that are generated during chasing are stored in a list.
 *
 * @author Efthymia Tsamoura
 *
 */
public class RestrictedChaser extends Chaser {

	/**
	 * Constructor for RestrictedChaser.
	 *
	 * @param statistics the statistics
	 */
	public RestrictedChaser(StatisticsCollector statistics) {
		super(statistics);
	}


	/**
	 * Chases the input state until termination.
	 *
	 * @param <S> the generic type
	 * @param instance the instance
	 * @param dependencies the dependencies
	 */
	@Override
	public <S extends ChaseInstance> void reasonUntilTermination(S instance,  Collection<? extends Dependency> dependencies) {
		Preconditions.checkArgument(instance instanceof ChaseInstance);
		TGDDependencyAssessor accessor = new DefaultTGDDependencyAssessor(dependencies);
		boolean appliedStep = false;
		Collection<? extends Dependency> d = dependencies;
		do {
			appliedStep = false;
			for(Dependency dependency:d) {
				List<Match> matches = instance.getTriggers(Lists.newArrayList(dependency), TriggerProperty.ACTIVE, LimitTofacts.THIS);	
				if(!matches.isEmpty()) {
					appliedStep = true;
					instance.chaseStep(matches);
				}
			}
			d = accessor.getDependencies(instance);	
		} while (appliedStep);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.reasoning.chase.Chaser#clone()
	 */
	@Override
	public RestrictedChaser clone() {
		return new RestrictedChaser(this.statistics);
	}
}
