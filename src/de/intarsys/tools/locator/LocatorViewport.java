/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.randomaccess.AbstractRandomAccess;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A viewport implementation for {@link ILocator}.
 * 
 * This object defines a range of bytes visible to the client of the viewport.
 */
public class LocatorViewport extends CommonLocator implements IAdapterSupport {

	private class InputStreamProxy extends InputStream {
		private InputStream is;

		private int current = 0;

		public InputStreamProxy(InputStream is) {
			this.is = is;
		}

		@Override
		public void close() throws IOException {
			is.close();
		}

		@Override
		public int read() throws IOException {
			while (current < getStart()) {
				is.read();
				current++;
			}
			if (getEnd() != -1 && current >= getEnd()) {
				return -1;
			}
			int i = is.read();
			current++;
			return i;
		}
	}

	private class RandomAccessProxy extends AbstractRandomAccess {

		private IRandomAccess ra;

		private long ptrLocal = 0;

		private long ptrHost = 0;

		public RandomAccessProxy(IRandomAccess ra) {
			this.ra = ra;
			try {
				seek(0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void close() throws IOException {
			ra.close();
		}

		public void flush() throws IOException {
			ra.flush();
		}

		public long getLength() throws IOException {
			long end = Math.min(getEnd(), ra.getLength());
			return end - getStart();
		}

		public long getOffset() throws IOException {
			return ra.getOffset() - getStart();
		}

		public boolean isReadOnly() {
			return true;
		}

		public int read() throws IOException {
			if (ptrHost < getStart()) {
				throw new IOException("index out of scope");
			}
			if (getEnd() != -1 && ptrHost >= getEnd()) {
				return -1;
			}
			int i = ra.read();
			ptrHost++;
			ptrLocal++;
			return i;
		}

		public int read(byte[] buffer) throws IOException {
			if (ptrHost < getStart()) {
				throw new IOException("index out of scope");
			}
			if (getEnd() != -1 && ptrHost >= getEnd()) {
				return -1;
			}
			int numBytes = Math.min(buffer.length, (int) getEnd()
					- (int) ptrHost);
			int i = ra.read(buffer, 0, numBytes);
			ptrHost += i;
			ptrLocal += i;
			return i;
		}

		public int read(byte[] buffer, int start, int numBytes)
				throws IOException {
			if (ptrHost < getStart()) {
				throw new IOException("index out of scope");
			}
			if (getEnd() != -1 && ptrHost >= getEnd()) {
				return -1;
			}
			numBytes = Math.min(numBytes, (int) getEnd() - (int) ptrHost);
			int i = ra.read(buffer, start, numBytes);
			ptrHost += i;
			ptrLocal += i;
			return i;
		}

		public void seek(long offset) throws IOException {
			ra.seek(getStart() + offset);
			ptrHost = getStart() + offset;
			ptrLocal = offset;
		}

		public void seekBy(long delta) throws IOException {
			ra.seek(ptrHost + delta);
			ptrHost += delta;
			ptrLocal += delta;
		}

		public void setLength(long newLength) throws IOException {
		}

		public void write(byte[] buffer) throws IOException {
		}

		public void write(byte[] buffer, int start, int numBytes)
				throws IOException {
		}

		public void write(int b) throws IOException {
		}
	}

	private class ReaderProxy extends Reader {
		private Reader r;

		private int current = 0;

		public ReaderProxy(Reader r) {
			this.r = r;
		}

		@Override
		public void close() throws IOException {
			r.close();
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			while (current < getStart()) {
				r.read();
				current++;
			}
			if (current >= getEnd()) {
				return -1;
			}
			len = Math.min(len, (int) getEnd() - current);
			int i = r.read(cbuf, off, len);
			current += i;
			return i;
		}
	}

	private ILocator delegate;

	private long start = 0;

	private long end = -1;

	private String name;

	public LocatorViewport(ILocator delegate) {
		super();
		setDelegate(delegate);
	}

	@Override
	public void delete() throws IOException {
		// nothing to do...
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#exists()
	 */
	public boolean exists() {
		return getDelegate().exists();
	}

	public <T> T getAdapter(Class<T> clazz) {
		return AdapterTools.getAdapter(delegate, clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	public ILocator getChild(String name) {
		return getDelegate().getChild(name);
	}

	public ILocator getDelegate() {
		return delegate;
	}

	public long getEnd() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	public String getFullName() {
		if (getName() != null) {
			return getName();
		}
		return getDelegate().getFullName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new InputStreamProxy(getDelegate().getInputStream());
	}

	@Override
	public long getLength() {
		return getEnd() - getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getLocalName()
	 */
	public String getLocalName() {
		if (getName() != null) {
			return getName();
		}
		return getDelegate().getLocalName();
	}

	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getParent()
	 */
	public ILocator getParent() {
		return getDelegate().getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getRandomAccess()
	 */
	public IRandomAccess getRandomAccess() throws IOException {
		return new RandomAccessProxy(getDelegate().getRandomAccess());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	public Reader getReader() throws IOException {
		return new ReaderProxy(getDelegate().getReader());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader(java.lang.String)
	 */
	public Reader getReader(String encoding) throws IOException {
		return new ReaderProxy(getDelegate().getReader(encoding));
	}

	public long getStart() {
		return start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getType()
	 */
	public String getType() {
		return getDelegate().getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getTypedName()
	 */
	public String getTypedName() {
		return getDelegate().getTypedName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter()
	 */
	public Writer getWriter() throws IOException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter(java.lang.String)
	 */
	public Writer getWriter(String encoding) throws IOException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isDirectory()
	 */
	public boolean isDirectory() {
		return getDelegate().isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isOutOfSynch()
	 */
	public boolean isOutOfSynch() {
		return getDelegate().isOutOfSynch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.locator.ILocator#listLocators(de.intarsys.tools.locator
	 * .ILocatorNameFilter)
	 */
	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		return getDelegate().listLocators(filter);
	}

	@Override
	public void rename(String newName) throws IOException {
		getDelegate().rename(newName);
	}

	protected void setDelegate(ILocator delegate) {
		this.delegate = delegate;
	}

	public void setEnd(long end) {
		try {
			if ((end < 0) || (end > getDelegate().getLength())) {
				throw new IllegalArgumentException("end");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.end = end;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStart(long start) {
		try {
			if ((start < 0) || (start > getDelegate().getLength())) {
				throw new IllegalArgumentException("start");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.start = start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	public void synch() {
		getDelegate().synch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#toURL()
	 */
	public URL toURL() {
		return getDelegate().toURL();
	}
}
