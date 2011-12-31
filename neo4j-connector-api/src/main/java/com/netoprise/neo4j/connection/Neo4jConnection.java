package com.netoprise.neo4j.connection;

import org.neo4j.graphdb.GraphDatabaseService;

public interface Neo4jConnection extends GraphDatabaseService {
	
	void close();

}
