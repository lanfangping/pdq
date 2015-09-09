package uk.ac.ox.cs.pdq.test.acceptance;

import java.io.PrintStream;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * High-level acceptance criterion.
 * 
 * @author leblay
 *
 */
public interface AcceptanceCriterion<Expected, Observed> {

	/**
	 */
	enum AcceptanceLevels { PASS, FAIL }
	
	/**
	 * The result of an acceptance criterion check. 
	 * It consists of an acceptance level and supporting information as a list 
	 * of Strings.
	 * 
	 * @author Julien Leblay
	 */
	static class AcceptanceResult {
		private final AcceptanceLevels level;
		private final List<String> supportingInfo;
		
		/**
		 * Default constructor 
		 * @param level
		 * @param supportingInfo
		 */
		AcceptanceResult(AcceptanceLevels level, String... supportingInfo) {
			this.level = level;
			this.supportingInfo = ImmutableList.copyOf(supportingInfo);
		}
		
		/**
		 * Prints a report of the acceptance result to the given output stream
		 * @param out
		 */
		public void report(PrintStream out) {
			out.println(this.level);
			for (String reason: this.supportingInfo) {
				out.println("\t" + reason);
			}
		}
		
		/**
		 * @return the acceptance level of the result
		 */
		public AcceptanceLevels getLevel() {
			return this.level;
		}
	}
	
	/**
	 * @param e expected object
	 * @param o observed object
	 * @return true, iff the observed object satisfies the acceptance criterion.
	 */
	AcceptanceResult check(Expected e, Observed o);
}
