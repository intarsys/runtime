package de.intarsys.tools.transaction;

/**
 * A common superclass for implementing the {@link ITransactionManager}.
 * 
 */
abstract public class CommonTransactionManager implements ITransactionManager {

	public void begin() {
		Transaction tx = lookupTransaction();
		Transaction newTransaction = null;
		if (tx == null) {
			newTransaction = createTransaction();
		} else {
			newTransaction = tx.createTransaction();
		}
		newTransaction.start();
		registerTransaction(newTransaction);
	}

	public void commit() throws TransactionException {
		Transaction tx = lookupTransaction();
		if (tx == null) {
			throw new NoTransactionException(
					"no transaction active when commiting");
		} else {
			Transaction parent = (Transaction) tx.getParent();
			tx.commit();
			registerTransaction(parent);
		}
	}

	public void commitResume() throws TransactionException {
		Transaction tx = lookupTransaction();
		if (tx == null) {
			throw new NoTransactionException(
					"no transaction active when commiting");
		} else {
			tx.commitResume();
		}
	}

	protected Transaction createTransaction() {
		return new RootTransaction();
	}

	public ITransaction getTransaction() {
		return lookupTransaction();
	}

	abstract protected Transaction lookupTransaction();

	abstract protected void registerTransaction(Transaction tx);

	public void resume(ITransaction transaction) {
		Transaction tx = lookupTransaction();
		if (tx == transaction) {
			return;
		}
		if (tx != null) {
			tx.suspend();
		}
		((Transaction) transaction).resume();
		registerTransaction(((Transaction) transaction));
	}

	public void rollback() throws TransactionException {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			Transaction parent = (Transaction) tx.getParent();
			tx.rollback();
			registerTransaction(parent);
		}
	}

	public void rollbackResume() throws TransactionException {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			tx.rollbackResume();
		}
	}

	public ITransaction suspend() {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			tx.suspend();
			registerTransaction(null);
		}
		return tx;
	}

}
