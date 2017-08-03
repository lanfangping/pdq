package uk.ac.ox.cs.pdq.test.planner.dag.explorer.filters;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import uk.ac.ox.cs.pdq.cost.DoubleCost;
import uk.ac.ox.cs.pdq.fol.Constant;
import uk.ac.ox.cs.pdq.fol.UntypedConstant;
import uk.ac.ox.cs.pdq.planner.dag.explorer.filters.FactDominationFilter;
import uk.ac.ox.cs.pdq.test.planner.TestObjects1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

// TODO: Auto-generated Javadoc
/**
 * The Class FactDominationFilterTest.
 *
 * @author Efthymia Tsamoura
 */

public class FactDominationFilterTest extends TestObjects1{

	/** The filter. */
	FactDominationFilter filter = new FactDominationFilter();

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.test.planner.TestObjects1#setup()
	 */
	@Before public void setup() {
		super.setup();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test1.
	 */
	@Ignore public void test1() {
		when(config11.getState()).thenReturn(config11State);
		when(config11State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3));
		when(config11.getPlan()).thenReturn(plan11);
		when(plan11Cost).thenReturn(new DoubleCost(3.0));
		when(config11.isClosed()).thenReturn(false);
		when(config11.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1")));

		when(config12.getState()).thenReturn(config12State);
		when(config12State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p3,p2));
		when(config12.getPlan()).thenReturn(plan12);
		when(plan12Cost).thenReturn(new DoubleCost(3.0));
		when(config12.isClosed()).thenReturn(false);
		when(config12.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1")));

		when(config21.getState()).thenReturn(config21State);
		when(config21State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4));
		when(config21.getPlan()).thenReturn(plan21);
		when(plan21Cost).thenReturn(new DoubleCost(4.0));
		when(config21.isClosed()).thenReturn(true);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList());

		when(config22.getState()).thenReturn(config22State);
		when(config22State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p4));
		when(config22.getPlan()).thenReturn(plan22);
		when(plan22Cost).thenReturn(new DoubleCost(2.0));
		when(config22.isClosed()).thenReturn(true);
		when(config22.getInput()).thenReturn(Lists.<Constant>newArrayList());

		when(config31.getState()).thenReturn(config31State);
		when(config31State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4,p5,p6,p7));
		when(config31.getPlan()).thenReturn(plan31);
		when(plan31Cost).thenReturn(new DoubleCost(2.0));
		when(config31.isClosed()).thenReturn(false);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1"), UntypedConstant.create("c3")));

		when(config32.getState()).thenReturn(config32State);
		when(config32State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4,p5,p6,p7));
		when(config32.getPlan()).thenReturn(plan32);
		when(plan32Cost).thenReturn(new DoubleCost(3.0));
		when(config32.isClosed()).thenReturn(true);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1"), UntypedConstant.create("c2")));

		Assert.assertEquals(this.filter.filter(Sets.newHashSet(config11, config12, config21, config22)), 
				Sets.newHashSet(config11, config12, config21, config22));
	}

	/**
	 * Test2.
	 */
	@Test public void test2() {
		when(config11.getState()).thenReturn(config11State);
		when(config11State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3));
		when(config11.getPlan()).thenReturn(plan11);
		when(plan11Cost).thenReturn(new DoubleCost(3.0));
		when(config11.isClosed()).thenReturn(false);
		when(config11.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1")));

		when(config12.getState()).thenReturn(config12State);
		when(config12State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p3,p2));
		when(config12.getPlan()).thenReturn(plan12);
		when(plan12Cost).thenReturn(new DoubleCost(3.0));
		when(config12.isClosed()).thenReturn(false);
		when(config12.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1")));

		when(config21.getState()).thenReturn(config21State);
		when(config21State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4));
		when(config21.getPlan()).thenReturn(plan21);
		when(plan21Cost).thenReturn(new DoubleCost(4.0));
		when(config21.isClosed()).thenReturn(true);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList());

		when(config22.getState()).thenReturn(config22State);
		when(config22State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p4));
		when(config22.getPlan()).thenReturn(plan22);
		when(plan22Cost).thenReturn(new DoubleCost(2.0));
		when(config22.isClosed()).thenReturn(true);
		when(config22.getInput()).thenReturn(Lists.<Constant>newArrayList());

		when(config31.getState()).thenReturn(config31State);
		when(config31State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4,p5,p6,p7));
		when(config31.getPlan()).thenReturn(plan31);
		when(plan31Cost).thenReturn(new DoubleCost(2.0));
		when(config31.isClosed()).thenReturn(false);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1"), UntypedConstant.create("c3")));

		when(config32.getState()).thenReturn(config32State);
		when(config32State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4,p5,p6,p7));
		when(config32.getPlan()).thenReturn(plan32);
		when(plan32Cost).thenReturn(new DoubleCost(3.0));
		when(config32.isClosed()).thenReturn(true);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1"), UntypedConstant.create("c2")));

		Assert.assertEquals(this.filter.filter(Sets.newHashSet(config11, config21, config22, config32)), 
				Sets.newHashSet(config11, config21, config22));
	}

	/**
	 * Test3.
	 */
	@Test public void test3() {
		when(config11.getState()).thenReturn(config11State);
		when(config11State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3));
		when(config11.getPlan()).thenReturn(plan11);
		when(plan11Cost).thenReturn(new DoubleCost(3.0));
		when(config11.isClosed()).thenReturn(false);
		when(config11.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1")));

		when(config12.getState()).thenReturn(config12State);
		when(config12State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p3,p2));
		when(config12.getPlan()).thenReturn(plan12);
		when(plan12Cost).thenReturn(new DoubleCost(3.0));
		when(config12.isClosed()).thenReturn(false);
		when(config12.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1")));

		when(config21.getState()).thenReturn(config21State);
		when(config21State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4));
		when(config21.getPlan()).thenReturn(plan21);
		when(plan21Cost).thenReturn(new DoubleCost(4.0));
		when(config21.isClosed()).thenReturn(true);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList());

		when(config22.getState()).thenReturn(config22State);
		when(config22State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p4));
		when(config22.getPlan()).thenReturn(plan22);
		when(plan22Cost).thenReturn(new DoubleCost(2.0));
		when(config22.isClosed()).thenReturn(true);
		when(config22.getInput()).thenReturn(Lists.<Constant>newArrayList());

		when(config31.getState()).thenReturn(config31State);
		when(config31State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4,p5,p6,p7));
		when(config31.getPlan()).thenReturn(plan31);
		when(plan31Cost).thenReturn(new DoubleCost(2.0));
		when(config31.isClosed()).thenReturn(false);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1"), UntypedConstant.create("c3")));

		when(config32.getState()).thenReturn(config32State);
		when(config32State.getInferredAccessibleFacts()).thenReturn(Lists.newArrayList(p1,p2,p3,p4,p5,p6,p7));
		when(config32.getPlan()).thenReturn(plan32);
		when(plan32Cost).thenReturn(new DoubleCost(3.0));
		when(config32.isClosed()).thenReturn(true);
		when(config21.getInput()).thenReturn(Lists.<Constant>newArrayList(UntypedConstant.create("c1"), UntypedConstant.create("c2")));

		Assert.assertEquals(this.filter.filter(Sets.newHashSet(config12, config21, config22, config31)), Sets.newHashSet(config12, config21, config22));
	}
}
