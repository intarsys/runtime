package de.intarsys.tools.locator.trusted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.randomaccess.IRandomAccess;

public class HashedTrustedLocator extends TrustedLocator {

	protected HashedTrustedLocator(TrustedLocatorFactory factory,
			ILocator wrapped) {
		super(factory, wrapped);
	}

	@Override
	protected ILocator createChildLocator(ILocator locator) {
		return new HashedTrustedLocator(getFactory(), locator);
	}

	public InputStream getInputStream() throws IOException {
		return getWrapped().getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return getWrapped().getOutputStream();
	}

	public IRandomAccess getRandomAccess() throws IOException {
		return getWrapped().getRandomAccess();
	}

}
