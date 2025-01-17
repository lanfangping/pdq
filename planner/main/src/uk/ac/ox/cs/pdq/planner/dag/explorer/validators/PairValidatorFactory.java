// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.planner.dag.explorer.validators;

import uk.ac.ox.cs.pdq.planner.PlannerParameters.ValidatorTypes;

/**
 * Creates validators (shape restrictions) based on the input arguments.
 * 
 * 	-The DefaultValidator requires the left and right configurations to be non-trivial:
	an ordered pair of configurations (left, right) is non-trivial if the output facts of the right configuration are not included in the output facts of left configuration and vice versa.
	-The ApplyRuleDepthValidator requires the input pair of configurations to be non trivial, their combined depth to be <= the depth threshold
	and at least one of the input configurations to be an ApplyRule.
	-The ApplyRuleValidator requires the input pair of configurations to be non trivial and at least one of the input configurations to be an ApplyRule.
	-The DepthValidator requires the input pair of configurations to be non trivial and their combined depth to be <= the depth threshold.
	-The LinearValidator requires the input pair of configurations to be non trivial and their composition to be a closed left-deep configuration
	-The RightDepthValidator requires the input pair of configurations to be non trivial and the right's depth to be <= the depth threshold
 *
 * @author Efthymia Tsamoura
 *
 */
public class PairValidatorFactory {

	/**  The type of the target validator object*. */
	private final ValidatorTypes type;

	/** The depth threshold. */
	private final int depthThreshold;

	/**
	 * Constructor for ValidatorFactory.
	 * @param type ValidatorTypes
	 */
	public PairValidatorFactory(ValidatorTypes type) {
		this.type = type;
		this.depthThreshold = 3;
	}

	/**
	 * Constructor for ValidatorFactory.
	 * @param type ValidatorTypes
	 * @param depthThreshold int
	 */
	public PairValidatorFactory(ValidatorTypes type, int depthThreshold) {
		this.type = type;
		this.depthThreshold = depthThreshold;
	}

	/**
	 * Gets the single instance of ValidatorFactory.
	 *
	 * @return Validator
	 */
	public PairValidator[] getInstance() {
		switch(this.type) {
		case DEFAULT_VALIDATOR:
			return new PairValidator[] {new DefaultPairValidator()};
		case APPLYRULE_VALIDATOR:
			return new PairValidator[] {new DefaultPairValidator(), new ApplyRulePairValidator()};
		case DEPTH_VALIDATOR:
			return new PairValidator[] {new DefaultPairValidator(), new DepthPairValidator(this.depthThreshold)};
		case RIGHT_DEPTH_VALIDATOR:
			return new PairValidator[] {new DefaultPairValidator(), new RightDepthPairValidator(this.depthThreshold)};
		case LINEAR_VALIDATOR:
			return new PairValidator[] {new DefaultPairValidator(), new LinearPairValidator()};
		default:
			throw new RuntimeException("Invalid validator type: " + this.type);
		}
	}
}
