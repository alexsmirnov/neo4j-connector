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
package com.netoprise.neo4j;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.TransactionSupport;
import javax.security.auth.Subject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4JConnectionFactoryImpl;
import com.netoprise.neo4j.connection.Neo4JConnectionImpl;
import com.netoprise.neo4j.transaction.PlatformTransactionProvider;

/**
 * Neo4jManagedConnectionFactory
 * 
 * @version $Revision: $
 */
@ConnectionDefinition(connectionFactory = Neo4JConnectionFactory.class, connectionFactoryImpl = Neo4JConnectionFactoryImpl.class, connection = GraphDatabaseService.class, connectionImpl = Neo4JConnectionImpl.class)
public class Neo4jManagedConnectionFactory implements ManagedConnectionFactory,
		ResourceAdapterAssociation, TransactionSupport {

	/** The serial version UID */
	private static final long serialVersionUID = 1L;


	/** The resource adapter */
	private Neo4jResourceAdapter ra;

	/** The logwriter */
	private PrintWriter logwriter;

	private GraphDatabaseService database;

	private int connectionsCreated = 0;

	@ConfigProperty
	private String neo4jConfig;
	@ConfigProperty
	private String dir;
	@ConfigProperty
	private boolean xa;

	/**
	 * Default constructor
	 */
	public Neo4jManagedConnectionFactory() {
		this.logwriter = new PrintWriter(System.out);
	}

	public String getDir() {
		if (null == dir) {
			return ra.getDir();
		}
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isXa() {
		return xa || ra.isXa();
	}

	public void setXa(boolean xa) {
		this.xa = xa;
	}

	/**
	 * Creates a Connection Factory instance.
	 * 
	 * @param cxManager
	 *            ConnectionManager to be associated with created EIS connection
	 *            factory instance
	 * @return EIS-specific Connection Factory instance or
	 *         javax.resource.cci.ConnectionFactory instance
	 * @throws ResourceException
	 *             Generic exception
	 */
	public Object createConnectionFactory(ConnectionManager cxManager)
			throws ResourceException {
		logwriter.append("createConnectionFactory()");
		return new Neo4JConnectionFactoryImpl(this, cxManager);
	}

	/**
	 * Creates a Connection Factory instance.
	 * 
	 * @return EIS-specific Connection Factory instance or
	 *         javax.resource.cci.ConnectionFactory instance
	 * @throws ResourceException
	 *             Generic exception
	 */
	public Object createConnectionFactory() throws ResourceException {
		throw new ResourceException(
				"This resource adapter doesn't support non-managed environments");
	}

	/**
	 * Creates a new physical connection to the underlying EIS resource manager.
	 * 
	 * @param subject
	 *            Caller's security information
	 * @param cxRequestInfo
	 *            Additional resource adapter specific connection request
	 *            information
	 * @throws ResourceException
	 *             generic exception
	 * @return ManagedConnection instance
	 */
	public ManagedConnection createManagedConnection(Subject subject,
			ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		logwriter.append("createManagedConnection()");
//		createDatabase();
		connectionsCreated++;
		return new Neo4jManagedConnection(this);
	}

	/**
	 * Returns a matched connection from the candidate set of connections.
	 * 
	 * @param connectionSet
	 *            Candidate connection set
	 * @param subject
	 *            Caller's security information
	 * @param cxRequestInfo
	 *            Additional resource adapter specific connection request
	 *            information
	 * @throws ResourceException
	 *             generic exception
	 * @return ManagedConnection if resource adapter finds an acceptable match
	 *         otherwise null
	 */
	public ManagedConnection matchManagedConnections(Set connectionSet,
			Subject subject, ConnectionRequestInfo cxRequestInfo)
			throws ResourceException {
		logwriter.append("matchManagedConnections()");
		ManagedConnection result = null;
		Iterator it = connectionSet.iterator();
		while (result == null && it.hasNext()) {
			ManagedConnection mc = (ManagedConnection) it.next();
			if (mc instanceof Neo4jManagedConnection) {
				result = mc;
			}

		}
		return result;
	}

	/**
	 * Get the log writer for this ManagedConnectionFactory instance.
	 * 
	 * @return PrintWriter
	 * @throws ResourceException
	 *             generic exception
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		logwriter.append("getLogWriter()");
		return logwriter;
	}

	/**
	 * Set the log writer for this ManagedConnectionFactory instance.
	 * 
	 * @param out
	 *            PrintWriter - an out stream for error logging and tracing
	 * @throws ResourceException
	 *             generic exception
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		logwriter.append("setLogWriter()");
		logwriter = out;
	}

	/**
	 * Get the resource adapter
	 * 
	 * @return The handle
	 */
	public Neo4jResourceAdapter getResourceAdapter() {
		logwriter.append("getResourceAdapter()");
		return ra;
	}

	/**
	 * Set the resource adapter
	 * 
	 * @param ra
	 *            The handle
	 */
	public void setResourceAdapter(ResourceAdapter ra) {
		logwriter.append("setResourceAdapter()");
		this.ra = (Neo4jResourceAdapter) ra;
		this.ra.addFactory(this);
	}

	public void destroyManagedConnection(Neo4jManagedConnection connection) {
		connectionsCreated--;
		if (connectionsCreated <= 0) {
//			shutdownDatabase();
		}
	}

	public void start() {
		createDatabase();
	}

	public void stop() {
		shutdownDatabase();
	}

	private void shutdownDatabase() {
		if (null != database) {
			database.shutdown();
			database = null;
		}
	}

	private void createDatabase() {
		if (null == database) {
			Map<String, String> config = new HashMap<String, String>();
			// Do some double split
			if(neo4jConfig!=null) {
				String[] parameterPairs = neo4jConfig.split(";");
				for(String pair : parameterPairs) {
					int equalsPos = pair.indexOf('=');
					String key = pair.substring(0, equalsPos);
					String value = pair.substring(equalsPos+1);
					config.put(key, value);
				}
			}
			// XA config is always done after manual parameter passing to override it
			if (isXa()) {
				config.put(Config.TXMANAGER_IMPLEMENTATION,
						PlatformTransactionProvider.JEE_JTA);
			}
			database = new EmbeddedGraphDatabase(getDir(), config);
		}
	}

	/**
	 * @return the database
	 */
	public GraphDatabaseService getDatabase() {
		return database;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		return result;
	}

	/**
	 * Indicates whether some other object is equal to this one.
	 * 
	 * @param other
	 *            The reference object with which to compare.
	 * @return true if this object is the same as the obj argument, false
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} else if (other == this) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TransactionSupportLevel getTransactionSupport() {
		if (isXa()) {
			return TransactionSupportLevel.XATransaction;
		} else {
			return TransactionSupportLevel.LocalTransaction;
		}
	}

}
