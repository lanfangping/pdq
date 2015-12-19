package uk.ac.ox.cs.pdq.planner.reasoning.chase.dominance;

import java.util.ArrayList;

import uk.ac.ox.cs.pdq.planner.PlannerParameters.DominanceTypes;
import uk.ac.ox.cs.pdq.planner.PlannerParameters.SuccessDominanceTypes;
import uk.ac.ox.cs.pdq.planner.cardinality.CardinalityEstimator;

/**
 * Creates cost dominance detectors using the input parameters.
 * The available options are:
 * 		CLOSED for closed dominance and
 * 		OPEN for open dominance (an open configuration may dominate a closed one)
 *
 * @author Efthymia Tsamoura
 */
public class DominanceFactory {

	/**
	 * 
	 * @param type
	 * @param cardinalityEstimator
	 * @return
	 */
	public static Dominance[] getInstance(DominanceTypes type, CardinalityEstimator cardinalityEstimator) {
		ArrayList<Dominance> detector = new ArrayList<>();
		switch(type) {
		case TIGHT:
			detector.add(new TightQualityDominance(cardinalityEstimator));
			break;
		default:
			break;
		}
		Dominance<?>[] array = new Dominance[detector.size()];
		detector.toArray(array);
		return array;
	}
	
	/**
	 * 
	 * @param type
	 * @param cardinalityEstimator
	 * @return
	 */
	public static Dominance getInstance(SuccessDominanceTypes type, CardinalityEstimator cardinalityEstimator) {
		switch(type) {
		case LOOSE:
			return new LooseQualityDominance(cardinalityEstimator);
		default:
			break;
		}
		return null;
	}
}