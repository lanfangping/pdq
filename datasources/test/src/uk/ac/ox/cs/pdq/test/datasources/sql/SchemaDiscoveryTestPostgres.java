// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.test.datasources.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ox.cs.pdq.builder.BuilderException;
import uk.ac.ox.cs.pdq.datasources.schemabuilder.PostgresqlSchemaDiscoverer;
import uk.ac.ox.cs.pdq.db.AccessMethodDescriptor;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.exceptions.DatabaseException;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.test.util.PdqTest;


/**
 * The Class PostgresqlSchemaDiscoveryTest.
 */
public class SchemaDiscoveryTestPostgres extends PdqTest {

	/** The schema. */
	private Schema schema = null;
	
	/**
	 * The Class Relation.
	 */
	private static class TmpRelation {
		
		/** The attributes. */
		String[] attributes;
		
		/** The bindings. */
		AccessMethodDescriptor bindings;
	}
	
	/** The relations. */
	private Map<String, TmpRelation> relations = new LinkedHashMap<>();
	
	/** The relation names. */
	private String[] relationNames = new String[] {"customer", "lineitem", "nation", "orders",
					"part", "partsupp", "region", "supplier",
					"order_customer", "order_supplier", "region_nation"};
	
	/** The binding positions. */
	private Integer[][] bindingPositions = new Integer[][] {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}};
	
	/** The attributes names. */
	private String[][] attributesNames = new String[][] {
					{"c_custkey", "c_name", "c_address", "c_nationkey", "c_phone", "c_acctbal", "c_mktsegment", "c_comment"},
					{"l_orderkey", "l_partkey", "l_suppkey", "l_linenumber", "l_quantity", "l_extendedprice", "l_discount", "l_tax", "l_returnflag", "l_linestatus", "l_shipdate", "l_commitdate", "l_receiptdate", "l_shipinstruct", "l_shipmode", "l_comment"},
					{"n_nationkey", "n_name", "n_regionkey", "n_comment"},
					{"o_orderkey", "o_custkey", "o_orderstatus", "o_totalprice", "o_orderdate", "o_orderpriority", "o_clerk", "o_shippriority", "o_comment"},
					{"p_partkey", "p_name", "p_mfgr", "p_brand", "p_type", "p_size", "p_container", "p_retailprice", "p_comment"},
					{"ps_partkey", "ps_suppkey", "ps_availqty", "ps_supplycost", "ps_comment"},
					{"r_regionkey", "r_name", "r_comment"},
					{"s_suppkey", "s_name", "s_address", "s_nationkey",  "s_phone", "s_acctbal", "s_comment"},
					{"cname", "caddress", "cnation", "cactbal", "opriority", "oclerk", "pname", "pbrand", "ptype", "lextendedprice", "ldiscount", "ltax", "lflag"},
					{"sname", "saddress", "snation", "sactbal", "opriority", "oclerk", "pname", "pbrand", "ptype", "lextendedprice", "ldiscount", "ltax", "lflag"},
					{"nation_key", "nation_name", "region_key", "region_name"}
			};
	
	/**
	 * Instantiates a new postgresql schema discovery test.
	 *
	 * @throws BuilderException the builder exception
	 */
	public SchemaDiscoveryTestPostgres() throws BuilderException {
		Properties properties = new Properties();
		properties.put("url", "jdbc:postgresql://localhost/");
		properties.put("database", "tpch");
		properties.put("username", "postgres");
		properties.put("username", "postgres");
		properties.put("password", "postgres");
		properties.put("driver","org.postgresql.Driver");		
		int i = 0;
		for(String n: this.relationNames) {
			TmpRelation r = new TmpRelation();
			r.attributes = this.attributesNames[i];
			r.bindings = new AccessMethodDescriptor(this.bindingPositions[i]);
			this.relations.put(n, r);
			i++;
		}
		PostgresqlSchemaDiscoverer disco = new PostgresqlSchemaDiscoverer();
		disco.setProperties(properties);
		this.schema = disco.discover();
	}
	
	/**
	 * Make attributes.
	 *
	 * @param attributeNames the attribute names
	 * @return the list
	 */
	private Attribute[] makeAttributes(String[] attributeNames) {
		Attribute[] result = new Attribute[attributeNames.length];
		for (int index = 0; index < attributeNames.length; ++index) 
			result[index] = Attribute.create(String.class, attributeNames[index]);
		return result;
	}
	
	/**
	 * Makes sure assertions are enabled.
	 */
	@Before 
	public void setup() {
		PdqTest.assertsEnabled();
	}
	
	@Test
	public void testFactsReading() {
		Properties properties = new Properties();
		properties.put("url", "jdbc:postgresql://localhost/");
		properties.put("database", "tpch");
		properties.put("username", "postgres");
		properties.put("username", "postgres");
		properties.put("password", "postgres");
		properties.put("driver","org.postgresql.Driver");		
		int i = 0;
		for(String n: this.relationNames) {
			TmpRelation r = new TmpRelation();
			r.attributes = this.attributesNames[i];
			r.bindings = new AccessMethodDescriptor(this.bindingPositions[i]);
			this.relations.put(n, r);
			i++;
		}
		PostgresqlSchemaDiscoverer disco = new PostgresqlSchemaDiscoverer();
		disco.setProperties(properties);
		disco.discover();
		try {
			String[] relations = new String[] {"nation","region","supplier"};
			
			Collection<Atom> discoveredFacts = disco.discoverRelationFacts(Arrays.asList(relations));
			
			Assert.assertEquals(0,getNumberOfFactsForRelation(discoveredFacts,"customer"));
			Assert.assertEquals(25,getNumberOfFactsForRelation(discoveredFacts,"nation"));
			Assert.assertEquals(5,getNumberOfFactsForRelation(discoveredFacts,"region"));
			Assert.assertEquals(10000,getNumberOfFactsForRelation(discoveredFacts,"supplier"));
		} catch (SQLException | DatabaseException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	private int getNumberOfFactsForRelation(Collection<Atom> discoveredFacts, String predicateName) {
		int number = 0;
		for (Atom a: discoveredFacts) if (a.getPredicate().getName().equalsIgnoreCase(predicateName)) number++;
		return number;
	}

	@Test
	public void testCardinalities() {
		Properties properties = new Properties();
		properties.put("url", "jdbc:postgresql://localhost/");
		properties.put("database", "tpch");
//		properties.put("username", "postgres");
		properties.put("username", "postgres");
		properties.put("password", "postgres");
		properties.put("driver","org.postgresql.Driver");		
		int i = 0;
		for(String n: this.relationNames) {
			TmpRelation r = new TmpRelation();
			r.attributes = this.attributesNames[i];
			r.bindings = new AccessMethodDescriptor(this.bindingPositions[i]);
			this.relations.put(n, r);
			i++;
		}
		PostgresqlSchemaDiscoverer disco = new PostgresqlSchemaDiscoverer();
		disco.setProperties(properties);
		Schema schema = disco.discover();
		try {
			Map<Relation, Integer> cardinalityMap = disco.getRelationCardinalities();
			Assert.assertEquals(150000,(int)cardinalityMap.get(schema.getRelation("customer")));
			Assert.assertEquals(25,(int)cardinalityMap.get(schema.getRelation("nation")));
			Assert.assertEquals(800000,(int)cardinalityMap.get(schema.getRelation("partsupp")));
			Assert.assertEquals(5,(int)cardinalityMap.get(schema.getRelation("region")));
			Assert.assertEquals(1500000,(int)cardinalityMap.get(schema.getRelation("orders")));
			Assert.assertEquals(10000,(int)cardinalityMap.get(schema.getRelation("supplier")));
			Assert.assertEquals(6001215,(int)cardinalityMap.get(schema.getRelation("lineitem")));
			Assert.assertEquals(200000,(int)cardinalityMap.get(schema.getRelation("part")));
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}

	/**
	 * Test parse view defintion.
	 */
	@Test
	public void testParseViewDefintion() {
		Properties properties = new Properties();
		properties.put("url", "jdbc:postgresql://localhost/");
		properties.put("database", "tpch_0001");
		properties.put("username", "postgres");
		properties.put("password", "postgres");
		Map<String, Relation> map = new LinkedHashMap<>();
		map.put("customer", Relation.create("customer", this.makeAttributes(this.attributesNames[0])));
		map.put("lineitem", Relation.create("lineitem", this.makeAttributes(this.attributesNames[1])));
		map.put("orders", Relation.create("orders", this.makeAttributes(this.attributesNames[3])));
		map.put("part", Relation.create("part", this.makeAttributes(this.attributesNames[4])));
		PostgresqlSchemaDiscoverer disco = new PostgresqlSchemaDiscoverer();
		disco.setProperties(properties);
		disco.parseViewDefinition(
				"order_supplier",
				"SELECT \n" +
				"c.c_name AS cname, \n" +
				"    c.c_address AS caddress, \n" +
				"    c.c_nationkey AS cnation, \n" +
				"    c.c_acctbal AS cactbal, \n" +
				"    o.o_orderpriority AS opriority, \n" +
				"    o.o_clerk AS oclerk, \n" +
				"    p.p_name AS pname, \n" +
				"    p.p_brand AS pbrand, \n" +
				"    p.p_type AS ptype, \n" +
				"    l.l_extendedprice AS lextendedprice, \n" +
				"    l.l_discount AS ldiscount, \n" +
				"    l.l_tax AS ltax, \n" +
				"    l.l_returnflag AS lflag \n" +
				"   FROM customer c, \n" +
				"    orders o, \n" +
				"    lineitem l, \n" +
				"    part p \n" +
				"  WHERE (((o.o_orderkey = l.l_orderkey) AND (o.o_custkey = c.c_custkey)) AND \n" +
				"(l.l_partkey = p.p_partkey)); \n",
				map);
	}
	
	/**
	 * Test number of relations.
	 */
	@Test
	public void testNumberOfRelations() {
	//	Assert.assertTrue(this.schema.getNumberOfRelations() > this.relationNames.length);
	}
	
	/**
	 * Test relation names.
	 */
	@Test
	public void testRelationNames() {
		for (uk.ac.ox.cs.pdq.db.Relation r: this.schema.getRelations()) {
			assertTrue(r.getName() + " not present.", this.relations.containsKey(r.getName()));
		}
	}
	
	/**
	 * Test relation arities.
	 */
	@Test
	public void testRelationArities() {
		for (uk.ac.ox.cs.pdq.db.Relation r: this.schema.getRelations()) {
			assertEquals((Integer) r.getArity(), (Integer) this.relations.get(r.getName()).attributes.length);
		}
	}
	
	/**
	 * Test attribute names.
	 */
	@Test
	public void testAttributeNames() {
		for (uk.ac.ox.cs.pdq.db.Relation r: this.schema.getRelations()) {
			int j = 0;
			for (Attribute a: r.getAttributes()) {
				assertEquals(a.getName(),  this.relations.get(r.getName()).attributes[j++]);
			}
		}
	}

	/**
	 * Test access method methods.
	 */
	@Test
	public void testAccessMethodMethods() {
		for (uk.ac.ox.cs.pdq.db.Relation r: this.schema.getRelations()) {
			for (AccessMethodDescriptor b: r.getAccessMethods()) 
				Assert.assertArrayEquals(b.getInputs(), this.relations.get(r.getName()).bindings.getInputs());
		}
	}

}
