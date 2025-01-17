// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.test.reasoning.chase.state;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import com.google.common.collect.Sets;

import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Match;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.exceptions.DatabaseException;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.fol.Constant;
import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.fol.EGD;
import uk.ac.ox.cs.pdq.fol.Predicate;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.fol.UntypedConstant;
import uk.ac.ox.cs.pdq.fol.Variable;
import uk.ac.ox.cs.pdq.reasoning.chase.state.DatabaseChaseInstance;
import uk.ac.ox.cs.pdq.reasoningdatabase.DatabaseManager;
import uk.ac.ox.cs.pdq.reasoningdatabase.DatabaseParameters;
import uk.ac.ox.cs.pdq.reasoningdatabase.ExternalDatabaseManager;
import uk.ac.ox.cs.pdq.reasoningdatabase.InternalDatabaseManager;
import uk.ac.ox.cs.pdq.reasoningdatabase.LogicalDatabaseInstance;
import uk.ac.ox.cs.pdq.reasoningdatabase.cache.MultiInstanceFactCache;
import uk.ac.ox.cs.pdq.test.util.PdqTest;

/**
 * (4) Create the following unit tests for EGDchaseStep 
 * 
 * a. the facts of the chase instance are A(c_i,c_{i+1}), for i=1,...,1000 The input matches contain
 * the EGD B(x,y), B(x,y') -> y=y' and the i-th match in the input collection
 * contains the mapping {x=k1, y'=c_i, y=c_{i+1}} for i=1,...,1000 after you do
 * this operation, the database should contain only one A fact. 
 * 
 * b. the facts of the chase instance are A(c_i,c_{i+1}), for i=1,...,1000 The
 * input matches contain the EGD B(x,y), B(x,y') -> y=y' and the i-th match in
 * the input collection contains the mapping {x=k1, y'=c_i, y=c_{i+1}} for
 * i=1,...,500 after you do this operation, the database should contain 501 A
 * facts. 
 * 
 * @author Gabor
 *
 */
public class TestEGDChaseStep extends PdqTest {
	Relation A = Relation.create("A", new Attribute[] { Attribute.create(String.class, "attribute0"),Attribute.create(String.class, "attribute1")});
	Relation B = Relation.create("B", new Attribute[] { Attribute.create(String.class, "attribute0"),Attribute.create(String.class, "attribute1")});
	Relation r[] = new Relation[] { A,B };
	Schema s = new Schema(r,new Dependency[0]);

	
	void testA(DatabaseManager conn) throws SQLException, DatabaseException {
		List<Atom> facts = new ArrayList<>();
		for (int i=2; i <= 1000; i++) facts.add(Atom.create(A, new Term[]{UntypedConstant.create("c_"+(i-1)),UntypedConstant.create("c_"+i)}));
		
		DatabaseChaseInstance state = new DatabaseChaseInstance(facts, conn);
		Dependency d[] = new Dependency[] {
				EGD.create(new Atom[]{ Atom.create(B, Variable.create("x"),Variable.create("y1")),
										Atom.create(B, Variable.create("x"), Variable.create("y2"))}, 
						new Atom[] {Atom.create(Predicate.create("EQUALITY", 2,true), Variable.create("y1"), Variable.create("y2"))})
				};
		long start = System.currentTimeMillis();
		Collection<Match> matches = new HashSet<>();
		for (int i = 1; i < 1000; i++)  {
			Map<Variable, Constant> mapping = new HashMap<>();
			mapping.put(Variable.create("y1"),UntypedConstant.create("c_"+i));
			mapping.put(Variable.create("y2"),UntypedConstant.create("c_"+(i+1)));
			matches.add(Match.create(d[0], mapping));
		}
		state.EGDchaseStep(matches );
		System.out.println("1000 equalities processed in : "+ (System.currentTimeMillis()-start)/1000.0 +" sec. ");
		Set<Atom> facts2 = Sets.newHashSet(state.getFacts());
		Iterator<Atom> iterator2 = facts2.iterator();
		while(iterator2.hasNext()) {
			Atom fact = iterator2.next();
			if(fact.isEquality()) {
				iterator2.remove();
			}
		}
		Assert.assertEquals(1, facts2.size());
		try {
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	DatabaseManager getDatabaseConnectionInMem(Schema s) throws SQLException, DatabaseException{
		LogicalDatabaseInstance connection = new InternalDatabaseManager();
		connection.initialiseDatabaseForSchema(s);
		return connection;
	}

	DatabaseManager getDatabaseConnectionExternal(Schema s) throws SQLException, DatabaseException{
		LogicalDatabaseInstance connection = new LogicalDatabaseInstance(new MultiInstanceFactCache(), 
				new ExternalDatabaseManager(DatabaseParameters.Postgres),13);
		connection.initialiseDatabaseForSchema(s);
		return connection;
	}
	
	void testB(DatabaseManager conn) throws SQLException, DatabaseException {
		List<Atom> facts = new ArrayList<>();
		for (int i=2; i <= 1000; i++) facts.add(Atom.create(A, new Term[]{UntypedConstant.create("c_"+(i-1)),UntypedConstant.create("c_"+i)}));
		
		DatabaseChaseInstance state = new DatabaseChaseInstance(facts, conn);
		Dependency d[] = new Dependency[] {
				EGD.create(new Atom[]{ Atom.create(B, Variable.create("x"),Variable.create("y1")),
										Atom.create(B, Variable.create("x"), Variable.create("y2"))}, 
						new Atom[] {Atom.create(Predicate.create("EQUALITY", 2,true), Variable.create("y1"), Variable.create("y2"))})
				};
		long start = System.currentTimeMillis();
		Collection<Match> matches = new HashSet<>();
		for (int i = 1; i < 500; i++)  {
			Map<Variable, Constant> mapping = new HashMap<>();
			mapping.put(Variable.create("x"),UntypedConstant.create("k1"));
			mapping.put(Variable.create("y1"),UntypedConstant.create("c_"+i));
			mapping.put(Variable.create("y2"),UntypedConstant.create("c_"+(i+1)));
			matches.add(Match.create(d[0], mapping));
		}
		state.EGDchaseStep(matches );
		System.out.println("500 equalities processed in : "+ (System.currentTimeMillis()-start)/1000.0 +" sec.");
		Set<Atom> facts2 = Sets.newHashSet(state.getFacts());
		Iterator<Atom> iterator2 = facts2.iterator();
		while(iterator2.hasNext()) {
			Atom fact = iterator2.next();
			if(fact.isEquality()) {
				iterator2.remove();
			}
		}
		Assert.assertEquals(501, facts2.size());
		try {
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
