// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.reasoning.chase;

import static org.mockito.ArgumentMatchers.intThat;

import java.awt.print.Printable;
import java.util.Iterator;
import java.util.List;

import javax.management.MBeanOperationInfo;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.checkerframework.checker.signature.qual.BinaryNameInUnnamedPackage;
import org.checkerframework.checker.units.qual.m;

import com.google.common.base.Preconditions;

import uk.ac.ox.cs.pdq.db.Match;
import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.reasoning.chase.dependencyAssessor.DependencyAssessor;
import uk.ac.ox.cs.pdq.reasoning.chase.dependencyAssessor.DependencyAssessor.EGDROUND;
import uk.ac.ox.cs.pdq.reasoning.chase.state.ChaseInstance;
import uk.ac.ox.cs.pdq.reasoning.chase.state.TriggerProperty;


/**
 * Parallel chasing here just refers to doing chase steps in bulk using SQL calls to find active triggers
 * rather than performing one chase step at a time
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
public class ParallelChaser extends Chaser {

	/**
	 * Chases the input state until termination.
	 *
	 * @param <S> the generic type
	 * @param instance the instance
	 * @param dependencies the dependencies
	 */
	@Override
	public <S extends ChaseInstance> void reasonUntilTermination(S instance,  Dependency[] dependencies) {
		Preconditions.checkArgument(instance instanceof ChaseInstance);
		DependencyAssessor accessor = new DependencyAssessor(dependencies);
		boolean appliedStep = false;
		Dependency[] d = dependencies;
		do {
			appliedStep = false;
			for(Dependency dependency:d) {
				List<Match> matches = instance.getTriggers(new Dependency[]{dependency}, TriggerProperty.ACTIVE);
				
				// check matches
				Iterator<Match> matchIterable = matches.iterator();
				while (matchIterable.hasNext()) {
					Match m = matchIterable.next();
					System.out.println("substitution: "+m.toString() + "\n");
				}
				
				if(!matches.isEmpty()) {
					boolean success = instance.chaseStep(matches);
					if (success) {
						appliedStep = true;
					}
					accessor.addNewFacts(instance.getNewFacts());
				}
			}
			d = accessor.getDependencies(EGDROUND.BOTH);	
		} while (appliedStep);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.reasoning.chase.Chaser#clone()
	 */
	@Override
	public ParallelChaser clone() {
		return new ParallelChaser();
	}
}
