// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.test.datasources.simplewebservice;

import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;

import uk.ac.ox.cs.pdq.datasources.ExecutableAccessMethod;
import uk.ac.ox.cs.pdq.datasources.io.jaxb.DbIOManager;
import uk.ac.ox.cs.pdq.datasources.io.jaxb.XmlExecutableAccessMethod.PostParameter;
import uk.ac.ox.cs.pdq.datasources.memory.InMemoryAccessMethod;
import uk.ac.ox.cs.pdq.datasources.simplewebservice.XmlWebService;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Cache;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.tuple.Tuple;
import uk.ac.ox.cs.pdq.db.tuple.TupleType;

public class XmlWebServiceTest {

	@Test
	@Ignore
	public void testLoad() {

		XmlWebService target;

		Integer[] inputs;

		Relation relation = Mockito.mock(Relation.class);
		Attribute[] relationAttributes = new Attribute[] {
				Attribute.create(Integer.class, "key"),
				Attribute.create(String.class, "name"), 
				Attribute.create(String.class, "comment")};
		
		when(relation.getAttributes()).thenReturn(relationAttributes.clone());
		when(relation.getName()).thenReturn("TestRelation1");

		Attribute[] amAttributes = new Attribute[] {
				Attribute.create(String.class, "n_name"), 
				Attribute.create(Integer.class, "n_nationkey"),
				Attribute.create(String.class, "n_comment"), 
				Attribute.create(Integer.class, "n_regionkey")};

		Map<Attribute, Attribute> attributeMapping = new HashMap<Attribute, Attribute>();
		attributeMapping.put(Attribute.create(Integer.class, "n_nationkey"), Attribute.create(Integer.class, "key"));
		attributeMapping.put(Attribute.create(String.class, "n_comment"), Attribute.create(String.class, "comment"));
		attributeMapping.put(Attribute.create(String.class, "n_name"), Attribute.create(String.class, "name"));

		// These constructions should work.
		inputs = new Integer[1];
		inputs[0] = 1;
		target = new XmlWebService(amAttributes, inputs, relation, attributeMapping);
		target.setUrl("http://pdq-webapp.cs.ox.ac.uk:80/webapp/servlets/servlet/NationInput");
		//target.setUrl("http://localhost:8080/webapp/servlets/servlet/NationInput");
		List<PostParameter> requestTemplates = new ArrayList<>();
		requestTemplates.add(new PostParameter( "n_nationkey","{0}"));
		target.setRequestTemplates(requestTemplates);
		
		File out = new File("test" + File.separator + "src" + File.separator + "uk" + File.separator + "ac" + File.separator + "ox" + File.separator + 
				"cs" + File.separator + "pdq" + File.separator + "test" + File.separator + "datasources" + File.separator + "simplewebservice" + 
				File.separator + "webServiceExported.xml");
		
		try {
			DbIOManager.exportAccessMethod(target, out);
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail();
		}
		XmlWebService read = null;
		try {
			read = (XmlWebService) DbIOManager.importAccess(out);
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertEquals(target.getName(),read.getName());
		Assert.assertEquals(target.getUrl(),read.getUrl());
		Assert.assertEquals(target.getRelation().getName(),read.getRelation().getName());
		Assert.assertEquals(target.getRequestTemplates().size(),read.getRequestTemplates().size());
		Assert.assertEquals(target.getRequestTemplates().get(0).getName(),read.getRequestTemplates().get(0).getName());
		Assert.assertEquals(target.getRequestTemplates().get(0).getValue(),read.getRequestTemplates().get(0).getValue());
		Assert.assertEquals(target.getNumberOfInputs(),read.getNumberOfInputs());
		Assert.assertEquals(target.getInputPosition(0),read.getInputPosition(0));
		
		TupleType tt = TupleType.DefaultFactory.create(Integer.class);
		List<Tuple> inputTuples = new ArrayList<Tuple>();
		inputTuples.add(tt.createTuple(1));
		inputTuples.add(tt.createTuple(2));
		Iterable<Tuple> data = target.access(inputTuples.iterator());
		List<Tuple> resultTuples = StreamSupport.stream(data.spliterator(), false).collect(Collectors.toList());
		Assert.assertEquals(2,resultTuples.size());
		Assert.assertEquals((Integer)2,(Integer)(resultTuples.get(1).getValue(0)));
		System.out.println(resultTuples);
		System.out.println("done.");
	}
	@Test
	public void testJson() {
		
		try
		{
			File jsonAccess = new File("test" + File.separator + "src" + File.separator + "uk" + File.separator + "ac" + File.separator + "ox" + File.separator + 
					"cs" + File.separator + "pdq" + File.separator + "test" + File.separator + "datasources" + File.separator + "simplewebservice" + 
					File.separator + "chembl-activityFree.xml");

			ExecutableAccessMethod access = DbIOManager.importAccess(jsonAccess);
			
			TupleType tupleType = TupleType.DefaultFactory.createFromTyped();
			Tuple input = tupleType.createTuple();
			Iterable<Tuple> results = access.access(Arrays.asList(new Tuple[] {input}).iterator());
			List<Tuple> tuples = StreamSupport.stream(results.spliterator(), false).collect(Collectors.toList());
			Assert.assertEquals(20, tuples.size());
			int i = 0;
			for (Tuple t: tuples)
				System.out.println("#"+i++ +" " +t);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testAccess() {

		InMemoryAccessMethod target;
		Iterable<Tuple> result;

		Integer[] inputs;

		Relation relation = Mockito.mock(Relation.class);
		Attribute[] relationAttributes = new Attribute[] {Attribute.create(Integer.class, "a"),
				Attribute.create(Integer.class, "b"), Attribute.create(String.class, "c")};
		when(relation.getAttributes()).thenReturn(relationAttributes.clone());

		Attribute[] amAttributes = new Attribute[] {
				Attribute.create(String.class, "W"), Attribute.create(Integer.class, "X"),
				Attribute.create(Integer.class, "Y"), Attribute.create(Integer.class, "Z")};

		Map<Attribute, Attribute> attributeMapping = new HashMap<Attribute, Attribute>();
		attributeMapping.put(Attribute.create(Integer.class, "X"), Attribute.create(Integer.class, "a"));
		attributeMapping.put(Attribute.create(Integer.class, "Y"), Attribute.create(Integer.class, "b"));
		attributeMapping.put(Attribute.create(String.class, "W"), Attribute.create(String.class, "c"));

		/*
		 * Test with no inputs and results in the internal schema.
		 */
		inputs = new Integer[0];
		target = new InMemoryAccessMethod(amAttributes, inputs, relation, attributeMapping);

		// Create some tuples conforming to the external (access method) schema.
		TupleType tt = TupleType.DefaultFactory.create(String.class, Integer.class, Integer.class, Integer.class);

		Collection<Tuple> tuples = new ArrayList<Tuple>();
		tuples.add(tt.createTuple("Alice", 40, 162, 42));
		tuples.add(tt.createTuple("Bob", 31, 174, 61));
		tuples.add(tt.createTuple("John", 52, 174, 55));

		Assert.assertEquals(0, target.getData().size());
		target.load(tuples);
		Assert.assertEquals(3, target.getData().size());

		result = target.access();

		// The tuples in the result conform to the internal (relation) schema.  
		Iterator<Tuple> it;
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		Collection<Tuple> actual; 
		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(3, next.size());
			actual.add(next);
		}

		Assert.assertEquals(3, actual.size());

		Tuple[] actualArray;
		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("Alice", actualArray[0].getValue(2));
		Assert.assertEquals("Bob", actualArray[1].getValue(2));
		Assert.assertEquals("John", actualArray[2].getValue(2));

		/*
		 * Test with no inputs and results in the external schema.
		 */
		result = target.access(false);

		// The tuples in the result conform to the internal (relation) schema.  
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());
		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(4, next.size());
			actual.add(next);
		}

		Assert.assertEquals(3, actual.size());

		Assert.assertEquals(tuples, actual);

		// In this case, the result is identical to the loaded tuples.
		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("Alice", actualArray[0].getValue(0));
		Assert.assertEquals("Bob", actualArray[1].getValue(0));
		Assert.assertEquals("John", actualArray[2].getValue(0));
	}

	@Test
	public void testAccessIteratorTuple() {

		Cache.reStartCaches();

		InMemoryAccessMethod target;
		Iterable<Tuple> result;
		boolean caught;

		Integer[] inputs;
		Collection<Tuple> inputTuples;

		Relation relation = Mockito.mock(Relation.class);
		Attribute[] relationAttributes = new Attribute[] {Attribute.create(Integer.class, "a"),
				Attribute.create(Integer.class, "b"), Attribute.create(String.class, "c")};
		when(relation.getAttributes()).thenReturn(relationAttributes.clone());

		Attribute[] amAttributes = new Attribute[] {
				Attribute.create(String.class, "W"), Attribute.create(Integer.class, "X"),
				Attribute.create(Integer.class, "Y"), Attribute.create(Integer.class, "Z")};

		Map<Attribute, Attribute> attributeMapping = new HashMap<Attribute, Attribute>();
		attributeMapping.put(Attribute.create(Integer.class, "X"), Attribute.create(Integer.class, "a"));
		attributeMapping.put(Attribute.create(Integer.class, "Y"), Attribute.create(Integer.class, "b"));
		attributeMapping.put(Attribute.create(String.class, "W"), Attribute.create(String.class, "c"));

		// Create some tuples conforming to the external (access method) schema.
		TupleType tt = TupleType.DefaultFactory.create(String.class, Integer.class, Integer.class, Integer.class);

		Collection<Tuple> tuples = new ArrayList<Tuple>();
		tuples.add(tt.createTuple("Alice", 40, 162, 42));
		tuples.add(tt.createTuple("Bob", 31, 174, 60));
		tuples.add(tt.createTuple("John", 52, 174, 55));

		/*
		 * Test with inputs on position 1 and results in the internal schema.
		 * Note that Integer[] inputs always refers to the external schema.
		 */
		inputs = new Integer[] {1};
		target = new InMemoryAccessMethod(amAttributes, inputs, relation, attributeMapping);

		Assert.assertEquals(0, target.getData().size());
		target.load(tuples);
		Assert.assertEquals(3, target.getData().size());

		// Attempting a "free access" (i.e. without inputs) throws an IllegalStateException. 
		caught = false;
		try {
			target.access();;
		} catch(IllegalStateException e) {
			caught = true;
		}
		Assert.assertTrue(caught);

		// Attempting to access with invalid inputs throws an IllegalArgumentException.
		// The tuples are invalid inputs since they conform to the external schema.
		caught = false;
		try {
			target.access(tuples.iterator());;
		} catch(IllegalArgumentException e) {
			caught = true;
		}
		Assert.assertTrue(caught);

		// Create some input tuples conforming to the internal schema.
		TupleType ttInputs;
		ttInputs = TupleType.DefaultFactory.create(Integer.class);
		inputTuples = new ArrayList<Tuple>();
		inputTuples.add(ttInputs.createTuple(31));

		// With valid inputs the access is successful.
		result = target.access(inputTuples.iterator());

		// The tuples in the result conform to the internal (relation) schema.  
		Iterator<Tuple> it;
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		Collection<Tuple> actual; 
		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(3, next.size());
			actual.add(next);
		}

		Assert.assertEquals(1, actual.size());

		Tuple[] actualArray;
		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("Bob", actualArray[0].getValue(2));

		// Test with different input tuples.
		inputTuples = new ArrayList<Tuple>();
		inputTuples.add(ttInputs.createTuple(31));
		inputTuples.add(ttInputs.createTuple(40));

		result = target.access(inputTuples.iterator());

		// The tuples in the result conform to the internal (relation) schema.  
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(3, next.size());
			actual.add(next);
		}

		Assert.assertEquals(2, actual.size());

		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("Alice", actualArray[0].getValue(2));
		Assert.assertEquals("Bob", actualArray[1].getValue(2));

		/*
		 * Test with inputs on position 2 and results in the internal schema.
		 * Note that Integer[] inputs always refers to the external schema.
		 */
		inputs = new Integer[] {2};
		target = new InMemoryAccessMethod(amAttributes, inputs, relation, attributeMapping);

		Assert.assertEquals(0, target.getData().size());
		target.load(tuples);
		Assert.assertEquals(3, target.getData().size());

		// Create some input tuples conforming to the internal schema.
		ttInputs = TupleType.DefaultFactory.create(Integer.class);
		inputTuples = new ArrayList<Tuple>();
		inputTuples.add(ttInputs.createTuple(174));

		result = target.access(inputTuples.iterator());

		// The tuples in the result conform to the internal (relation) schema.  
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(3, next.size());
			actual.add(next);
		}

		Assert.assertEquals(2, actual.size());

		actualArray = actual.toArray(new Tuple[0]);

		// Both Bob and John match the input tuple (i.e. attribute "b" has value 174).
		Assert.assertEquals("Bob", actualArray[0].getValue(2));
		Assert.assertEquals("John", actualArray[1].getValue(2));

		/*
		 * Test with inputs on positions 0 and 2 and results in the internal schema.
		 * Note that Integer[] inputs always refers to the external schema.
		 */
		inputs = new Integer[] {0, 2};
		target = new InMemoryAccessMethod(amAttributes, inputs, relation, attributeMapping);

		Assert.assertEquals(0, target.getData().size());
		target.load(tuples);
		Assert.assertEquals(3, target.getData().size());

		// Create some input tuples conforming to the internal schema.
		ttInputs = TupleType.DefaultFactory.create(Integer.class, String.class);
		inputTuples = new ArrayList<Tuple>();
		inputTuples.add(ttInputs.createTuple(174, "John"));
		inputTuples.add(ttInputs.createTuple(174, "Alice"));

		result = target.access(inputTuples.iterator());

		// The tuples in the result conform to the internal (relation) schema.  
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(3, next.size());
			actual.add(next);
		}

		Assert.assertEquals(1, actual.size());

		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("John", actualArray[0].getValue(2));

		// Test with different input tuples.
		inputTuples.add(ttInputs.createTuple(174, "John"));
		inputTuples.add(ttInputs.createTuple(162, "Alice"));

		result = target.access(inputTuples.iterator());

		// The tuples in the result conform to the internal (relation) schema.  
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(3, next.size());
			actual.add(next);
		}

		Assert.assertEquals(2, actual.size());

		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("Alice", actualArray[0].getValue(2));
		Assert.assertEquals("John", actualArray[1].getValue(2));

		/*
		 * Test with inputs on position 0 and results in the external schema.
		 */
		inputs = new Integer[] {0};
		target = new InMemoryAccessMethod(amAttributes, inputs, relation, attributeMapping);

		Assert.assertEquals(0, target.getData().size());
		target.load(tuples);
		Assert.assertEquals(3, target.getData().size());

		// Create some input tuples conforming to the internal schema.
		ttInputs = TupleType.DefaultFactory.create(String.class);
		inputTuples = new ArrayList<Tuple>();
		inputTuples.add(ttInputs.createTuple("Alice"));
		inputTuples.add(ttInputs.createTuple("Eve"));

		// Attempting a "free access" (i.e. without inputs) throws an IllegalStateException. 
		caught = false;
		try {
			target.access(false);;
		} catch(IllegalStateException e) {
			caught = true;
		}
		Assert.assertTrue(caught);

		// With valid inputs the access is successful.
		result = target.access(inputTuples.iterator(), false);

		// The tuples in the result conform to the external schema.  
		it = result.iterator(); 
		Assert.assertTrue(it.hasNext());

		actual = new ArrayList<Tuple>();
		while(it.hasNext()) {
			Tuple next = it.next();
			Assert.assertEquals(4, next.size());
			actual.add(next);
		}

		Assert.assertEquals(1, actual.size());

		actualArray = actual.toArray(new Tuple[0]);

		Assert.assertEquals("Alice", actualArray[0].getValue(0));
	}

	//@Test
	public void stressTest1() {

		// Scale parameter (number of tuples in each relation):
		int N = 3000000;

		Attribute[] relationR1Attributes = new Attribute[] {Attribute.create(String.class, "a"),
				Attribute.create(String.class, "b")};
		Relation relationR1 = Relation.create("R1", relationR1Attributes);

		Map<Attribute, Attribute> attributeMapping1 = ImmutableMap.of(
				Attribute.create(String.class, "A"), Attribute.create(String.class, "b"),
				Attribute.create(String.class, "B"), Attribute.create(String.class, "a"));

		Attribute[] amAttributes = new Attribute[] {Attribute.create(String.class, "A"),
				Attribute.create(String.class, "B")};

		InMemoryAccessMethod target = new InMemoryAccessMethod(amAttributes, 
				new Integer[0], relationR1, attributeMapping1);

		// Create some tuples. 
		TupleType ttStringString = TupleType.DefaultFactory.create(String.class, String.class);
		Collection<Tuple> tuples1 = new ArrayList<Tuple>();
		for (int i = 0; i != N; i++) {
			String[] values = new String[] {"A" + i, "B" + i};
			tuples1.add(ttStringString.createTuple((Object[]) Arrays.copyOf(values, values.length)));
		}

		// Load tuples   
		target.load(tuples1);

		// Access the tuples. 
		List<Tuple> result = new ArrayList<Tuple>();

//		boolean translate = true;
		boolean translate = false;
		Iterator<Tuple> it = target.access(translate).iterator();
		while (it.hasNext())
			result.add(it.next());

		Assert.assertEquals(N, result.size());
		Tuple expected = translate ? ttStringString.createTuple("B0", "A0") : 
			ttStringString.createTuple("A0", "B0");

		Assert.assertTrue(result.get(0).equals(expected));
	}
}
