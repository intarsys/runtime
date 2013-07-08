package de.intarsys.tools.locator.trusted;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.intarsys.tools.digest.DigestTools;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.digest.IDigester;
import de.intarsys.tools.locator.DelegatingLocatorFactory;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorFactory;
import de.intarsys.tools.stream.StreamTools;

/**
 * Create an {@link ILocator} that protects itself against malicious
 * manipulation of the underlying data.
 * 
 */
public class TrustedLocatorFactory extends DelegatingLocatorFactory {

	private IDigester digester;

	public TrustedLocatorFactory(ILocatorFactory wrapped) {
		super(wrapped);
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		ILocator locator = super.basicCreateLocator(location);
		return new MemoryTrustedLocator(this, locator);
	}

	synchronized protected IDigest createDigest(InputStream is)
			throws IOException {
		try {
			init();
			// just to be sure reused digester is in valid state
			digester.reset();
			return DigestTools.digest(digester, is);
		} catch (FileNotFoundException e) {
			return null;
		} finally {
			StreamTools.close(is);
		}
	}

	synchronized public IDigester getDigester() {
		init();
		return digester;
	}

	protected synchronized void init() {
		if (digester == null) {
			digester = DigestTools.createDigesterSHA1();
		}
	}

	synchronized public void setDigester(IDigester digester) {
		this.digester = digester;
	}

}
