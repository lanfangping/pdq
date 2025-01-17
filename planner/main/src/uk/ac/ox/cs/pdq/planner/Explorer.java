// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.planner;


import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;

import uk.ac.ox.cs.pdq.algebra.RelationalTerm;
import uk.ac.ox.cs.pdq.cost.Cost;
import uk.ac.ox.cs.pdq.exceptions.LimitReachedException;
import uk.ac.ox.cs.pdq.exceptions.LimitReachedException.Reasons;

/**
 * Searches for a feasible plan w.r.t. the relations' bindings and the schema dependencies.
 *
 * @author Efthymia Tsamoura
 * @author Stefano
 */
public abstract class Explorer {

	/**  */
	protected static Logger log = Logger.getLogger(Explorer.class);

	/**  The best plan found this far. */
	protected RelationalTerm bestPlan = null;

	/** If we want to find the best plan or not */
	protected Boolean findBestPlan = true;
	
	/**  The cost of the best plan found this far. */
	protected Cost bestCost = null;
	
	/**  If true then the explorer must terminate immediately. */
	protected boolean forcedTermination = false;

	/**  Exploration time elapsed so far (in nanoseconds). */
	protected double elapsedTime = 0;

	/**  Clock mark to computer the elapsed time. */
	protected double tick;

	/**  Number of iterations. */
	protected int rounds = 0;

	/**  Maximum time the explorer could search for a plan (in milliseconds). */
	protected boolean exceptionOnLimit = false;

	/**  Maximum time the explorer could search for a plan (in milliseconds). */
	protected double maxElapsedTime = Double.POSITIVE_INFINITY;

	/**  Maximum number of iterations the explorer could search for a plan. */
	protected double maxRounds = Double.POSITIVE_INFINITY;

	/**  Event bus shared across explorer elements. */
	protected final EventBus eventBus;

	public Explorer(EventBus eventBus) {
		assert (eventBus != null);
		this.eventBus = eventBus;
	}

	/**
	 * Explores the search space until termination:
	 * 		 -the maximum elapsed time/the maximum number of iterations has reached or
	 * 		 -the best plan is found.
	 *
	 * @throws PlannerException the planner exception
	 * @throws LimitReachedException the limit reached exception
	 */
	public void explore() throws PlannerException, LimitReachedException {
		this.tick = System.nanoTime();
		this.post();
		while (!this.terminates() && !this.forcedTermination) {
			if (this.checkLimitReached()) {
				this.forcedTermination = true;
				break;
			}
			this.performSingleExplorationStep();
			this.post();
		}
		this.checkLimitReached();
		this.post();
		if (foundEnoughPlans()) {
			System.out.println("Plan found!");
		}
		else if (hasTimedOut()) {
			System.out.println(this.elapsedTime/ 1e6);
			System.out.println(this.maxElapsedTime);
			System.out.println("TIMEOUT EXPIRED");
		} else if (hasReachMaxRounds()){
			System.out.println("Max Rounds Reached");
		}
	}

	public void updateClock() {
		long tack = System.nanoTime();
		this.elapsedTime += tack - this.tick;
		this.tick = tack;
	}

	protected boolean checkLimitReached() throws LimitReachedException {
		boolean hasTimedOutCache = hasTimedOut();
		if (foundEnoughPlans()) {
			return true;
		}
		if (hasTimedOutCache && this.exceptionOnLimit)
			throw new LimitReachedException("Planning timeout reached: " + (this.elapsedTime / 1e6) + ">" + this.maxElapsedTime, Reasons.TIMEOUT);
		boolean hasReachMaxRoundsCache = hasReachMaxRounds();
		if (hasReachMaxRoundsCache && this.exceptionOnLimit)
			throw new LimitReachedException("Planning max number of iterations reached: " + this.rounds + ">" + this.maxRounds, Reasons.MAX_ITERATION);
		return hasTimedOutCache || hasReachMaxRoundsCache;
	}

	private boolean hasReachMaxRounds() {
		return this.rounds > this.maxRounds;
	}
        
	/** if we do not require the best plan (parameter that can be user set) then return true if we have any plan
	*/
	private boolean foundEnoughPlans() {
		return this.bestPlan != null && !this.getFindBestPlan();
	}

	private boolean hasTimedOut() {
		this.updateClock();
		return (this.elapsedTime / 1e6) > this.maxElapsedTime;
	}

	protected void post() {
		this.updateClock();
		this.eventBus.post(this);
		RelationalTerm plan = this.getBestPlan();
		if (plan != null) 
			this.eventBus.post(plan);
	}

	/**
	 *
	 * @return true if termination is reached. For more details look at specific implementations
	 */
	protected abstract boolean terminates();

	/**
	 * Does the actual exploration. For more details look at specific implementations
	 *
	 */
	public abstract void performSingleExplorationStep() throws PlannerException, LimitReachedException;

	public RelationalTerm getBestPlan() {
		return this.bestPlan;
	}
	
	public Cost getBestCost() {
		return this.bestCost;
	}

	public void setMaxElapsedTime(double maxElapsedTime) {
		this.maxElapsedTime = maxElapsedTime;
	}

	/**
	 * Sets the max rounds.
	 *
	 * @param maxRounds Double
	 */
	public void setMaxRounds(Double maxRounds) {
		this.maxRounds = maxRounds;
	}

	public int getRounds() {
		return this.rounds;
	}

	public void setExceptionOnLimit(Boolean exceptionOnLimit) {
		this.exceptionOnLimit = exceptionOnLimit;
	}

	public boolean getExceptionOnLimit() {
		return this.exceptionOnLimit;
	}

	public double getElapsedTime() {
		return this.elapsedTime;
	}

	public boolean getFindBestPlan(){
		return this.findBestPlan;
	}

	public void setFindBestPlan(Boolean findBestPlan){
		this.findBestPlan = findBestPlan;
	}

}
