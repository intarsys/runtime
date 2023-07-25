/*
 * Copyright (c) 2007, intarsys GmbH
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
import java.net.URI;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.exception.ExceptionTools;
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

		private int current;

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
				int i = is.read();
				if (i == -1) {
					return i;
				}
				current++;
			}
			if (getEnd() != -1 && current >= getEnd()) {
				return -1;
			}
			int i = is.read();
			if (i != -1) {
				current = current + i;
			}
			return i;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			while (current < getStart()) {
				int i = is.read();
				if (i == -1) {
					return i;
				}
				current++;
			}
			if (getEnd() != -1 && current >= getEnd()) {
				return -1;
			}
			int i = is.read(b, off, len);
			if (i != -1) {
				current = current + i;
			}
			return i;
		}
	}

	private class RandomAccessProxy extends AbstractRandomAccess {

		private IRandomAccess ra;

		private long ptrHost;

		public RandomAccessProxy(IRandomAccess ra) {
			this.ra = ra;
			try {
				seek(0);
			} catch (IOException e) {
				throw ExceptionTools.wrap(e);
			}
		}

		@Override
		public void close() throws IOException {
			ra.close();
		}

		@Override
		public void flush() throws IOException {
			ra.flush();
		}

		@Override
		public long getLength() throws IOException {
			return Math.min(getEnd(), ra.getLength()) - getStart();
		}

		@Override
		public long getOffset() throws IOException {
			return ra.getOffset() - getStart();
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public int read() throws IOException {
			if (ptrHost < getStart()) {
				throw new IOException("index out of scope");
			}
			if (getEnd() != -1 && ptrHost >= getEnd()) {
				return -1;
			}
			int i = ra.read();
			ptrHost++;
			return i;
		}

		@Override
		public int read(byte[] buffer) throws IOException {
			if (ptrHost < getStart()) {
				throw new IOException("index out of scope");
			}
			if (getEnd() != -1 && ptrHost >= getEnd()) {
				return -1;
			}
			int numBytes = Math.min(buffer.length, (int) getEnd() - (int) ptrHost);
			int i = ra.read(buffer, 0, numBytes);
			ptrHost += i;
			return i;
		}

		@Override
		public int read(byte[] buffer, int start, int numBytes) throws IOException {
			if (ptrHost < getStart()) {
				throw new IOException("index out of scope");
			}
			if (getEnd() != -1 && ptrHost >= getEnd()) {
				return -1;
			}
			numBytes = Math.min(numBytes, (int) getEnd() - (int) ptrHost);
			int i = ra.read(buffer, start, numBytes);
			ptrHost += i;
			return i;
		}

		@Override
		public void seek(long offset) throws IOException {
			ra.seek(getStart() + offset);
			ptrHost = getStart() + offset;
		}

		@Override
		public void seekBy(long delta) throws IOException {
			ra.seek(ptrHost + delta);
			ptrHost += delta;
		}

		@Override
		public void setLength(long newLength) throws IOException {
			// not supported
		}

		@Override
		public void write(byte[] buffer) throws IOException {
			// not supported
		}

		@Override
		public void write(byte[] buffer, int start, int numBytes) throws IOException {
			// not supported
		}

		@Override
		public void write(int b) throws IOException {
			// not supported
		}
	}

	private class ReaderProxy extends Reader {
		private Reader r;

		private int current;

		public ReaderProxy(Reader r) {
			this.r = r;
		}

		@Override
		public void close() throws IOException {
			r.close();
		}

		@Override
		@SuppressWarnings("java:S2677")
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

	private long start;

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

	@Override
	public boolean exists() {
		return getDelegate().exists();
	}

	@Override
	public <T> T getAdapter(Class<T> clazz) {
		return AdapterTools.getAdapter(delegate, clazz, null);
	}

	@Override
	public ILocator getChild(String name) {
		return getDelegate().getChild(name);
	}

	public ILocator getDelegate() {
		return delegate;
	}

	public long getEnd() {
		return end;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new InputStreamProxy(getDelegate().getInputStream());
	}

	@Override
	public long getLength() {
		return getEnd() - getStart();
	}

	@Override
	public String getName() {
		if (name != null) {
			return name;
		}
		return getDelegate().getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public ILocator getParent() {
		return getDelegate().getParent();
	}

	@Override
	public String getPath() {
		if (getName() != null) {
			return getName();
		}
		return getDelegate().getPath();
	}

	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		return new RandomAccessProxy(getDelegate().getRandomAccess());
	}

	@Override
	public Reader getReader() throws IOException {
		return new ReaderProxy(getDelegate().getReader());
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		return new ReaderProxy(getDelegate().getReader(encoding));
	}

	public long getStart() {
		return start;
	}

	@Override
	public Writer getWriter() throws IOException {
		return null;
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		return null;
	}

	@Override
	public boolean isDirectory() {
		return getDelegate().isDirectory();
	}

	@Override
	public boolean isOutOfSynch() {
		return getDelegate().isOutOfSynch();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) throws IOException {
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
			throw ExceptionTools.wrap(e);
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
			throw ExceptionTools.wrap(e);
		}
		this.start = start;
	}

	@Override
	public void synch() {
		getDelegate().synch();
	}

	@Override
	public URI toURI() {
		return getDelegate().toURI();
	}
}
