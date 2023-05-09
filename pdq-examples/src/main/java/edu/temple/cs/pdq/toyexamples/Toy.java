package edu.temple.cs.pdq.toyexamples;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.bytebuddy.asm.Advice.This;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.exceptions.DatabaseException;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.fol.EGD;
import uk.ac.ox.cs.pdq.fol.Predicate;
import uk.ac.ox.cs.pdq.fol.TGD;
import uk.ac.ox.cs.pdq.fol.TypedConstant;
import uk.ac.ox.cs.pdq.fol.Variable;
import uk.ac.ox.cs.pdq.reasoning.chase.ParallelChaser;
import uk.ac.ox.cs.pdq.reasoning.chase.state.DatabaseChaseInstance;
import uk.ac.ox.cs.pdq.reasoningdatabase.DatabaseParameters;
import uk.ac.ox.cs.pdq.reasoningdatabase.ExternalDatabaseManager;
import uk.ac.ox.cs.pdq.util.QNames;
import uk.ac.ox.cs.pdq.fol.Term;

public class Toy {
	
	
	private Relation[] relations;
	
	private List<Atom> facts;
	
	private Dependency[] dependencies; 
	
	private Schema schema;
	
	private Attribute f = Attribute.create(String.class, "f");
	private Attribute s = Attribute.create(Integer.class, "s");
	private Attribute d = Attribute.create(Integer.class, "d");
	private Attribute n = Attribute.create(Integer.class, "n");
	private Attribute x = Attribute.create(Integer.class, "x");
	
	private Variable x_f = Variable.create("x_f");
	private Variable x_f1 = Variable.create("x_f1");
	private Variable x_f2 = Variable.create("x_f2");
	private Variable x_s = Variable.create("x_s");
	private Variable x_s1 = Variable.create("x_s1");
	private Variable x_s2 = Variable.create("x_s2");
	private Variable x_d = Variable.create("x_d");
	private Variable x_n = Variable.create("x_n");
	private Variable x_x1 = Variable.create("x_x1");
	private Variable x_x2 = Variable.create("x_x2");
	
	private DatabaseParameters parameters = DatabaseParameters.Postgres;
	
	public Toy() {
		this.setRelations();
		this.setFacts();
		this.setDependencies();
		this.setSchema();
	}
	
	public Relation[] getRelations() {
		
		return relations;
	}

	public void setRelations() {
		Relation F = Relation.create("F", new Attribute[] {f, s, d, n, x});
		this.relations = new Relation[] {F};
	}

	public List<Atom> getFacts() {
		return facts;
	}

	/**
	 * Rule(destination-based): 
	 * S(x_f, x_s, x_d) :- F(x_f, x_s, x_d, x_s, 1), F(x_f, x_s, x_d, 1, 2), F(x_f, x_s, x_d, 2, x_d).
	 * 
	 * Transform the body of a Rule to a set of facts, replace x_s, x_d with 12, 13
	 * Facts:
	 * F(x_f, x_s, x_d, x_s, 1) 
	 * F(x_f, x_s, x_d, 1, 2)
	 * F(x_f, x_s, x_d, 2, x_d)
	 */
	public void setFacts() {
		Atom f1 = Atom.create(this.getRelations()[0], new Term[] {TypedConstant.create("x_f"), TypedConstant.create(12), TypedConstant.create(13), TypedConstant.create(12), TypedConstant.create(1)});
		Atom f2 = Atom.create(this.getRelations()[0], new Term[] {TypedConstant.create("x_f"), TypedConstant.create(12), TypedConstant.create(13), TypedConstant.create(1), TypedConstant.create(2)});
		Atom f3 = Atom.create(this.getRelations()[0], new Term[] {TypedConstant.create("x_f"), TypedConstant.create(12), TypedConstant.create(13), TypedConstant.create(2), TypedConstant.create(13)});
		this.facts = new ArrayList<Atom>();
		this.facts.add(f1);
		this.facts.add(f2);
		this.facts.add(f3);
	}
	
	
	

	
	public Dependency[] getDependencies() {
		return dependencies;
	}

	/**
	 * TGD: 
	 * t1: F(x_f, 11, 13, 1, 2) :- F(x_f, 12, 13, 1, 2).   // rewrite source
	 * t2: F(x_f, 11, 14, 2, 14) :- F(x_f, 11, 13, 2, 13).  // rewrite destination
	 * t3: F(x_f, x_s1, x_d, x_x1, x_x2): F(x_f, x_s1, x_d, x_n, x_x1), F(x_f, x_s2, x_d, x_x1, x_x2).  // avoid black hole
	 * 
	 * EGD:
	 * e1: x_f1/x_f2 :- F(x_f1, 12, 13, 1, 2), F(x_f2, 11, 13, 1, 2).
	 * e2: x_f1/x_f2 :- F(x_f1, 11, 13, 2, 13), F(x_f2, 11, 14, 2, 14).
	 * e3: not(x_s=12, x_d=10.4) :- F(x_f, x_s, x_d, x_n, x_x).   // firewall dependency, does not support negation in EGD
	 */
	public void setDependencies() {
		Dependency t1 = TGD.create(
				new Atom[] {
						Atom.create(this.relations[0], x_f, TypedConstant.create(12), TypedConstant.create(13), TypedConstant.create(1), TypedConstant.create(2))
				},  //body
				new Atom[] {
						Atom.create(this.relations[0], x_f, TypedConstant.create(11), TypedConstant.create(13), TypedConstant.create(1), TypedConstant.create(2))
				}   // head
				);
		
		Dependency t2 = TGD.create(
				new Atom[] {
						Atom.create(this.relations[0], x_f, TypedConstant.create(11), TypedConstant.create(13), TypedConstant.create(2), TypedConstant.create(13))
				},  //body
				new Atom[] {
						Atom.create(this.relations[0], x_f, TypedConstant.create(11), TypedConstant.create(14), TypedConstant.create(2), TypedConstant.create(14))
				}   // head
				);
		
		Dependency t3 = TGD.create(
				new Atom[] {
						Atom.create(this.relations[0], x_f, x_s1, x_d, x_n, x_x1), 
						Atom.create(this.relations[0], x_f, x_s2, x_d, x_x1, x_x2), 
				},  //body
				new Atom[] {
						Atom.create(this.relations[0], x_f, x_s1, x_d, x_x1, x_x2)
				}   // head
				);
		
		Dependency e1 = EGD.create(
				new Atom[] {
						Atom.create(this.relations[0], x_f1, TypedConstant.create(12), TypedConstant.create(13), TypedConstant.create(1), TypedConstant.create(2)),
						Atom.create(this.relations[0], x_f2, TypedConstant.create(11), TypedConstant.create(13), TypedConstant.create(1), TypedConstant.create(2))
				}, // body
				new Atom[] {
						Atom.create(Predicate.create(QNames.EQUALITY.toString(), 2, true), x_f1, x_f2)
				}  // head
				);
		
		Dependency e2 = EGD.create(
				new Atom[] {
						Atom.create(this.relations[0], x_f1, TypedConstant.create(11), TypedConstant.create(13), TypedConstant.create(2), TypedConstant.create(13)),
						Atom.create(this.relations[0], x_f2, TypedConstant.create(11), TypedConstant.create(14), TypedConstant.create(2), TypedConstant.create(14))
				}, // body
				new Atom[] {
						Atom.create(Predicate.create(QNames.EQUALITY.toString(), 2, true), x_f1, x_f2)
				}  // head
				);
		
//		Dependency e3 = EGD.create(
//				new Atom[] {}, // body
//				new Atom[] {}  // head
//				);
				
		this.dependencies = new Dependency[] {t1, e1, t2, e2, t3};
	}
	
	public Schema getSchema() {
		return schema;
	}

	/**
	 * store relations and dependencies into a schema
	 * @return
	 */
	public void setSchema() {
		Schema schema = new Schema(this.relations, this.dependencies);
		this.schema = schema;
	}

	
	public DatabaseParameters getParameters() {
		return parameters;
	}

	public static void main(String[] args) throws DatabaseException, SQLException {
		// TODO Auto-generated method stub
		Toy toy = new Toy();
		ExternalDatabaseManager manager = new ExternalDatabaseManager(toy.getParameters());
		manager.initialiseDatabaseForSchema(toy.getSchema());
		
		DatabaseChaseInstance state = new DatabaseChaseInstance(toy.getFacts(), manager);
		
		ParallelChaser chaser = new ParallelChaser();
		chaser.reasonUntilTermination(state, toy.getDependencies());
		
		Set<Atom> newfacts = Sets.newHashSet(state.getFacts());
		Iterator<Atom> iterator = newfacts.iterator();
		
		System.out.println("\nAfter reasoning:");
		while (iterator.hasNext()) {
			Atom fact = iterator.next();
			System.out.println(fact);
		}
		manager.dropDatabase();
		manager.shutdown();
	}

}
