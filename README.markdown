Neo4j Java EE connector 
=======================

The next question was: how to add Neo4j to our application and how to coordinate transactions between JPA and Neo4j ? Because we run inside Glassfish 3.1 container, we put graph database outside of application and created Java EE connector neo4j-connector that deployed to server as resource adaptor and manages Neo4j database. Application get instance of GraphDatabaseService from JNDI and doesn’t have to care about database startup/shutdown, configuration, and transactions.

Connector features
------------------

* Standard JCA 1.6 connector, that can be installed on any Java EE 6 compatible server. For the oldest servers, It’s pretty easy to convert connector to JCA 1.5 API.
* ResourceAdapter starts Neoj4 database at first request and shutdown it with server.
* Supports both LocalTransaction and XA transaction. XA support may be not quite correct – instead of using proper XAResource from adapter, it provides access to platform TransactionManager for Neo4j server. Diving into Neo4j internals, I’ve found that it creates couple of XAResource objects and enlists them in the Transaction, while ResourceAdaptor lets to create only one XAResource. Finally, I creater Provider for JEE Server TransactionManager, similar to the Spring Data project.

Usage
-----

Check out source code from the Github neo4j-connector project and build it with Maven. Project uses some artifacts from the Jboss Maven repository, so you have to configure it before build.
Deploy connector to your application server. For Glassfish 3, there is shell script that deploys resource adapter and configures connector. All what you need is having asadmin application in the path or GLASSFISH_HOME environment variable. The connector supports some configuration options :

* `dir` for the Neo4j database location 
* boolean `xa` property that switches adapter from Local to XA transactions.

Add neo4j-connector-api library to compile your application. JCA classes loaded in the parent Classloader on the server, you don’t need neo4j classes at runtime. For maven, just add dependency to ejb or war project:

	<dependency>
		<groupId>com.netoprise</groupId>
		<artifactId>neo4j-connector-api</artifactId>
		<version>0.1-SNAPSHOT</version>
		<scope>provided</scope>
	</dependency>



There you go – Neo4j GraphDatabaseService now available as JNDI resource!

	@Resource(mappedName="/eis/Neo4j") Neo4JConnectionFactory neo4jConnectionFactory;
	 
	@Produces GraphDatabaseService createDatabaseService() throws ResourceException{
		return neo4jConnectionFactory.getConnection();
	}



In the EJB services, you don’t have to start/stop transactions by hand, it will be done by container if you set apporpriate `@TransactionAttribute` for EJB/business method.
In the XA mode, you can use JPA/SQL and Neo4j together, and container will take care for data consistency ( of course, database connection also have to use XADataSource ).

Future plans
------------

* Add database configuration parameters to adapter.
* Support Neo4j High Availability cluster in the adaptor. I plan to start and configure all necessary services directly in the adapter, using Application Server cluster service where possible. Therefore, JEE application can be scaled without any changes in the code, as it supposed for JEE.
* Provide JPA style Object to Graph mapping, most likely as the CDI extension.