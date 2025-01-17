// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.planner;

/**
	@author Efthymia Tsamoura and Mark Ridler

	This package contains classes that initiate and start the plan exploration process.  
	The initiation process consists of the following steps:
		-selection of the appropriate reasoning tool
		-selection of the appropriate plan exploration algorithm 
		-selection of the appropriate cost function that will evaluate the performance of the created plans
		
	The explorers find plans by exploring the space of proofs.
	The proofs are built up compositionally during each step of the exploration process.
	Each proof is translated to a plan, which is later delegated to a cost estimation module.
	We assume that the cost functions are monotonic, e.g., the more accesses we add to a plan, the higher its cost becomes.  
	Two types of proofs/plans are explored: linear and bushy ones. The following types of explorers are available:
	
	-The LinearGeneric explores the space of linear proofs exhaustively. 
	-The LinearOptimized employs several heuristics to cut down the search space. 
	The first heuristic prunes the configurations that map to plans with cost >= to the best plan found so far.
	The second heuristic prunes the cost dominated configurations.
	A configuration c and c' is fact dominated by another configuration c' 
	if there exists an homomorphism from the facts of c to the facts of c' and the input constants are preserved.
	A configuration c is cost dominated by c' if it is fact dominated by c and maps to a plan with cost >= the cost of the plan of c'.
	The LinearOptimized class also employs the notion of equivalence in order not to revisit configurations already visited before.
	Both the LinearGeneric and LinearOptimized perform reasoning every time a new node is added to the plan tree. 
	-The LinearKChase class works similarly to the LinearOptimized class.
	However, it does not perform reasoning every time a new node is added to the plan tree but every k steps.  

	-The DAGGeneric explores the space of proofs exhaustively.
	-The DAGOptimized, DAGSimpleDP and DAGChaseFriendlyDP employ two DP-like heuristics to cut down the search space.
	The first heuristic prunes the configurations that map to plans with cost >= to the best plan found so far.
	The second heuristic prunes the cost dominated configurations. A configuration c and c' is fact dominated by another configuration c' 
	if there exists an homomorphism from the facts of c to the facts of c' and the input constants are preserved.
	A configuration c is cost dominated by c' if it is fact dominated by c and maps to a plan with cost >= the cost of the plan of c'.
	-The DAGOptimized employs further techniques to speed up the planning process like reasoning in parallel and re-use of reasoning results. 
	
	
	The following sub-packages are included:
	
	-- linear.* packages contain classes related to creating and exploring linear proofs
	
	-- dag.* packages contain classes related to creating and exploring dag proofs
	
	-- reasoning.* packages contain proof related classes, e.g., classes that represent the state of a chase configuration.  
	
	-- util package contains utility classes and methods.
	
	-- logging.* packages contain classes that monitor certain characteristics, like the number of rounds, 
	the planning time. 
	
	The top-level classes are as follows:
	- Explorer, which is created using the ExplorerFactory class.
	- Bootstrap, which is the main entry point for the planner package
	- ExplorationSetup, where all properties are gathered and used to
  	   create various objects like explorer,reasoner,cost estimator, etc
   	- PlannerParameters, which holds the parameters of a planning session.
   	
**/