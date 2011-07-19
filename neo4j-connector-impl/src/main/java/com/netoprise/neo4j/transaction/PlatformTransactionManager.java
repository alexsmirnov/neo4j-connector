package com.netoprise.neo4j.transaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;

/**
 * @author asmirnov
 * 
 */
class PlatformTransactionManager extends AbstractTransactionManager {
	private static Logger log = Logger
	.getLogger("Neo4jTransactionManager");

	private static final Collection<String> NAMES = Arrays.asList(
			"java:/TransactionManager", "java:appserver/TransactionManager",
			"java:pm/TransactionManager", "java:comp/TransactionManager","java:jboss/TransactionManager");

	private TransactionManager transactionManager;

	PlatformTransactionManager() {
	}

	@Override
	public void init(XaDataSourceManager xaDsManager) {
		Context initialContext;
		try {
			initialContext = new InitialContext();
		} catch (NamingException e1) {
			throw new RuntimeException("Cannot create JNDI Context");
		}
		for (String name : NAMES) {
			try {
				transactionManager = (TransactionManager) initialContext
						.lookup(name);
				log.info("TransactionManager found at "+name);
				return;
			} catch (NamingException e) {
				// try next
				continue;
			}
		}
		log.severe("Cannot find provided TransactionManager");
		throw new RuntimeException("Cannot find provided TransactionManager");
	}

	public void begin() throws NotSupportedException, SystemException {
		transactionManager.begin();
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		transactionManager.commit();
	}

	public int getStatus() throws SystemException {
		return transactionManager.getStatus();
	}

	public Transaction getTransaction() throws SystemException {
		return transactionManager.getTransaction();
	}

	public void resume(Transaction tobj) throws InvalidTransactionException,
			IllegalStateException, SystemException {
		transactionManager.resume(tobj);
	}

	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		transactionManager.rollback();
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		transactionManager.setRollbackOnly();
	}

	public void setTransactionTimeout(int seconds) throws SystemException {
		transactionManager.setTransactionTimeout(seconds);
	}

	public Transaction suspend() throws SystemException {
		return transactionManager.suspend();
	}

	@Override
	public void stop() {
		// Not our responsibility...
	}
}
