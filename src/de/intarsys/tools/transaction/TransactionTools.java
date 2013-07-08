package de.intarsys.tools.transaction;

/**
 * Tools for handling transactions.
 * <p>
 * Currently this also implements the missing link between transactions and
 * Resources.
 * 
 */
public class TransactionTools {

	static public void delist(IResource resource) throws TransactionException {
		Transaction transaction = (Transaction) TransactionManager.get()
				.getTransaction();
		if (transaction == null) {
			throw new NoTransactionException();
		}
		transaction.delist(resource);
	}

	static public void enlist(IResource resource) throws TransactionException {
		Transaction transaction = (Transaction) TransactionManager.get()
				.getTransaction();
		if (transaction == null) {
			throw new NoTransactionException();
		}
		transaction.enlist(resource);
	}

	static public <T extends IResource> T getResource(IResourceType<T> type,
			boolean create) throws TransactionException {
		Transaction transaction = (Transaction) TransactionManager.get()
				.getTransaction();
		if (transaction == null) {
			throw new NoTransactionException();
		}
		return getResource(type, transaction, create);
	}

	static protected <T extends IResource> T getResource(IResourceType<T> type,
			Transaction transaction, boolean create)
			throws TransactionException {
		T tempResource = transaction.getResource(type);
		if (tempResource == null) {
			if (create) {
				T parentResource = null;
				Transaction parentTransaction = (Transaction) transaction
						.getParent();
				if (parentTransaction != null) {
					parentResource = getResource(type, parentTransaction,
							create);
				}
				tempResource = type.createResource(parentResource);
				transaction.enlist(tempResource);
			} else {
				throw new TransactionException("no resource found");
			}
		}
		return tempResource;
	}
}
