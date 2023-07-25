package de.intarsys.tools.locator;

/**
 * Superclass for implementing a delegation model on an immutable wrapped
 * {@link ILocator}.
 * 
 */
public class ImmutableDelegatingLocator extends DelegatingLocator {

	private final ILocator locator;

	public ImmutableDelegatingLocator(ILocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	protected ILocator getLocator() {
		return locator;
	}

}
