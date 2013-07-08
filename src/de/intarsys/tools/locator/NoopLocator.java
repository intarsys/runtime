package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A no op {@link ILocator}.
 * 
 */
public class NoopLocator implements ILocator {

	public void delete() throws IOException {
		throw new IOException("unsupported operation");
	}

	public boolean exists() {
		return false;
	}

	public ILocator getChild(String name) {
		return null;
	}

	public String getFullName() {
		return "null";
	}

	public InputStream getInputStream() throws IOException {
		throw new IOException("unsupported operation");
	}

	public long getLength() throws IOException {
		return 0;
	}

	public String getLocalName() {
		return "null";
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("unsupported operation");
	}

	public ILocator getParent() {
		return null;
	}

	public IRandomAccess getRandomAccess() throws IOException {
		throw new IOException("unsupported operation");
	}

	public Reader getReader() throws IOException {
		throw new IOException("unsupported operation");
	}

	public Reader getReader(String encoding) throws IOException {
		throw new IOException("unsupported operation");
	}

	public String getType() {
		return "null";
	}

	public String getTypedName() {
		return "null";
	}

	public Writer getWriter() throws IOException {
		throw new IOException("unsupported operation");
	}

	public Writer getWriter(String encoding) throws IOException {
		throw new IOException("unsupported operation");
	}

	public boolean isDirectory() {
		return false;
	}

	public boolean isOutOfSynch() {
		return false;
	}

	public boolean isReadOnly() {
		return true;
	}

	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		return null;
	}

	public void rename(String newName) throws IOException {
	}

	public void setReadOnly() {
	}

	public void synch() {
	}

	public URL toURL() {
		return null;
	}

}
