package org.netoprise.neo4j;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.resource.ResourceException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;


@Stateless
@LocalBean
@Resource(mappedName=Neo4jClient.NEO4J_NAME,name="Neo4j",type=Neo4JConnectionFactory.class)
public class Neo4jClient {
	
	public static final String NEO4J_NAME = "java:/eis/Neo4j";
	
	@Resource(mappedName=NEO4J_NAME)
	private Neo4JConnectionFactory connectionFactory;

	
	public Neo4JConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}


	public String sayHello(String who) throws ResourceException{
		GraphDatabaseService connection = connectionFactory.getConnection();
		Node referenceNode = connection.getReferenceNode();
		return "Hello "+who;
	}

}
