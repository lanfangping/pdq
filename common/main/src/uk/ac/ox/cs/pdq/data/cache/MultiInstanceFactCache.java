package uk.ac.ox.cs.pdq.data.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ox.cs.pdq.fol.Atom;

/** manages many FactCache instances.
 * @author Gabor
 *
 */
public class MultiInstanceFactCache {
	/**
	 * The actual data
	 */
	private Map<String, FactCache> multiCache;
	public MultiInstanceFactCache() {
		multiCache = new HashMap<>();
	}
	public Collection<Atom> addFacts(Collection<Atom> facts, String instanceId) {
		if (!multiCache.containsKey(instanceId)) {
			multiCache.put(instanceId,new FactCache(instanceId));
		}
		return multiCache.get(instanceId).addFacts(facts);
	}
	public Collection<Atom> getFacts(String instanceId) {
		if (!multiCache.containsKey(instanceId)) {
			multiCache.put(instanceId,new FactCache(instanceId));
		}
		return multiCache.get(instanceId).getFacts();
	}
	public boolean deleteFacts(Collection<Atom> facts, String instanceId) {
		if (!multiCache.containsKey(instanceId)) {
			multiCache.put(instanceId,new FactCache(instanceId));
		}
		return multiCache.get(instanceId).removeFacts(facts);
	}
}