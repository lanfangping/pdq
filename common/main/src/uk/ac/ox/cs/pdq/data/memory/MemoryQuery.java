package uk.ac.ox.cs.pdq.data.memory;

import uk.ac.ox.cs.pdq.data.PhysicalQuery;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.fol.ConjunctiveQuery;

/**
 * This is a query that was created from formulas such as ConjunctiveQuery or a
 * dependency activeness check, but converted to SQL language.
 * 
 * @author Gabor
 *
 */
public class MemoryQuery extends PhysicalQuery {

	/**
	 * Normal query that can be answered by a MemoryDatabaseInstance
	 * 
	 * @param source
	 */
	public MemoryQuery(ConjunctiveQuery source, Schema schema) {
		super(source, schema);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ox.cs.pdq.data.PhysicalQuery#getFormula()
	 */
	@Override
	protected ConjunctiveQuery getConjunctiveQuery() {
		return super.getConjunctiveQuery();
	}

	protected MemoryQuery getRightQuery() {
		return null;
	}
}
