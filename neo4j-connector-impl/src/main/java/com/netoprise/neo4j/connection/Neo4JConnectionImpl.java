/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.netoprise.neo4j.connection;

import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.IndexManager;

import com.netoprise.neo4j.Neo4jManagedConnection;
import com.netoprise.neo4j.Neo4jManagedConnectionFactory;

/**
 * Neo4JConnectionImpl
 * 
 * @version $Revision: $
 */
public class Neo4JConnectionImpl implements GraphDatabaseService {
	/** The logger */
	private static Logger log = Logger.getLogger("Neo4JConnectionImpl");

	/** ManagedConnection */
	private final Neo4jManagedConnection mc;

	/** ManagedConnectionFactory */
	private final Neo4jManagedConnectionFactory mcf;

	private GraphDatabaseService graphDatabase;

	/**
	 * Default constructor
	 * 
	 * @param mc
	 *            Neo4JManagedConnection
	 * @param mcf
	 *            Neo4JManagedConnectionFactory
	 */
	public Neo4JConnectionImpl(Neo4jManagedConnection mc,
			Neo4jManagedConnectionFactory mcf) {
		this.mc = mc;
		this.mcf = mcf;
		this.graphDatabase = mcf.getDatabase();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#createNode()
	 */
	@Override
	public Node createNode() {
		return this.graphDatabase.createNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getAllNodes()
	 */
	@Override
	public Iterable<Node> getAllNodes() {
		return graphDatabase.getAllNodes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getNodeById(long)
	 */
	@Override
	public Node getNodeById(long arg0) {
		return graphDatabase.getNodeById(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getReferenceNode()
	 */
	@Override
	public Node getReferenceNode() {
		return graphDatabase.getReferenceNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getRelationshipById(long)
	 */
	@Override
	public Relationship getRelationshipById(long arg0) {
		return graphDatabase.getRelationshipById(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#getRelationshipTypes()
	 */
	@Override
	public Iterable<RelationshipType> getRelationshipTypes() {
		return graphDatabase.getRelationshipTypes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neo4j.graphdb.GraphDatabaseService#index()
	 */
	@Override
	public IndexManager index() {
		return graphDatabase.index();
	}


	/**
	 * @return
	 * @see org.neo4j.graphdb.GraphDatabaseService#beginTx()
	 */
	public Transaction beginTx() {
		return graphDatabase.beginTx();
	}


	public void shutdown() {
		mc.closeHandle(this);
	}


	public <T> TransactionEventHandler<T> registerTransactionEventHandler(
			TransactionEventHandler<T> handler) {
		return graphDatabase.registerTransactionEventHandler(handler);
	}


	public <T> TransactionEventHandler<T> unregisterTransactionEventHandler(
			TransactionEventHandler<T> handler) {
		return graphDatabase.unregisterTransactionEventHandler(handler);
	}


	public KernelEventHandler registerKernelEventHandler(
			KernelEventHandler handler) {
		return graphDatabase.registerKernelEventHandler(handler);
	}


	public KernelEventHandler unregisterKernelEventHandler(
			KernelEventHandler handler) {
		return graphDatabase.unregisterKernelEventHandler(handler);
	}

}
