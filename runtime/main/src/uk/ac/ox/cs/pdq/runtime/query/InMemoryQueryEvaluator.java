package uk.ac.ox.cs.pdq.runtime.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ox.cs.pdq.algebra.predicates.AttributeEqualityPredicate;
import uk.ac.ox.cs.pdq.algebra.predicates.ConjunctivePredicate;
import uk.ac.ox.cs.pdq.algebra.predicates.ConstantEqualityPredicate;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.TypedConstant;
import uk.ac.ox.cs.pdq.db.wrappers.InMemoryTableWrapper;
import uk.ac.ox.cs.pdq.fol.ConjunctiveQuery;
import uk.ac.ox.cs.pdq.fol.Predicate;
import uk.ac.ox.cs.pdq.fol.Query;
import uk.ac.ox.cs.pdq.fol.Term;
import uk.ac.ox.cs.pdq.fol.Variable;
import uk.ac.ox.cs.pdq.runtime.EvaluationException;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.CrossProduct;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.IsEmpty;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.MemoryScan;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.Projection;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.Selection;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.SymmetricMemoryHashJoin;
import uk.ac.ox.cs.pdq.runtime.exec.iterator.TupleIterator;
import uk.ac.ox.cs.pdq.util.BooleanResult;
import uk.ac.ox.cs.pdq.util.Result;
import uk.ac.ox.cs.pdq.util.Table;
import uk.ac.ox.cs.pdq.util.Tuple;
import uk.ac.ox.cs.pdq.util.TupleType;
import uk.ac.ox.cs.pdq.util.Typed;
import uk.ac.ox.cs.pdq.util.Utility;

import com.google.common.eventbus.EventBus;

/**
 * In-memory query evaluator.
 * 
 * @author Julien Leblay
 *
 */
public class InMemoryQueryEvaluator implements QueryEvaluator {

	/** The logger */
	public static Logger log = Logger.getLogger(InMemoryQueryEvaluator.class);

	/** The evaluator's event bus */
	private EventBus eventBus;
	
	/** The query to be evaluated */
	private final Query<?> query;

	/**
	 * Constructor for InMemoryQueryEvaluator.
	 * @param q Query
	 */
	public InMemoryQueryEvaluator(Query<?> q) {
		this.query = q;
	}
	
	/**
	 * @param eventBus EventBus
	 * @see uk.ac.ox.cs.pdq.runtime.query.QueryEvaluator#setEventBus(EventBus)
	 */
	@Override
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ox.cs.pdq.benchmark.eval.QueryEvaluator#evaluate(uk.ac.ox.cs.pdq.formula.ConjunctiveQuery)
	 */
	/**
	 * @return Result
	 * @throws EvaluationException
	 * @see uk.ac.ox.cs.pdq.runtime.query.QueryEvaluator#evaluate()
	 */
	@Override
	public Result evaluate() throws EvaluationException {
		if (!(this.query instanceof ConjunctiveQuery)) {
			throw new UnsupportedOperationException("Non-conjunctive queries not yet supported.");
		}
		ConjunctiveQuery q = (ConjunctiveQuery) this.query;
		try (TupleIterator phyPlan = this.makePhysicalPlan(q)) {
			phyPlan.open();
			if (q.isBoolean()) {
				boolean result = (boolean) phyPlan.next().getValue(0);
				if (this.eventBus != null) {
					this.eventBus.post(TupleType.DefaultFactory.create(Boolean.class).createTuple(result));
				}
				return new BooleanResult(!result);
			}
			Table result = new Table(Utility.termsToAttributes(q));
			while(phyPlan.hasNext()) {
				Tuple t = phyPlan.next();
				result.appendRow(t);
				if (this.eventBus != null) {
					this.eventBus.post(t);
				}
			}
			return result;
		}
	}

	/**
	 * @param q
	 * @return a physical plan as a tuple iterator.
	 * @throws EvaluationException if the statement could not be generated.
	 */
	private TupleIterator makePhysicalPlan(ConjunctiveQuery q) throws EvaluationException {
		
		Map<Variable, Set<Predicate>> joins = new LinkedHashMap<>();
		Map<Predicate, TupleIterator> scans = new LinkedHashMap<>();
		for (Predicate p: q.getBody()) {
			scans.put(p, this.makeScans(p));
			for (Term t: p.getTerms()) {
				if (t instanceof Variable) {
					Set<Predicate> preds = joins.get(t);
					if (preds == null) {
						preds = new LinkedHashSet<>();
						joins.put((Variable) t, preds);
					}
					preds.add(p);
				}
			}
		}
		TupleIterator result = this.makeJoins(scans, new LinkedList<>(joins.values()));
		if (q.isBoolean()) {
			result = new IsEmpty(result);
		} else {
			TupleType type = Utility.getTupleType(q);
			result = new Projection(Utility.termsToTyped(q.getFree(),  type), result);
		}
		return result;
	}
	
	/**
	 * @param p PredicateFormula
	 * @return TupleIterator
	 * @throws EvaluationException
	 */
	private TupleIterator makeScans(Predicate p) throws EvaluationException {
		if (!(p.getSignature() instanceof InMemoryTableWrapper)) {
			throw new EvaluationException(
					p.getSignature().getClass().getSimpleName() +
					" relations not supported in In-Mem query evaluator.");
		}
		InMemoryTableWrapper r = (InMemoryTableWrapper) p.getSignature();
		List<Term> terms = p.getTerms();
		TupleType type = TupleType.DefaultFactory.createFromTyped(r.getAttributes());
		List<uk.ac.ox.cs.pdq.algebra.predicates.Predicate> preds = this.makeSelectionPredicates(r.getAttributes(), terms); 
		if (preds.isEmpty()) {
			return new MemoryScan(Utility.termsToTyped(terms, type), r.getData());
		}
		return new Selection(new ConjunctivePredicate<>(preds), new MemoryScan(Utility.termsToTyped(terms, type), r.getData()));
	}

	/**
	 * 
	 * @param attributes List<Attribute>
	 * @param terms List<Term>
	 * @return List<Predicate>
	 */
	private List<uk.ac.ox.cs.pdq.algebra.predicates.Predicate> makeSelectionPredicates(List<Attribute> attributes, List<Term> terms) {
		List<uk.ac.ox.cs.pdq.algebra.predicates.Predicate> result = new ArrayList<>();
		Map<Term, Integer> positions = new LinkedHashMap<>();
		int i = 0;
		for (Term t: terms) {
			if (t instanceof TypedConstant) {
				result.add(new ConstantEqualityPredicate(i, (TypedConstant) t));
			} else {
				Integer position = positions.get(t);
				if (position != null) {
					result.add(new AttributeEqualityPredicate(position, i));
				} else {
					positions.put(t, i);
				}
			}
			i++;
		}
		return result;
	}
	
	/**
	 * 
	 * @param scans
	 * @param clusters
	 * @return a join/cross product relation tree
	 * @throws TupleIteratorException
	 */
	private TupleIterator makeJoins(
			Map<Predicate, TupleIterator> scans,
			List<Set<Predicate>> clusters) {
		TupleIterator outer = null;
		Iterator<Set<Predicate>> i = Utility.connectedComponents(clusters).iterator();
		if (i.hasNext()) {
			do {
				TupleIterator inner = null;
				Set<Predicate> cluster = i.next();
				Iterator<Predicate> j = cluster.iterator();
				if (j.hasNext()) {
					do {
						Predicate predicate = j.next();
						if (inner == null) {
							inner = scans.get(predicate);
						} else {
							inner = new SymmetricMemoryHashJoin(this.makeNaturalJoinPredicate(inner, scans.get(predicate)), inner, scans.get(predicate));
						}
					} while (j.hasNext());
				}
				if (outer == null) {
					outer = inner;
				} else {
					outer = new CrossProduct(outer, inner);
				}
			} while (i.hasNext());
		}
		return outer;
	}
	
	/**
	 * @param left TupleIterator
	 * @param right TupleIterator
	 * @return ConjunctivePredicate<AttributeEqualityPredicate>
	 */
	private ConjunctivePredicate<AttributeEqualityPredicate> makeNaturalJoinPredicate(TupleIterator left, TupleIterator right) {
		Collection<AttributeEqualityPredicate> result = new ArrayList<>();
		int i = 0;
		for (Typed l: left.getColumns()) {
			int j = 0;
			for (Typed r: right.getColumns()) {
				if (l.equals(r)) {
					result.add(new AttributeEqualityPredicate(i, left.getColumns().size() + j));
				}
				j++;
			}
			i++;
		}
		return new ConjunctivePredicate<>(result);
	}
}
