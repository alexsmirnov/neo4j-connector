package com.netoprise.neo4j.transaction;

import java.util.logging.Logger;

import org.neo4j.helpers.Service;
import org.neo4j.kernel.impl.core.KernelPanicEventGenerator;
import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.TransactionManagerProvider;
import org.neo4j.kernel.impl.transaction.TxHook;

/**
 * TransactionManager provider that delegates transaction management to the application server.
 * @author asmirnov
 *
 */
@Service.Implementation(TransactionManagerProvider.class)
public class PlatformTransactionProvider extends TransactionManagerProvider {
	
	private static Logger log = Logger
	.getLogger("Neo4jTransactionManagerProvider");

	public static final String JEE_JTA = "jee-jta";

	public PlatformTransactionProvider() {
		super(JEE_JTA);
		log.info("Load PlatformTransactionManagerProvider");
	}

	@Override
	protected AbstractTransactionManager loadTransactionManager(
			String txLogDir, KernelPanicEventGenerator kpe,
			TxHook rollbackHook) {
		log.info("Create Platform TransactionManager wrapper");
		return new PlatformTransactionManager();
	}

}
