package de.intarsys.tools.locator.trusted;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorNameFilter;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A common superclass for implementing {@link ITrustedLocator}
 * 
 */
abstract public class TrustedLocator implements ILocator, ITrustedLocator,
		IAdapterSupport {

	private ILocator wrapped;

	private IDigest digest;

	private TrustedLocatorFactory factory;

	private IOException exception;

	public TrustedLocator(TrustedLocatorFactory factory, ILocator wrapped) {
		this.factory = factory;
		this.wrapped = wrapped;
		try {
			initDigest();
		} catch (FileNotFoundException e) {
			// ignore
		} catch (IOException e) {
			// remember to rethrow later
			exception = e;
		}
	}

	protected InputStream basicGetDigestStream() throws IOException {
		return basicGetInputStream();
	}

	final protected InputStream basicGetInputStream() throws IOException {
		return getWrapped().getInputStream();
	}

	final protected OutputStream basicGetOutputStream() throws IOException {
		return getWrapped().getOutputStream();
	}

	final protected IRandomAccess basicGetRandomAccess() throws IOException {
		return getWrapped().getRandomAccess();
	}

	final protected Reader basicGetReader() throws IOException {
		return getWrapped().getReader();
	}

	final protected Reader basicGetReader(String encoding) throws IOException {
		return getWrapped().getReader(encoding);
	}

	final protected Writer basicGetWriter() throws IOException {
		return getWrapped().getWriter();
	}

	final protected Writer basicGetWriter(String encoding) throws IOException {
		return getWrapped().getWriter();
	}

	public void checkpoint() throws IOException {
		// must be synchronized as this may interfere with digest creation in
		// "dumpBytes"
		if (getException() != null) {
			throw getException();
		}
		if (getDigest() == null) {
			return;
		}
		IDigest newDigest = factory.createDigest(basicGetDigestStream());
		if (!getDigest().equals(newDigest)) {
			throw new IOException("digest mismatch");
		}
	}

	abstract protected ILocator createChildLocator(ILocator wrapped);

	public void delete() throws IOException {
		getWrapped().delete();
	}

	public void ensureEqual(ILocator locator) throws IOException {
		if (getException() != null) {
			throw getException();
		}
		if (getDigest() == null) {
			// no need to bother, we have no checkpoint to check against
			return;
		}
		if (!(locator instanceof TrustedLocator)) {
			throw new IOException("uncomparable locators");
		}
		TrustedLocator other = (TrustedLocator) locator;
		if (other.getDigest() == null) {
			other.checkpoint();
		}
		if (!getDigest().equals(other.getDigest())) {
			throw new IOException("digest mismatch");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TrustedLocator)) {
			return false;
		}
		return getWrapped().equals(((TrustedLocator) obj).getWrapped());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#exists()
	 */
	public boolean exists() {
		return getWrapped().exists();
	}

	public <T> T getAdapter(Class<T> clazz) {
		return AdapterTools.getAdapter(wrapped, clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	public ILocator getChild(String name) {
		ILocator child = getWrapped().getChild(name);
		if (child == null) {
			return null;
		} else {
			return createChildLocator(child);
		}
	}

	protected IDigest getDigest() {
		return digest;
	}

	public IOException getException() {
		return exception;
	}

	public TrustedLocatorFactory getFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	public String getFullName() {
		return getWrapped().getFullName();
	}

	public long getLength() throws IOException {
		return getWrapped().getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getLocalName()
	 */
	public String getLocalName() {
		return getWrapped().getLocalName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getParent()
	 */
	public ILocator getParent() {
		ILocator parent = getWrapped().getParent();
		if (parent == null) {
			return null;
		} else {
			return createChildLocator(parent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	public Reader getReader() throws IOException {
		return new InputStreamReader(getInputStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader(java.lang.String)
	 */
	public Reader getReader(String encoding) throws IOException {
		return new InputStreamReader(getInputStream(), encoding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getType()
	 */
	public String getType() {
		return getWrapped().getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getTypedName()
	 */
	public String getTypedName() {
		return getWrapped().getTypedName();
	}

	/**
	 * The wrapped {@link ILocator} for this. You should not use this one
	 * directly for security relevant operations.
	 * 
	 * @return The wrapped {@link ILocator} for this.
	 */
	public ILocator getWrapped() {
		return wrapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter()
	 */
	public Writer getWriter() throws IOException {
		return new OutputStreamWriter(getOutputStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter(java.lang.String)
	 */
	public Writer getWriter(String encoding) throws IOException {
		return new OutputStreamWriter(getOutputStream(), encoding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}

	protected void initDigest() throws IOException {
		if (wrapped.isDirectory()) {
			return;
		}
		digest = factory.createDigest(basicGetDigestStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isDirectory()
	 */
	public boolean isDirectory() {
		return getWrapped().isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isOutOfSynch()
	 */
	public boolean isOutOfSynch() {
		return getWrapped().isOutOfSynch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isReadOnly()
	 */
	public boolean isReadOnly() {
		return getWrapped().isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#listLocators(de.intarsys.tools.locator.ILocatorNameFilter)
	 */
	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		ILocator[] children = getWrapped().listLocators(filter);
		ILocator[] result = new ILocator[children.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = createChildLocator(children[i]);
		}
		return result;
	}

	public void rename(String newName) throws IOException {
		getWrapped().rename(newName);
	}

	public void setReadOnly() {
		getWrapped().setReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	public void synch() {
		getWrapped().synch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#toURL()
	 */
	public URL toURL() {
		return getWrapped().toURL();
	}
}
