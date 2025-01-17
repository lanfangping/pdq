// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.datasources;

/**
	@author Michael Benedikt 
	
This package represents interfaces for getting external data that is to be integrated.
Ways of getting data include REST services, SQL access to databases, and data available

The package includes:
--  internal representation of metadata about external data,
--  methods for calling the external sources, which are used as a base step
in the plan language
-- tools for creating these objects from XML spefications of services
-- tools for reading these datasource objects from external repositories (currently,
from SQL-based databases).

There are 3 generations of XML-based specifications, unfortunately, of which two are still supported.
	
Subpackages include:

-- the access repository, which represents a directory of services

-- schema builder, which allows one to read in external schemas (currently from a DBMS)

-- io.jaxb, for reading specifications in the current "simple web service" format

-- services, giving the java object representation of an older specification of REST services -- metadata
and methods to access the data

-- simple web service: the simplest and most recent specification for web services

-- SQL, giving the java object representations of SQL-based datasources

-- memory, giving the java object representation of access to main memory data

-- tuple and utility classes
  	
 The main top-level file in the package is  ExecutableAccessMethod.java
 This class extends the functionality of an AccessMethodDescriptor, which is an
 abstract description of an access method. The extension includes a method for
executing the access method. An ExecutableAccessMethod is described
 as a function with some input attributes, while an AccessMethodDescriptor is described
 as giving access to a relation on some input positions. It is thus
 necessary to map between attributes of the ExecutableAccessMethod
and positions of the corresponding relation, and this class includes
the necessary mapping information.



**/
