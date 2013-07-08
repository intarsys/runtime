package de.intarsys.tools.locator;

import java.io.IOException;

/**
 * Superclass for implementing a delegation model on an MUTABLE wrapped
 * {@link ILocator}. MOst of the time this will be a lazy evaluated target.
 * 
 */
public class MutableDelegatingLocator extends DelegatingLocator {

	private ILocator locator;

	public MutableDelegatingLocator(ILocator locator) {
		super();
		this.locator = locator;
	}

	protected ILocator createLocator() throws IOException {
		throw new IOException("undefined locator");
	}

	/**
	 * Get the underlying locator instance.
	 * 
	 * This method MAY throw an exception for implementations that lazy create
	 * the target locator.
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	synchronized public ILocator getLocator() throws IOException {
		if (locator == null) {
			locator = createLocator();
		}
		return locator;
	}
}
