package com.netoprise.neo4j.transaction;

import org.neo4j.helpers.Service;
import org.neo4j.kernel.impl.core.KernelPanicEventGenerator;
import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.TransactionManagerProvider;
import org.neo4j.kernel.impl.transaction.TxFinishHook;

/**
 * TransactionManager provider that delegates transaction management to the application server.
 * @author asmirnov
 *
 */
@Service.Implementation(TransactionManagerProvider.class)
public class PlatformTransactionProvider extends TransactionManagerProvider {
	public static final String JEE_JTA = "jee-jta";

	public PlatformTransactionProvider() {
		super(JEE_JTA);
	}

	@Override
	protected AbstractTransactionManager loadTransactionManager(
			String txLogDir, KernelPanicEventGenerator kpe,
			TxFinishHook rollbackHook) {
		return new PlatformTransactionManager();
	}
}
