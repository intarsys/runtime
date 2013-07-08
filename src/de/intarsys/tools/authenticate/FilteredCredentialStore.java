package de.intarsys.tools.authenticate;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.reflect.ObjectCreationException;

public class FilteredCredentialStore implements ICredentialStore,
		IElementConfigurable {

	// similar to the one in commons-collections
	public static interface Predicate {

		public boolean evaluate(ICredentialScope scope);
	}

	// similar to the one in commons-collections
	private static class TruePredicate implements Predicate {

		@Override
		public boolean evaluate(ICredentialScope scope) {
			return true;
		}
	}

	private static final String TAG_PREDICATE = "predicate"; //$NON-NLS-1$

	private Predicate predicate;
	private ICredentialStore store = new BasicCredentialProvider();

	public FilteredCredentialStore() {
		this(new TruePredicate());
	}

	public FilteredCredentialStore(Predicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public void configure(IElement element)
			throws ConfigurationException {
		IElement predicateElement = element.element(TAG_PREDICATE);
		if (element != null) {
			try {
				predicate = ElementTools.createObject(predicateElement,
						Predicate.class, null);
			} catch (ObjectCreationException ex) {
				throw new ConfigurationException(ex);
			}
		}
	}

	@Override
	public ICredential getCredential(ICredentialScope scope) {
		if (predicate.evaluate(scope)) {
			return store.getCredential(scope);
		}
		return null;
	}

	@Override
	public void removeCredential(ICredentialScope scope) {
		if (predicate.evaluate(scope)) {
			store.removeCredential(scope);
		}
	}

	@Override
	public void setCredential(ICredentialScope scope, ICredential credential) {
		if (predicate.evaluate(scope)) {
			store.setCredential(scope, credential);
		}
	}
}
