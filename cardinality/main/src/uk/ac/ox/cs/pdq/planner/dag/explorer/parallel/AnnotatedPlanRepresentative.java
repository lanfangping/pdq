package uk.ac.ox.cs.pdq.planner.dag.explorer.parallel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

import uk.ac.ox.cs.pdq.planner.dag.DAGAnnotatedPlan;
import uk.ac.ox.cs.pdq.planner.dag.equivalence.DAGAnnotatedPlanClass;
import uk.ac.ox.cs.pdq.planner.dag.equivalence.DAGAnnotatedPlanClasses;

/**
 * Map of representatives. For each configuration c = BinConfiguration(c_1,c_2) we create a map from the
 * equivalence classes of c and c' to c''. This map helps us reducing the chasing time, i.e.,
 * if c'_1 and c'_2 are structurally equivalent to c_1 and c_2, respectively, and
 * c = BinConfiguration(c_1,c_2) has already been fully chased,
 * then we copy the state of c to the state of c' = BinConfiguration(c'_1,c'_2).
 * 
 * @author Efthymia Tsamoura
 *
 * @param 
 */
public class AnnotatedPlanRepresentative {
	
	private final Map<Pair<DAGAnnotatedPlanClass,DAGAnnotatedPlanClass>,DAGAnnotatedPlan> representatives  = new ConcurrentHashMap<>();

	public DAGAnnotatedPlan getRepresentative(DAGAnnotatedPlanClasses eclasses, DAGAnnotatedPlan left, DAGAnnotatedPlan right) {
		//The equivalence class of the left input configuration
		DAGAnnotatedPlanClass rep0 = eclasses.getEquivalenceClass(left);
		//The equivalence class of the left input configuration
		DAGAnnotatedPlanClass rep1 = eclasses.getEquivalenceClass(right);
		//A configuration BinConfiguration(c,c'), where c and c' belong to the equivalence classes of
		//the left and right input configuration, respectively.
		return this.representatives.get(Pair.of(rep0, rep1));
	}
	
	public void put(DAGAnnotatedPlanClasses classes, DAGAnnotatedPlan left, DAGAnnotatedPlan right, DAGAnnotatedPlan representative) {
		this.representatives.put(Pair.of(classes.getEquivalenceClass(left), classes.getEquivalenceClass(right)), representative);
	}
}