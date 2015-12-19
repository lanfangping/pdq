package uk.ac.ox.cs.pdq.planner.dag.explorer.validators;


import uk.ac.ox.cs.pdq.planner.dag.ConfigurationUtility;
import uk.ac.ox.cs.pdq.planner.dag.DAGAnnotatedPlan;
import uk.ac.ox.cs.pdq.planner.dag.DAGConfiguration;

/**
 * Requires the input pair of configurations to be non trivial and their combined depth to be <= the depth threshold
 * @author Efthymia Tsamoura
 *
 * @param 
 */
public class DepthValidator implements Validator{

	private final int depthThreshold;

	public DepthValidator() {
		this.depthThreshold = 3;
	}

	/**
	 * Constructor for DepthThrottlingValidator.
	 * @param depthThreshold int
	 */
	public DepthValidator(int depthThreshold) {
		this.depthThreshold = depthThreshold;
	}

	/**
	 * @param left DAGConfiguration
	 * @param right DAGConfiguration
	 * @return boolean
	 * @see uk.ac.ox.cs.pdq.dag.explorer.validators.Validator#validate(DAGConfiguration, DAGConfiguration)
	 */
	@Override
	public boolean validate(DAGAnnotatedPlan left, DAGAnnotatedPlan right) {
		return left.getHeight() <= this.depthThreshold && ConfigurationUtility.isNonTrivial(left, right);
	}

	/**
	 * @param left DAGConfiguration
	 * @param right DAGConfiguration
	 * @param depth int
	 * @return boolean
	 * @see uk.ac.ox.cs.pdq.dag.explorer.validators.Validator#validate(DAGConfiguration, DAGConfiguration, int)
	 */
	@Override
	public boolean validate(DAGAnnotatedPlan left, DAGAnnotatedPlan right, int depth) {
		return left.getHeight() + right.getHeight() == depth && this.validate(left, right);
	}

	/**
	 * @return Validator
	 * @see uk.ac.ox.cs.pdq.planner.dag.explorer.validators.Validator#clone()
	 */
	@Override
	public Validator clone() {
		return new DepthValidator(this.depthThreshold);
	}

}