package uk.ac.ox.cs.pdq.datasources.services.policies;

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import uk.ac.ox.cs.pdq.datasources.legacy.services.AccessPostProcessor;
import uk.ac.ox.cs.pdq.datasources.legacy.services.AccessPreProcessor;
import uk.ac.ox.cs.pdq.datasources.legacy.services.rest.RESTRequestEvent;
import uk.ac.ox.cs.pdq.datasources.legacy.services.rest.RESTResponseEvent;
import uk.ac.ox.cs.pdq.datasources.services.servicegroup.GroupUsagePolicy;

/**
 * This class factorizes the behavior of usage policies that check if some
 * feature of a sequence of accesses exceed a certain threshold for a given 
 * time period.
 * 
 * @author Julien Leblay
 */
public abstract class PeriodicalAllowance
			implements UsagePolicy, AccessPreProcessor<RESTRequestEvent>,
					AccessPostProcessor<RESTResponseEvent> {

	public int getTotal() {
		return this.total;
	}

	private static Logger log = Logger.getLogger(PeriodicalAllowance.class);

	/**  Properties key for the limit attribute. */
	private static final String LIMIT = "limit";
	
	/**  Properties key for the period attribute. */
	private static final String PERIOD = "period";

	/**  Properties key for the wait attribute. */
	private static final String WAIT = "wait";
	
	/**  Allowance limit. */
	private final int limit;
	
	/**  Period through which the allowance applies. */
	private final long period;

	/**
	 * If true, the policy put the current on hold for the necessary amount of time when an allowance is not satisified.
	 * Otherwise, an exception is thrown in such a case.
	 */
	private final boolean wait;
	
	/**  History of the requests, mapping a timestamp to the amount collected then. */
	private TreeMap<Long, Integer> history = new TreeMap<>();
	
	/** Total number of items recorded so far. */
	private int total = 0;

	public PeriodicalAllowance(int limit, long period, boolean wait) {
		this.limit = limit;
		this.period = period;
		this.wait = wait;
	}
	
	public PeriodicalAllowance(Properties properties) {
		this(Integer.parseInt(properties.getProperty(LIMIT, "-1")),
				Periods.parse(properties.getProperty(PERIOD, "24h")),
				Boolean.parseBoolean(properties.getProperty(WAIT, "false")));
	}
	
	public PeriodicalAllowance(GroupUsagePolicy gup) {
		this(Integer.parseInt((gup.getLimit() != null) ? gup.getLimit() : "-1"),
				Periods.parse((gup.getPeriod() != null) ? gup.getPeriod() : "24h"),
				Boolean.parseBoolean((gup.getWait() != null) ? gup.getWait() : "false"));
	}
	
	

	@Override
	public void processAccessRequest(RESTRequestEvent event) throws UsagePolicyViolationException {
		this.updateHistory();
		if (this.total >= this.limit) {
			if (!this.wait) {
				throw new UsagePolicyViolationException("Exceeded periodical allowance (" + this.limit + " history, per " + this.period + " ms)");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					long waitPeriod = PeriodicalAllowance.this.getWaitPeriod();
					log.warn("Exceeded periodical allowance (" + PeriodicalAllowance.this.limit + "/" + PeriodicalAllowance.this.period + " ms), waiting for " + waitPeriod + "ms...");
					try {
						Thread.sleep(Math.max(0L, waitPeriod));
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					synchronized (PeriodicalAllowance.this) {
						PeriodicalAllowance.this.notify();
					}
				}
			}).start();
			try {
				synchronized (PeriodicalAllowance.this) {
					this.wait(this.period);
				}
			} catch (InterruptedException e) {
				log.debug(e);
				Thread.currentThread().interrupt();
			}
		}
	}
	
	protected abstract int getAmount(RESTResponseEvent event);

	@Override
	public void processAccessResponse(RESTResponseEvent event) throws UsagePolicyViolationException {
		int total = this.getAmount(event);
		this.history.put(System.currentTimeMillis(), total);
		this.total += total;
	}

	private void updateHistory() {
		Iterator<Long> i = this.history.keySet().iterator();
		Long key = null;
		while (i.hasNext()
				&& (key = i.next()) < (System.currentTimeMillis() - this.period)) {
			this.total -= this.history.get(key);
			i.remove();
		}
	}
	
	private Long getWaitPeriod() {
		int i = 0;
		long l = System.currentTimeMillis();
		Iterator<Long> li = this.history.descendingKeySet().iterator();
		while(li.hasNext() && i < this.limit) {
			l = li.next();
			i += this.history.get(l);
		}
		return this.period - (System.currentTimeMillis() - l);
	}

	public boolean isWait() {
		return this.wait;
	}

	public int getLimit() {
		return this.limit;
	}

	public long getPeriod() {
		return this.period;
	}
}