package de.intarsys.tools.locator.trusted;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.content.ICharsetAccess;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.locator.AbstractLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorNameFilter;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

/**
 * A common superclass for implementing {@link ITrustedLocator}
 * 
 */
public abstract class TrustedLocator extends AbstractLocator implements ITrustedLocator, IAdapterSupport {

	private ILocator wrapped;

	private IDigest digest;

	private TrustedLocatorFactory factory;

	private IOException exception;

	protected TrustedLocator(TrustedLocatorFactory factory, ILocator wrapped) {
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

	protected final InputStream basicGetInputStream() throws IOException {
		return getWrapped().getInputStream();
	}

	protected final OutputStream basicGetOutputStream() throws IOException {
		return getWrapped().getOutputStream();
	}

	protected final IRandomAccess basicGetRandomAccess() throws IOException {
		return getWrapped().getRandomAccess();
	}

	protected final Reader basicGetReader() throws IOException {
		return getWrapped().getReader();
	}

	protected final Reader basicGetReader(String encoding) throws IOException {
		return getWrapped().getReader(encoding);
	}

	protected final Writer basicGetWriter() throws IOException {
		return getWrapped().getWriter();
	}

	protected final Writer basicGetWriter(String encoding) throws IOException {
		return getWrapped().getWriter(encoding);
	}

	@Override
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

	protected abstract ILocator createChildLocator(ILocator wrapped);

	@Override
	public void delete() throws IOException {
		getWrapped().delete();
	}

	@Override
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TrustedLocator)) {
			return false;
		}
		return getWrapped().equals(((TrustedLocator) obj).getWrapped());
	}

	@Override
	public boolean exists() {
		return getWrapped().exists();
	}

	@Override
	public <T> T getAdapter(Class<T> clazz) {
		return AdapterTools.getAdapter(wrapped, clazz, null);
	}

	@Override
	public ILocator getChild(String name) {
		ILocator child = getWrapped().getChild(name);
		if (child == null) {
			return null;
		} else {
			return createChildLocator(child);
		}
	}

	@Override
	public IDigest getDigest() {
		return digest;
	}

	public IOException getException() {
		return exception;
	}

	public TrustedLocatorFactory getFactory() {
		return factory;
	}

	@Override
	public long getLength() throws IOException {
		return getWrapped().getLength();
	}

	@Override
	public String getName() {
		return getWrapped().getName();
	}

	@Override
	public ILocator getParent() {
		ILocator parent = getWrapped().getParent();
		if (parent == null) {
			return null;
		} else {
			return createChildLocator(parent);
		}
	}

	@Override
	public String getPath() {
		return getWrapped().getPath();
	}

	@Override
	public Reader getReader() throws IOException {
		ICharsetAccess charsetAccess = AdapterTools.getAdapter(getWrapped(), ICharsetAccess.class, null);
		String charset = null;
		if (charsetAccess != null) {
			charset = charsetAccess.getCharset();
		}
		if (StringTools.isEmpty(charset)) {
			return new InputStreamReader(getInputStream());
		}
		return new InputStreamReader(getInputStream(), charset);
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		if (encoding == null) {
			encoding = Charset.defaultCharset().name();
		}
		return new InputStreamReader(getInputStream(), encoding);
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

	@Override
	public Writer getWriter() throws IOException {
		ICharsetAccess charsetAccess = AdapterTools.getAdapter(getWrapped(), ICharsetAccess.class, null);
		String charset = null;
		if (charsetAccess != null) {
			charset = charsetAccess.getCharset();
		}
		if (StringTools.isEmpty(charset)) {
			return new OutputStreamWriter(getOutputStream());
		}
		return new OutputStreamWriter(getOutputStream(), charset);

	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		if (encoding == null) {
			encoding = Charset.defaultCharset().name();
		}
		return new OutputStreamWriter(getOutputStream(), encoding);
	}

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

	@Override
	public boolean isDirectory() {
		return getWrapped().isDirectory();
	}

	@Override
	public boolean isOutOfSynch() {
		return getWrapped().isOutOfSynch();
	}

	@Override
	public boolean isReadOnly() {
		return getWrapped().isReadOnly();
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) throws IOException {
		ILocator[] children = getWrapped().listLocators(filter);
		ILocator[] result = new ILocator[children.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = createChildLocator(children[i]);
		}
		return result;
	}

	@Override
	public void rename(String newName) throws IOException {
		getWrapped().rename(newName);
	}

	@Override
	public void setReadOnly() {
		getWrapped().setReadOnly();
	}

	@Override
	public void synch() {
		getWrapped().synch();
	}

	@Override
	public URI toURI() {
		return getWrapped().toURI();
	}
}
