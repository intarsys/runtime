package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A placeholder for delayed failure detection.
 * 
 */
public class FailedLocator extends AbstractLocator {

	private final Exception exception;

	public FailedLocator(Exception exception) {
		super();
		this.exception = exception;
	}

	@Override
	public void delete() throws IOException {
		// not required
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ILocator getChild(String name) {
		return null;
	}

	public Exception getException() {
		return exception;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public long getLength() throws IOException {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public ILocator getParent() {
		return null;
	}

	@Override
	public String getPath() {
		return "locator not available";
	}

	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public Reader getReader() throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public Writer getWriter() throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		throw new IOException("locator not available", getException());
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean isOutOfSynch() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) throws IOException {
		return new ILocator[0];
	}

	@Override
	public void rename(String newName) throws IOException {
		// not required
	}

	@Override
	public void setReadOnly() {
		// not required
	}

	@Override
	public void synch() {
		// not required
	}

	@Override
	public URI toURI() {
		return null;
	}

}
