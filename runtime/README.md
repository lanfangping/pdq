PDQ Runtime Library

The runtime library features packages and classes with runtime execution of 
queries and plans.
This projects also contains schema and relation wrappers, to access underlying
data sources such as RDBMSs and webservices.

The source code is available for free for non-commercial use.
See the LICENCE file for details.

I. Requirements
   
 * Java 1.7 or higher
 * Maven 2 or higher
   You can build each library outside Maven, e.g. in your favorite IDE, but
   you will have to managed dependencies manually.

II. Dependencies
 
Internal: (pdq-)common
External: jcommander-1.35
	
III. Installing & running the runtime

Under the top directory, type:

	mvn install
	
The JAR will be built and placed in the project's "target/" directory under the
name pdq-runtime-<version>.jar.

To run the runtime, type:

	java -jar /path/to/JAR/file --help
	
This will printout all required command line arguments.

Runtime parameters can be passed through the command line, however, you 
may want to set those parameters in a separate file.
See pdq-runtime.properties for an overview of all parameters that can be used.

Example:
	mvn install
	java -jar target/pdq-runtime-<version>.one-jar.jar \
			-c examples/example_01/case.properties     \
			-s examples/example_01/schema.xml          \
			-q examples/example_01/query.xml           \
			-p examples/example_01/plan.xml            \
			-d examples/example_01/data.txt            \
			-v