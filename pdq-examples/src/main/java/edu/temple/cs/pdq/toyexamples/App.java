package edu.temple.cs.pdq.toyexamples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.Magenta;

import com.google.common.collect.Sets;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.driver.PostgreSQLDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import uk.ac.ox.cs.pdq.db.AccessMethodDescriptor;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.exceptions.DatabaseException;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.fol.EGD;
import uk.ac.ox.cs.pdq.fol.Predicate;
import uk.ac.ox.cs.pdq.fol.TGD;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.fol.TypedConstant;
import uk.ac.ox.cs.pdq.fol.UntypedConstant;
import uk.ac.ox.cs.pdq.fol.Variable;
import uk.ac.ox.cs.pdq.reasoning.chase.ParallelChaser;
import uk.ac.ox.cs.pdq.reasoning.chase.state.DatabaseChaseInstance;
import uk.ac.ox.cs.pdq.reasoningdatabase.DatabaseManager;
import uk.ac.ox.cs.pdq.reasoningdatabase.DatabaseParameters;
import uk.ac.ox.cs.pdq.reasoningdatabase.ExternalDatabaseManager;
import uk.ac.ox.cs.pdq.reasoningdatabase.execution.ExecutorThread.DriverType;
import uk.ac.ox.cs.pdq.reasoningdatabase.monitor.DatabaseMonitor;
import uk.ac.ox.cs.pdq.util.QNames;

/**
 * Hello world!
 *
 */
public class App 
{	
	/**
	 * Pure JDBC operators for PostgreSQL
	 * @throws SQLException 
	 */
	private static void test1() throws SQLException {
		String url = "jdbc:postgresql://localhost/";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        props.setProperty("database", "pdq");
//        props.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(url, props);

//        String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
//        Connection conn = DriverManager.getConnection(url);
        
        Statement st = conn.createStatement();
        st.execute("drop table if exists mytable;");
        st.execute("create table mytable(col1 text)");
        st.close();
        
        PreparedStatement pst = conn.prepareStatement("insert into mytable values (?)");
        pst.setObject(1, "try1");
        pst.executeUpdate();
        pst.close();
        
        st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM mytable");
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getString(1));
        }
        rs.close();
        st.close();
	}
	
	/**
	 * tests the database manager creating a database for a single relation that
	 * contains string attributes. No queries, just add and get facts.
	 * 
	 * @param parameters
	 * @throws DatabaseException
	 */
	private static void simpleDatabaseCreation(DatabaseParameters parameters) throws DatabaseException {
		Attribute a_s = Attribute.create(String.class, "a");
		Attribute b_s = Attribute.create(String.class, "b");
		Attribute c_s = Attribute.create(String.class, "c");
		
		AccessMethodDescriptor method0 = AccessMethodDescriptor.create(new Integer[] {});
		AccessMethodDescriptor method2 = AccessMethodDescriptor.create(new Integer[] { 0, 1 });
		
		Relation R = Relation.create("R", new Attribute[] { a_s, b_s, c_s }, new AccessMethodDescriptor[] { method0, method2 });

		ExternalDatabaseManager manager = new ExternalDatabaseManager(parameters);
		manager.initialiseDatabaseForSchema(new Schema(new Relation[] { R }));
		
		// ADD facts
		Atom a1 = Atom.create(R, new Term[] { UntypedConstant.create("12"), UntypedConstant.create("13"), UntypedConstant.create("14") });
		Atom a2 = Atom.create(R, new Term[] { TypedConstant.create(12), TypedConstant.create(13), TypedConstant.create(14) });
		List<Atom> facts = new ArrayList<Atom>();
		facts.add(a1);
		facts.add(a2);
		manager.addFacts(facts);
		Collection<Atom> getFacts = manager.getFactsFromPhysicalDatabase();
//		Assert.assertTrue(facts.size() == getFacts.size() && facts.containsAll(getFacts));

		// Test duplicated storage - stored data should not change when we add the same
		// set twice
		try {
			System.out.println("Testing exceptions:");
			manager.addFacts(facts);
			//Assert.fail("Should have thrown error for insering duplicates");
		} catch (Exception e) {}
		System.out.println("Testing exceptions finished.");
		getFacts = manager.getFactsFromPhysicalDatabase();
		System.out.println(manager.getDatabaseName());
	//	Assert.assertEquals(facts.size(), getFacts.size());

		// DELETE
//		manager.deleteFacts(facts);
//		getFacts = manager.getFactsFromPhysicalDatabase();
////		Assert.assertNotNull(getFacts);
////		Assert.assertEquals(0, getFacts.size());
		
		manager.dropDatabase();
		manager.shutdown();
	}
	
	public static void simpleDatabaseCreationPostgres() throws DatabaseException {
		simpleDatabaseCreation(DatabaseParameters.Postgres);
	}
	
	
	private static void test2() {
		String url = "jdbc:postgresql://localhost/";
		String database = "pdq";
		String username = "postgres";
		String password = "postgres";
		String driver = "org.postgresql.Driver";
		Connection connection; 
//		driverType = DriverType.Postgres;
		
		try {
			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// ignored error.
			e.printStackTrace();
		}
		
	}
	
	private static void test3(DatabaseParameters parameters) throws DatabaseException {
		Attribute a_s = Attribute.create(String.class, "a");
		Attribute b_s = Attribute.create(String.class, "b");
		Attribute c_s = Attribute.create(String.class, "c");
		
		Relation R = Relation.create("R", new Attribute[] { a_s, b_s });
		Relation A = Relation.create("A", new Attribute[] { a_s});
		Relation relation[] = new Relation[] { R, A};
		
		// ADD facts
		Atom a1 = Atom.create(R, new Term[] { TypedConstant.create("a"), TypedConstant.create("b") });
		Atom a2 = Atom.create(R, new Term[] { TypedConstant.create("b"), TypedConstant.create("b") });
		List<Atom> facts = new ArrayList<Atom>();
		facts.add(a1);
		facts.add(a2);
		
		
		Dependency dependencies[] = new Dependency[] {
				TGD.create(
						new Atom[] { Atom.create(R, Variable.create("x1"), Variable.create("x2")) }, //body
						new Atom[] { Atom.create(R, Variable.create("x1"), Variable.create("y")),
								Atom.create(A, Variable.create("y")),
								Atom.create(A, Variable.create("x2"))})  // head
				};
		
		Schema schema = new Schema(relation, dependencies);
		
		ExternalDatabaseManager manager = new ExternalDatabaseManager(parameters);
		manager.initialiseDatabaseForSchema(schema);
		
//		manager.addFacts(facts);
//		Collection<Atom> getFacts = manager.getFactsFromPhysicalDatabase();
		
		
		/**
		 * initiate facts to database
		 * */
		DatabaseChaseInstance state = null;
		try {
			state = new DatabaseChaseInstance(facts, manager);   // add facts to database
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Schema:" + schema);
		Set<Atom> newfacts = Sets.newLinkedHashSet(state.getFacts());
		Iterator<Atom> iterator = newfacts.iterator();
		System.out.println("Initial facts:");
		
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		
		/**
		 * chasing
		 * */
		ParallelChaser chaser = new ParallelChaser();
		chaser.reasonUntilTermination(state, dependencies);
		
		newfacts = Sets.newHashSet(state.getFacts());
		iterator = newfacts.iterator();
		
		System.out.println("\nAfter reasoning:");
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		manager.dropDatabase();
		manager.shutdown();
		
		
	}
	
	private static void test4(DatabaseParameters parameters) throws DatabaseException {
		Attribute a_s = Attribute.create(String.class, "a");
		Attribute b_s = Attribute.create(String.class, "b");
		Attribute c_s = Attribute.create(String.class, "c");
		
		Relation A = Relation.create("A", new Attribute[] { a_s, b_s });
		Relation B = Relation.create("B", new Attribute[] {a_s, b_s});
		Relation relation[] = new Relation[] { A, B};
		
		// ADD facts
		Atom a1 = Atom.create(A, new Term[] { TypedConstant.create("1"), TypedConstant.create("2") });
//		Atom a2 = Atom.create(R, new Term[] { TypedConstant.create("b"), TypedConstant.create("b") });
		List<Atom> facts = new ArrayList<Atom>();
		facts.add(a1);
//		facts.add(a2);
		
		
		Dependency dependencies[] = new Dependency[] {
				TGD.create(
						new Atom[] { Atom.create(A, TypedConstant.create("1"), Variable.create("y")) }, //body
						new Atom[] { Atom.create(B, TypedConstant.create("2"), Variable.create("y"))})  // head
				};
		
		Schema schema = new Schema(relation, dependencies);
		
		ExternalDatabaseManager manager = new ExternalDatabaseManager(parameters);
		manager.initialiseDatabaseForSchema(schema);
		
//		manager.addFacts(facts);
//		Collection<Atom> getFacts = manager.getFactsFromPhysicalDatabase();
		
		
		/**
		 * initiate facts to database
		 * */
		DatabaseChaseInstance state = null;
		try {
			state = new DatabaseChaseInstance(facts, manager);   // add facts to database
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Schema:" + schema);
		Set<Atom> newfacts = Sets.newLinkedHashSet(state.getFacts());
		Iterator<Atom> iterator = newfacts.iterator();
		System.out.println("Initial facts:");
		
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		
		/**
		 * chasing
		 * */
		ParallelChaser chaser = new ParallelChaser();
		chaser.reasonUntilTermination(state, dependencies);
		
		newfacts = Sets.newHashSet(state.getFacts());
		iterator = newfacts.iterator();
		
		System.out.println("\nAfter reasoning:");
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		manager.dropDatabase();
		manager.shutdown();
		
		
	}
	
	private static void test5(DatabaseParameters parameters) throws DatabaseException {
		Attribute a_s = Attribute.create(String.class, "a");
		Attribute b_s = Attribute.create(String.class, "b");
		Attribute c_s = Attribute.create(String.class, "c");
		
		Relation A = Relation.create("A", new Attribute[] { a_s, b_s });
		Relation B = Relation.create("B", new Attribute[] {a_s, b_s});
		Relation relation[] = new Relation[] { A, B};
		
		// ADD facts
		Atom a1 = Atom.create(A, new Term[] { TypedConstant.create("1"), TypedConstant.create("2") });
//		Atom a2 = Atom.create(R, new Term[] { TypedConstant.create("b"), TypedConstant.create("b") });
		List<Atom> facts = new ArrayList<Atom>();
		facts.add(a1);
//		facts.add(a2);
		
		
		Dependency dependencies[] = new Dependency[] {
				TGD.create(
						new Atom[] { Atom.create(A, Variable.create("x"), Variable.create("y")) }, //body
						new Atom[] { Atom.create(A, TypedConstant.create("2"), Variable.create("y"))})  // head
				};
		
		Schema schema = new Schema(relation, dependencies);
		
		ExternalDatabaseManager manager = new ExternalDatabaseManager(parameters);
		manager.initialiseDatabaseForSchema(schema);
		
//		manager.addFacts(facts);
//		Collection<Atom> getFacts = manager.getFactsFromPhysicalDatabase();
		
		
		/**
		 * initiate facts to database
		 * */
		DatabaseChaseInstance state = null;
		try {
			state = new DatabaseChaseInstance(facts, manager);   // add facts to database
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Schema:" + schema);
		Set<Atom> newfacts = Sets.newLinkedHashSet(state.getFacts());
		Iterator<Atom> iterator = newfacts.iterator();
		System.out.println("Initial facts:");
		
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		
		/**
		 * chasing
		 * */
		ParallelChaser chaser = new ParallelChaser();
		chaser.reasonUntilTermination(state, dependencies);
		
		newfacts = Sets.newHashSet(state.getFacts());
		iterator = newfacts.iterator();
		
		System.out.println("\nAfter reasoning:");
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		manager.dropDatabase();
		manager.shutdown();
		
		
	}
	
	public static void test() {
		
	}
	
    public static void main( String[] args ) throws SQLException, DatabaseException
    {
		System.out.println( "Hello World!" );
        
//        simpleDatabaseCreationPostgres();
        
//        test3(DatabaseParameters.Postgres);
//        test4(DatabaseParameters.Postgres);
        test5(DatabaseParameters.Postgres);
    }
}
