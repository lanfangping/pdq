package uk.ac.ox.cs.pdq.runtime.exec.spliterator;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import jersey.repackaged.com.google.common.base.Preconditions;
import uk.ac.ox.cs.pdq.algebra.Plan;
import uk.ac.ox.cs.pdq.algebra.SelectionTerm;
import uk.ac.ox.cs.pdq.runtime.exec.PlanDecorator;
import uk.ac.ox.cs.pdq.util.Tuple;

/**
 * An executable selection plan. 
 * 
 * @author Tim Hobson
 *
 */
public class Selection extends UnaryExecutablePlan {

	private final Predicate<Tuple> filterPredicate;

	public Selection(Plan plan) {
		super(plan);
		// Check compatibility with the given Plan instance.
		Preconditions.checkArgument(plan instanceof SelectionTerm);

		// Assign the (decorated) child plan to the child field.
		this.child = PlanDecorator.decorate(this.getDecoratedPlan().getChildren()[0]);

		// Assign the filter predicate field, based on the selection condition.
		this.filterPredicate = ((SelectionTerm) this.getDecoratedPlan()).getSelectionCondition().asPredicate();
	}

	@Override
	public Spliterator<Tuple> spliterator() {
		return new SelectionSpliterator(this.child.spliterator());
	}

	public Predicate<Tuple> getFilterPredicate() {
		return this.filterPredicate;
	}

	private class SelectionSpliterator extends UnaryPlanSpliterator {

		public SelectionSpliterator(Spliterator<Tuple> childSpliterator) {
			super(childSpliterator);
		}

		@Override
		public boolean tryAdvance(Consumer<? super Tuple> action) {
			return StreamSupport.stream(this.childSpliterator, false)
					.filter(filterPredicate)
					.spliterator().tryAdvance(action);
		}
	}
}
