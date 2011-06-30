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
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import com.netoprise.neo4j.connection.Neo4JConnectionImpl;

/**
 * Neo4jManagedConnection
 * 
 * @version $Revision: $
 */
public class Neo4jManagedConnection implements ManagedConnection {

	private final class Neo4jXAResource implements XAResource {

		private int timeout;

		@Override
		public void commit(Xid xid, boolean onePhase) throws XAException {
		}

		@Override
		public void end(Xid xid, int arg1) throws XAException {
		}

		@Override
		public void forget(Xid xid) throws XAException {
		}

		@Override
		public int getTransactionTimeout() throws XAException {
			return this.timeout;
		}

		@Override
		public boolean isSameRM(XAResource arg0) throws XAException {
			return this == arg0;
		}

		@Override
		public int prepare(Xid xid) throws XAException {
			return XA_OK;
		}

		@Override
		public Xid[] recover(int arg0) throws XAException {
			// two-phase commits not supported
			return new Xid[0];
		}

		@Override
		public void rollback(Xid xid) throws XAException {
		}

		@Override
		public boolean setTransactionTimeout(int arg0) throws XAException {
			this.timeout = arg0;
			return true;
		}

		@Override
		public void start(Xid xid, int flags) throws XAException {
		}

	}

	/**
	 * TODO: delegate transactions to the platform JTA, see
	 * http://static.springsource
	 * .org/spring/docs/2.5.x/api/org/springframework/transaction
	 * /jta/JtaTransactionManager.html
	 * 
	 * @author asmirnov
	 * 
	 */
	private final class Neo4jLocalTransaction implements LocalTransaction {

		public boolean isActive() {
			return null != transaction;
		}

		@Override
		public void rollback() throws ResourceException {
			if (null != transaction) {
				transaction.failure();
				finish();
				fireRollbackEvent();
			}
		}

		public void finish() {
			transaction.finish();
			transaction = null;
		}

		@Override
		public void commit() throws ResourceException {
			if (null != transaction) {
				transaction.success();
				finish();
				fireCommitEvent();
			}
		}

		@Override
		public void begin() throws ResourceException {
			transaction = managedConnectionFactory.getDatabase().beginTx();
			fireBeginEvent();
		}
	}



	private Transaction transaction;

	/** The logwriter */
	private PrintWriter logwriter;

	/** ManagedConnectionFactory */
	private Neo4jManagedConnectionFactory managedConnectionFactory;

	/** Listeners */
	private List<ConnectionEventListener> listeners;

	/** Connection */
	private GraphDatabaseService connection;

	private LocalTransaction localTransaction;

	private Neo4jXAResource xaResource;

	/**
	 * Default constructor
	 * 
	 * @param mcf
	 *            mcf
	 */
	public Neo4jManagedConnection(Neo4jManagedConnectionFactory mcf) {
		this.managedConnectionFactory = mcf;
		this.logwriter = new PrintWriter(System.out);
		this.listeners = new ArrayList<ConnectionEventListener>(1);
		this.connection = null;
		this.localTransaction = new Neo4jLocalTransaction();
		this.xaResource = new Neo4jXAResource();
	}

	/**
	 * Creates a new connection handle for the underlying physical connection
	 * represented by the ManagedConnection instance.
	 * 
	 * @param subject
	 *            Security context as JAAS subject
	 * @param cxRequestInfo
	 *            ConnectionRequestInfo instance
	 * @return generic Object instance representing the connection handle.
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public GraphDatabaseService getConnection(Subject subject,
			ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		logwriter.append("getConnection()");
		connection = new Neo4JConnectionImpl(this, managedConnectionFactory);
		return connection;
	}

	/**
	 * Used by the container to change the association of an application-level
	 * connection handle with a ManagedConneciton instance.
	 * 
	 * @param connection
	 *            Application-level connection handle
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void associateConnection(Object connection) throws ResourceException {
		logwriter.append("associateConnection()");
	}

	/**
	 * Application server calls this method to force any cleanup on the
	 * ManagedConnection instance.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void cleanup() throws ResourceException

	{
		logwriter.append("cleanup()");
		this.connection = null;
	}

	/**
	 * Destroys the physical connection to the underlying resource manager.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void destroy() throws ResourceException {
		logwriter.append("destroy()");
		this.connection = null;
		this.localTransaction = null;
		managedConnectionFactory.destroyManagedConnection(this);
	}

	/**
	 * Adds a connection event listener to the ManagedConnection instance.
	 * 
	 * @param listener
	 *            A new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {
		logwriter.append("addConnectionEventListener()");
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.add(listener);
	}

	/**
	 * Removes an already registered connection event listener from the
	 * ManagedConnection instance.
	 * 
	 * @param listener
	 *            already registered connection event listener to be removed
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		logwriter.append("removeConnectionEventListener()");
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.remove(listener);
	}

	/**
	 * Close handle
	 * 
	 * @param neo4jConnectionImpl
	 *            The handle
	 */
	public void closeHandle(GraphDatabaseService neo4jConnectionImpl) {
		ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.CONNECTION_CLOSED);
		event.setConnectionHandle(neo4jConnectionImpl);
		fireCloseEvent(event);

	}

	private void fireCloseEvent(ConnectionEvent event) {
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}
	}

	private void fireRollbackEvent() {
		ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK);
		for (ConnectionEventListener cel : listeners) {
			cel.localTransactionRolledback(event);
		}
	}

	private void fireCommitEvent() {
		ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.LOCAL_TRANSACTION_COMMITTED);
		for (ConnectionEventListener cel : listeners) {
			cel.localTransactionCommitted(event);
		}
	}

	private void fireBeginEvent() {
		ConnectionEvent event = new ConnectionEvent(this,
				ConnectionEvent.LOCAL_TRANSACTION_STARTED);
		for (ConnectionEventListener cel : listeners) {
			cel.localTransactionStarted(event);
		}
	}

	/**
	 * Gets the log writer for this ManagedConnection instance.
	 * 
	 * @return Character ourput stream associated with this Managed-Connection
	 *         instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		return logwriter;
	}

	/**
	 * Sets the log writer for this ManagedConnection instance.
	 * 
	 * @param out
	 *            Character Output stream to be associated
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		logwriter = out;
	}

	/**
	 * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
	 * 
	 * @return LocalTransaction instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		logwriter.append("getLocalTransaction()");
		return localTransaction;
	}

	/**
	 * Returns an <code>javax.transaction.xa.XAresource</code> instance.
	 * 
	 * @return XAResource instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public XAResource getXAResource() throws ResourceException {
		logwriter.append("getXAResource()");
		return this.xaResource;
	}

	/**
	 * Gets the metadata information for this connection's underlying EIS
	 * resource manager instance.
	 * 
	 * @return ManagedConnectionMetaData instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		logwriter.append("getMetaData()");
		return new Neo4jManagedConnectionMetaData();
	}

}
