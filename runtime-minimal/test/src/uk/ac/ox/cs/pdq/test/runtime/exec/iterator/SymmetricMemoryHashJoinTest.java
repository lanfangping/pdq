package uk.ac.ox.cs.pdq.test.runtime.exec.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.ox.cs.pdq.algebra.AttributeEqualityCondition;
import uk.ac.ox.cs.pdq.algebra.Condition;
import uk.ac.ox.cs.pdq.algebra.ConjunctiveCondition;
import uk.ac.ox.cs.pdq.algebra.ConstantEqualityCondition;
import uk.ac.ox.cs.pdq.algebra.SimpleCondition;
import uk.ac.ox.cs.pdq.datasources.memory.InMemoryTableWrapper;
import uk.ac.ox.cs.pdq.datasources.sql.PostgresqlRelationWrapper;
import uk.ac.ox.cs.pdq.datasources.sql.SQLRelationWrapper;
import uk.ac.ox.cs.pdq.datasources.utility.Table;
import uk.ac.ox.cs.pdq.db.AccessMethod;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.TypedConstant;
import uk.ac.ox.cs.pdq.runtime.TimeoutException;
import uk.ac.ox.cs.pdq.runtime.exec.PipelinedPlanExecutor.TimeoutChecker;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.Access;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.Join;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.Selection;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.SymmetricMemoryHashJoin;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.TupleIterator;
import uk.ac.ox.cs.pdq.util.Tuple;
import uk.ac.ox.cs.pdq.util.TupleType;

public class SymmetricMemoryHashJoinTest {

	AccessMethod amFree = AccessMethod.create("free_access",new Integer[] {});

	InMemoryTableWrapper relation1 = new InMemoryTableWrapper("R1", new Attribute[] {Attribute.create(Integer.class, "a"),
			Attribute.create(Integer.class, "b"), Attribute.create(Integer.class, "c")},
			new AccessMethod[] {amFree});
	InMemoryTableWrapper relation2 = new InMemoryTableWrapper("R2", new Attribute[] {Attribute.create(Integer.class, "c"),
			Attribute.create(Integer.class, "d"), Attribute.create(Integer.class, "e")},
			new AccessMethod[] {amFree});
	InMemoryTableWrapper relation3 = new InMemoryTableWrapper("R3", new Attribute[] {Attribute.create(Integer.class, "b"),
			Attribute.create(Integer.class, "c"), Attribute.create(Integer.class, "d"), Attribute.create(Integer.class, "f")},
			new AccessMethod[] {amFree});

	@Test
	public void testSymmetricMemoryHashJoin() {

		// Sanity check the Join constructor
		Join target = new SymmetricMemoryHashJoin(new Access(relation1, amFree), new Access(relation2, amFree));

		Assert.assertNotNull(target);
		Assert.assertNotNull(target.getJoinConditions());

		// Test that the join condition (computed in the Join constructor) requires that the
		// values associated with the common attribute (i.e. attribute "c") are equal. That is,
		// the attribute in position 2 of the leftChild equals that in position 3 (child1.length+position) of the right child. 
		Condition expected = ConjunctiveCondition.create(
				new SimpleCondition[] {AttributeEqualityCondition.create(2, 3)});

		Assert.assertEquals(expected, target.getJoinConditions());
	}

	/*
	 * The following are integration tests: SymmetricMemoryHashJoin instances are constructed and executed.
	 */

	//Execute plans by passing them to this method
	private Table planExecution(TupleIterator plan) throws TimeoutException {
		Table results = new Table(plan.getOutputAttributes());
		ExecutorService execService = Executors.newFixedThreadPool(1);
		execService.execute(new TimeoutChecker(3600000, plan));
		plan.open();
		while (plan.hasNext()) {
			Tuple t = plan.next();
			results.appendRow(t);
		}
		execService.shutdownNow();
		if (plan.isInterrupted()) {
			throw new TimeoutException();
		}
		return results;
	}

	@Test
	public void test0() {

		/*
		 * Simple plan that joins the results of two accesses.
		 * The Join constructor assumes that two attributes should be equal if they have the same name.
		 * This is the so called equijoin. 
		 */
		Join target = new SymmetricMemoryHashJoin(new Access(relation1, amFree), new Access(relation2, amFree));

		// Create some tuples. 
		TupleType tt = TupleType.DefaultFactory.create(Integer.class, Integer.class, Integer.class);

		// Here we join on columns containing no duplicates.  
		Collection<Tuple> tuples1 = new ArrayList<Tuple>();
		int N = 12;
		for (int i = 0; i != N; i++) {
			Integer[] values = new Integer[] {i, i + 1, i + 2};
			tuples1.add(tt.createTuple((Object[]) Arrays.copyOf(values, values.length)));
		}
		Collection<Tuple> tuples2 = new ArrayList<Tuple>();
		int M = 18;
		for (int i = 0; i != M; i++) {
			Integer[] values = new Integer[] {i + 8, i + 9, i + 10};
			tuples2.add(tt.createTuple((Object[]) Arrays.copyOf(values, values.length)));
		}

		// Load tuples   
		relation1.load(tuples1);
		relation2.load(tuples2);

		//Execute the plan
		Table result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// Check that the result tuples are the ones expected. 
		// Here the join condition is *never* satisfied because the attributes named "c" 
		// (i.e. the last and first, respectively) first have different values in relation1 & relation2.
		Assert.assertNotNull(result);
		// In relation1 the "c" column ranges from 2 to 13. In relation2 the "c" column ranges from 8 to 25.
		// Note that the result tuples contain the tuple from the right appended onto the tuple from the left.
		TupleType tte = TupleType.DefaultFactory.create(Integer.class, Integer.class, Integer.class, 
				Integer.class, Integer.class, Integer.class);
		List<Tuple> expected = new ArrayList<Tuple>();
		expected.add(tte.createTuple((Object[]) new Integer[] { 6, 7, 8, 8, 9, 10}));
		expected.add(tte.createTuple((Object[]) new Integer[] { 7, 8, 9, 9, 10, 11}));
		expected.add(tte.createTuple((Object[]) new Integer[] { 8, 9, 10, 10, 11, 12}));
		expected.add(tte.createTuple((Object[]) new Integer[] { 9, 10, 11, 11, 12, 13}));
		expected.add(tte.createTuple((Object[]) new Integer[] { 10, 11, 12, 12, 13, 14}));
		expected.add(tte.createTuple((Object[]) new Integer[] { 11, 12, 13, 13, 14, 15}));

		// The values in common are 10 to 13.
		Assert.assertEquals(6, result.size());
		Assert.assertEquals(expected, result.getData());

	}

	@Test
	public void test1() {

		/*
		 * Simple plan that joins the results of two accesses.
		 * The Join constructor assumes that two attributes should be equal if they have the same name.
		 * This is the so called equijoin.
		 */
		Join target = new SymmetricMemoryHashJoin(new Access(relation1, amFree), new Access(relation2, amFree));

		// Create some tuples
		TupleType tt = TupleType.DefaultFactory.create(Integer.class, Integer.class, Integer.class);

		Integer[] values1 = new Integer[] {10, 11, 12};
		Collection<Tuple> tuples1 = new ArrayList<Tuple>();
		int N = 100;
		for (int i = 0; i != N; i++) {
			tuples1.add(tt.createTuple((Object[]) Arrays.copyOf(values1, values1.length)));
		}

		// Load tuples   
		relation1.load(tuples1);
		relation2.load(tuples1);

		//Execute the plan
		Table result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// Check that the result tuples are the ones expected. 
		// Here the join condition is *never* satisfied because the attributes named "c" 
		// (i.e. the last and first, respectively) first have different values in relation1 & relation2.
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());

		// Create some more tuples.
		Integer[] values2 = new Integer[] {12, 13, 14};
		Collection<Tuple> tuples2 = new ArrayList<Tuple>();
		for (int i = 0; i != N; i++) {
			tuples2.add(tt.createTuple((Object[]) Arrays.copyOf(values2, values2.length)));
		}

		// Load tuples into relation2 such that the join condition is *always* satisfied. 
		relation2.clear();
		relation2.load(tuples2);

		// Reconstruct the target Join (TODO: instead, reset ought to work here)
		// target.reset();
		target = new SymmetricMemoryHashJoin(new Access(relation1, amFree), new Access(relation2, amFree));

		//Execute the plan
		result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// Check that the result tuples are the ones expected.
		// Since the condition is always satisfied and we are doing a full outer join, 
		// we expect the result to be the cross product.
		Assert.assertNotNull(result);
		Assert.assertEquals(N*N, result.size());
		// Note that the result tuples contain the tuple from the right appended onto the tuple from the left.
		for (Tuple x:result.getData())
			Assert.assertArrayEquals(new Integer[] {10, 11, 12, 12, 13, 14}, x.getValues());

		// Load tuples into relation2 such that the join condition is *sometimes* satisfied. 
		relation2.clear();
		tuples2.clear();
		for (int i = 0; i != N; i++) {
			Integer[] values = ((i % 4) == 0) ? values2 : values1;
			tuples2.add(tt.createTuple((Object[]) Arrays.copyOf(values, values.length)));
		}
		relation2.load(tuples2);

		// Reconstruct the target Join (TODO: instead, reset ought to work here)
		target = new SymmetricMemoryHashJoin(new Access(relation1, amFree), new Access(relation2, amFree));

		//Execute the plan
		result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// Check that the result tuples are the ones expected
		Assert.assertNotNull(result);
		Assert.assertEquals(N*(N/4), result.size());
		// Note that the result tuples contain the tuple from the right appended onto the tuple from the left.
		for (Tuple x:result.getData())
			Assert.assertArrayEquals(new Integer[] {10, 11, 12, 12, 13, 14}, x.getValues());

	}

	@Test
	public void test2() {

		/*
		 * Another plan that joins the results of two accesses.
		 * The Join constructor assumes that two attributes should be equal if they have the same name.
		 * This is the so called equijoin.
		 */
		Join target = new SymmetricMemoryHashJoin(new Access(relation2, amFree), new Access(relation3, amFree));

		// Create some tuples
		TupleType tt2 = TupleType.DefaultFactory.create(Integer.class, Integer.class, Integer.class);
		TupleType tt3 = TupleType.DefaultFactory.create(Integer.class, Integer.class, Integer.class, Integer.class);

		Integer[] values1 = new Integer[] {10, 11, 12};
		Integer[] values2 = new Integer[] {10, 13, 12};
		Integer[] values3 = new Integer[] {9, 10, 11, 13};
		Integer[] values4 = new Integer[] {9, 10, 12, 14};

		Collection<Tuple> tuples2 = new ArrayList<Tuple>();
		int N = 100;
		for (int i = 0; i != N; i++) {
			Integer[] values = ((i % 2) == 0) ? values1 : values2;
			tuples2.add(tt2.createTuple((Object[]) Arrays.copyOf(values, values.length)));
		}
		Collection<Tuple> tuples3 = new ArrayList<Tuple>();
		int M = 200;
		for (int i = 0; i != M; i++) {
			Integer[] values = ((i % 3) == 0) ? values3 : values4;
			tuples3.add(tt3.createTuple((Object[]) Arrays.copyOf(values, values.length)));
		}

		// Load tuples   
		relation2.load(tuples2);
		relation3.load(tuples3);

		//Execute the plan
		Table result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// Check that the result tuples are the ones expected. 
		// Here the join condition is only satisfied between values1 and values3.
		// values1 appears 50 times in relation2 and values3 appears 67 times in relation 3.
		Assert.assertNotNull(result);
		Assert.assertEquals(50*67, result.size());

		// Note that the result tuples contain the tuple from the right appended onto the tuple from the left.
		for (Tuple x:result.getData())
			Assert.assertArrayEquals(new Integer[] {10, 11, 12, 9, 10, 11, 13}, x.getValues());
	}

	/*
	 *  PostgresqlRelation construction.
	 */
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty("url", "jdbc:postgresql://localhost:5432/");
		properties.setProperty("database", "tpch");
		properties.setProperty("username", "admin");
		properties.setProperty("password", "admin");
		return(properties);
	}

	Attribute[] attributes_C = new Attribute[] {
			Attribute.create(Integer.class, "C_CUSTKEY"),
			Attribute.create(String.class, "C_NAME"),
			Attribute.create(String.class, "C_ADDRESS"),
			Attribute.create(Integer.class, "C_NATIONKEY"),
			Attribute.create(String.class, "C_PHONE"),
			Attribute.create(Float.class, "C_ACCTBAL"),
			Attribute.create(String.class, "C_MKTSEGMENT"),
			Attribute.create(String.class, "C_COMMENT")
	};

	Attribute[] attributes_S = new Attribute[] {
			Attribute.create(Integer.class, "S_SUPPKEY"),
			Attribute.create(String.class, "S_NAME"),
			Attribute.create(String.class, "S_ADDRESS"),
			Attribute.create(Integer.class, "S_NATIONKEY"),
			Attribute.create(String.class, "S_PHONE"),
			Attribute.create(Float.class, "S_ACCTBAL"),
			Attribute.create(String.class, "S_COMMENT")
	};

	SQLRelationWrapper postgresqlRelationCustomer = new PostgresqlRelationWrapper(this.getProperties(), "CUSTOMER", 
			attributes_C, new AccessMethod[] {amFree});
	SQLRelationWrapper postgresqlRelationSupplier = new PostgresqlRelationWrapper(this.getProperties(), "SUPPLIER", 
			attributes_S, new AccessMethod[] {amFree});


	//@Test runs for ever
	public void test3() {

		/*
		 * A plan that joins the results of two accesses.
		 */
		Join target = new SymmetricMemoryHashJoin(new Access(postgresqlRelationCustomer, amFree), 
				new Access(postgresqlRelationSupplier, amFree));

		//Execute the plan
		Table result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// TODO. Check that the result tuples are the ones expected. 
		Assert.assertNotNull(result);
		// TODO: execute a join manually to determine the expected size.
		Assert.assertEquals(1000, result.size());
		// TODO: check that the common attributes (by name) have common values.

	}
	
	@Test
	public void test4() {

		/*
		 * A plan that joins the results of an access and a selection on an access.
		 */
		Condition nationkeyCondition = ConstantEqualityCondition.create(3, TypedConstant.create(44));
		Selection selection = new Selection(nationkeyCondition, new Access(postgresqlRelationCustomer, amFree));
		Join target = new SymmetricMemoryHashJoin(new Access(postgresqlRelationSupplier, amFree), selection);
		
		//Execute the plan
		Table result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// TODO. Check that the result tuples are the ones expected. 
		Assert.assertNotNull(result);
		// TODO: execute a join manually to determine the expected size.
		Assert.assertEquals(0, result.size());
		// TODO: check that the common attributes (by name) have common values.

	}
	
	@Test
	public void test5() {

		/*
		 * A plan that joins the results of a selection on an access and an access.
		 */
		Condition nationkeyCondition = ConstantEqualityCondition.create(3, TypedConstant.create(44));
		Condition mktsegmentCondition = ConstantEqualityCondition.create(6, TypedConstant.create("TODO"));
		Selection selection = new Selection(mktsegmentCondition, 
				new Selection(nationkeyCondition, new Access(postgresqlRelationCustomer, amFree)));

		Join target = new SymmetricMemoryHashJoin(selection, new Access(postgresqlRelationSupplier, amFree));

		//Execute the plan
		Table result = null;
		try {
			result = this.planExecution(target);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		// TODO. Check that the result tuples are the ones expected. 
		Assert.assertNotNull(result);
		// TODO: execute a join manually to determine the expected size.
		Assert.assertEquals(0, result.size());
		// TODO: check that the common attributes (by name) have common values.

	}

}