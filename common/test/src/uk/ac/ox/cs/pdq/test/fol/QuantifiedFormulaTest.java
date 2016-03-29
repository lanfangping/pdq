package uk.ac.ox.cs.pdq.test.fol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ox.cs.pdq.db.TypedConstant;
import uk.ac.ox.cs.pdq.fol.Constant;
import uk.ac.ox.cs.pdq.fol.Negation;
import uk.ac.ox.cs.pdq.fol.Atom;
import uk.ac.ox.cs.pdq.fol.QuantifiedFormula;
import uk.ac.ox.cs.pdq.fol.QuantifiedFormula.ExistentiallyQuantifiedFormula;
import uk.ac.ox.cs.pdq.fol.QuantifiedFormula.UniversallyQuantifiedFormula;
import uk.ac.ox.cs.pdq.util.Utility;
import uk.ac.ox.cs.pdq.fol.Predicate;
import uk.ac.ox.cs.pdq.fol.Skolem;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.fol.Variable;

import com.google.common.collect.Lists;

// TODO: Auto-generated Javadoc
/**
 * The Class QuantifiedFormulaTest.
 */
public class QuantifiedFormulaTest {
	
	/**
	 * Makes sure assertions are enabled.
	 */
	@Before 
	public void setup() {
		Utility.assertsEnabled();
	}

	/**
	 * Test universal.
	 */
	@Test public void testUniversal() {
		Predicate s = new Predicate("s", 5);
		List<Term> t = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p = new Atom( s, t);
		UniversallyQuantifiedFormula<Atom> n =
				QuantifiedFormula.forAll(
						Lists.newArrayList(new Variable("x1")), p);
		Assert.assertEquals("Universal subformulation must match that of construction ", p, n.getChild());
	}
	
	/**
	 * Test equals universal.
	 */
	@Test public void testEqualsUniversal() {
		Predicate s1 = new Predicate("s", 5);
		List<Term> t1 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p1 = new Atom(s1, t1);
		Predicate s2 = new Predicate("s", 5);
		List<Term> t2 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p2 = new Atom(s2, t2);
		List<Variable> v1 = Lists.newArrayList(new Variable("x1"));
		List<Variable> v2 = Lists.newArrayList(new Variable("x1"));
		UniversallyQuantifiedFormula<Atom> n1 = QuantifiedFormula.forAll(v1, p1);
		UniversallyQuantifiedFormula<Atom> n2 = QuantifiedFormula.forAll(v2, p2);
		Assert.assertTrue("Universal subformulation must match that of construction ", n1.equals(n2));
	}

	/**
	 * Test not equals universal.
	 */
	@Test public void testNotEqualsUniversal() {
		Predicate s1 = new Predicate("s", 5);
		List<Term> t1 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p1 = new Atom(s1, t1);
		Predicate s2 = new Predicate("s", 5);
		List<Term> t2 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("y")
				);
		Atom p2 = new Atom(s2, t2);
		Negation<Atom> n1 = Negation.of(p1);
		Negation<Atom> n2 = Negation.of(p2);
		Assert.assertFalse("Universal subformulation must match that of construction ", n1.equals(n2));
	}

	/**
	 * Test ground universal.
	 */
	@Test public void testGroundUniversal() {
		Predicate s = new Predicate("s", 5);
		List<Term> t = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Skolem("x3"),
				new Variable("x4"), 
				new TypedConstant<>("x5")
				);
		List<Term> g = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new TypedConstant<>("c2"), 
				new Skolem("x3"),
				new TypedConstant<>("c4"), 
				new TypedConstant<>("x5")
				);
		Map<Variable, Constant> m = new LinkedHashMap<>();
		m.put(new Variable("x2"), new TypedConstant<>("c2"));
		m.put(new Variable("x4"), new TypedConstant<>("c4"));
		Atom p = new Atom(s, t);
		List<Variable> v = Lists.newArrayList(new Variable("x1"));
		UniversallyQuantifiedFormula<Atom> n = QuantifiedFormula.forAll(v, p);
		Assert.assertEquals("Grounded universal must comply to mapping ", g, n.ground(m).getTerms());
	}

	/**
	 * Test ground universal invalid.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGroundUniversalInvalid() {
		Predicate s = new Predicate("s", 5);
		List<Term> t = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Skolem("x3"),
				new Variable("x4"), 
				new TypedConstant<>("x5")
				);
		List<Term> g = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new TypedConstant<>("c2"), 
				new Skolem("x3"),
				new TypedConstant<>("c4"), 
				new TypedConstant<>("x5")
				);
		Map<Variable, Constant> m = new LinkedHashMap<>();
		m.put(new Variable("x1"), new TypedConstant<>("c1"));
		m.put(new Variable("x2"), new TypedConstant<>("c2"));
		m.put(new Variable("x4"), new TypedConstant<>("c4"));
		Atom p = new Atom(s, t);
		List<Variable> v = Lists.newArrayList(new Variable("x1"));
		UniversallyQuantifiedFormula<Atom> n = QuantifiedFormula.forAll(v, p);
		Assert.assertEquals("Grounded universal must comply to mapping ", g, n.ground(m).getTerms());
	}

	/**
	 * Test existential.
	 */
	@Test public void testExistential() {
		Predicate s = new Predicate("s", 5);
		List<Term> t = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p = new Atom( s, t);
		ExistentiallyQuantifiedFormula<Atom> n =
				QuantifiedFormula.thereExists(
						Lists.newArrayList(new Variable("x1")), p);
		Assert.assertEquals("Universal subformulation must match that of construction ", p, n.getChild());
	}
	
	/**
	 * Test equals existential.
	 */
	@Test public void testEqualsExistential() {
		Predicate s1 = new Predicate("s", 5);
		List<Term> t1 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p1 = new Atom(s1, t1);
		Predicate s2 = new Predicate("s", 5);
		List<Term> t2 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p2 = new Atom(s2, t2);
		List<Variable> v1 = Lists.newArrayList(new Variable("x1"));
		List<Variable> v2 = Lists.newArrayList(new Variable("x1"));
		ExistentiallyQuantifiedFormula<Atom> n1 = QuantifiedFormula.thereExists(v1, p1);
		ExistentiallyQuantifiedFormula<Atom> n2 = QuantifiedFormula.thereExists(v2, p2);
		Assert.assertTrue("Universal subformulation must match that of construction ", n1.equals(n2));
	}

	/**
	 * Test not equals existential.
	 */
	@Test public void testNotEqualsExistential() {
		Predicate s1 = new Predicate("s", 5);
		List<Term> t1 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("x5")
				);
		Atom p1 = new Atom(s1, t1);
		Predicate s2 = new Predicate("s", 5);
		List<Term> t2 = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Variable("x3"),
				new Skolem("x4"), 
				new TypedConstant<>("y")
				);
		Atom p2 = new Atom(s2, t2);
		List<Variable> v1 = Lists.newArrayList(new Variable("x1"));
		List<Variable> v2 = Lists.newArrayList(new Variable("x1"));
		ExistentiallyQuantifiedFormula<Atom> n1 = QuantifiedFormula.thereExists(v1, p1);
		ExistentiallyQuantifiedFormula<Atom> n2 = QuantifiedFormula.thereExists(v2, p2);
		Assert.assertFalse("Universal subformulation must match that of construction ", n1.equals(n2));
	}

	/**
	 * Test ground existential invalid.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGroundExistentialInvalid() {
		Predicate s = new Predicate("s", 5);
		List<Term> t = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Skolem("x3"),
				new Variable("x4"), 
				new TypedConstant<>("x5")
				);
		List<Term> g = Lists.<Term>newArrayList(
				new TypedConstant<>("c1"), 
				new TypedConstant<>("c2"), 
				new Skolem("x3"),
				new TypedConstant<>("c4"), 
				new TypedConstant<>("x5")
				);
		Map<Variable, Constant> m = new LinkedHashMap<>();
		m.put(new Variable("x1"), new TypedConstant<>("c1"));
		m.put(new Variable("x2"), new TypedConstant<>("c2"));
		m.put(new Variable("x4"), new TypedConstant<>("c4"));
		Atom p = new Atom(s, t);
		List<Variable> v = Lists.newArrayList(new Variable("x1"));
		ExistentiallyQuantifiedFormula<Atom> n = QuantifiedFormula.thereExists(v, p);
		Assert.assertEquals("Grounded universal must comply to mapping ", g, n.ground(m).getTerms());
	}

	/**
	 * Test ground existential.
	 */
	@Test public void testGroundExistential() {
		Predicate s = new Predicate("s", 5);
		List<Term> t = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new Variable("x2"), 
				new Skolem("x3"),
				new Variable("x4"), 
				new TypedConstant<>("x5")
				);
		List<Term> g = Lists.<Term>newArrayList(
				new Variable("x1"), 
				new TypedConstant<>("c2"), 
				new Skolem("x3"),
				new TypedConstant<>("c4"), 
				new TypedConstant<>("x5")
				);
		Map<Variable, Constant> m = new LinkedHashMap<>();
		m.put(new Variable("x2"), new TypedConstant<>("c2"));
		m.put(new Variable("x4"), new TypedConstant<>("c4"));
		Atom p = new Atom(s, t);
		List<Variable> v = Lists.newArrayList(new Variable("x1"));
		ExistentiallyQuantifiedFormula<Atom> n = QuantifiedFormula.thereExists(v, p);
		Assert.assertEquals("Grounded universal must comply to mapping ", g, n.ground(m).getTerms());
	}
}
