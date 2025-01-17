// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.cost.estimators;

/**
 * Top level interface for all Order Dependent cost estimators.
 * 	The cost of a plan depends on the operators' order
 * In some of the planning algorithms, there will be a common way to propagate costs for all orderdependent estimators
 * and another common way for all order independent estimators
 * @author Julien Leblay
 * @author Efthymia Tsamoura
 */
public interface OrderDependentCostEstimator extends CostEstimator {

	/**
	 * 
	 *
	 * @return CostEstimator<P>
	 */
	OrderDependentCostEstimator clone();
	
}
