package de.intarsys.tools.transaction;

/**
 * 
 * A transaction manager allowing for a new root transaction within a thread.
 *
 */
public class ThreadExclusiveTransactionManager implements ITransactionManager {

	public static ThreadExclusiveTransactionManager install() {
		Thread thread = Thread.currentThread();
		ThreadExclusiveTransactionManager transactionManager = new ThreadExclusiveTransactionManager(thread,
				TransactionManager.get());
		TransactionManager.set(transactionManager);
		return transactionManager;
	}

	private ITransactionManager mainManager;

	private Thread thread;

	private ITransactionManager exclusiveManager;

	public ThreadExclusiveTransactionManager(Thread thread, ITransactionManager delegate) {
		this.thread = thread;
		this.mainManager = delegate;
		this.exclusiveManager = new ThreadLocalTransactionManager();
	}

	@Override
	public void begin() throws TransactionException {
		getResponsible().begin();
	}

	@Override
	public void commit() throws TransactionException {
		getResponsible().commit();
	}

	@Override
	public void commitResume() throws TransactionException {
		getResponsible().commitResume();
	}

	protected ITransactionManager getResponsible() {
		if (Thread.currentThread() == thread) {
			return exclusiveManager;
		}
		return mainManager;
	}

	@Override
	public ITransaction getTransaction() {
		return getResponsible().getTransaction();
	}

	public void restore() {
		TransactionManager.set(mainManager);
	}

	@Override
	public void resume(ITransaction tx) throws TransactionException {
		getResponsible().resume(tx);
	}

	@Override
	public void rollback() throws TransactionException {
		getResponsible().rollback();
	}

	@Override
	public void rollbackResume() throws TransactionException {
		getResponsible().rollbackResume();
	}

	@Override
	public ITransaction suspend() throws TransactionException {
		return getResponsible().suspend();
	}

}
