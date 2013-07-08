package de.intarsys.tools.locator.trusted;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locking.ILock;
import de.intarsys.tools.locking.ILockLevel;
import de.intarsys.tools.locking.ILockSupport;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.stream.StreamTools;

/**
 * This {@link ILocator} controls manipulation of its data resource simply by
 * copying it completely to memory. All I/O is done through the in memory
 * buffer. When it comes to writing the data, the buffer is always completely
 * written back.
 * <P>
 * This locator acts on a "committed read" concurrency strategy. If an output
 * stream or random access is open, other clients requesting stream will always
 * see the old state until the output stream or random access will flush. From
 * now the changes are visible to NEW stream, but still not to the old ones.
 */
public class MemoryTrustedLocator extends TrustedLocator {

	private SoftReference bytes;

	private ILock lock;

	private int lockCount = 0;

	public MemoryTrustedLocator(TrustedLocatorFactory factory, ILocator wrapped) {
		super(factory, wrapped);
	}

	/**
	 * Try to hold on on the wrapped {@link ILocator} resources. This for
	 * example locks a file in the file system.
	 * 
	 * @throws IOException
	 */
	synchronized protected void acquire() throws IOException {
		if (lockCount++ == 0) {
			ILockSupport lockSupport = AdapterTools.getAdapter(getWrapped(),
					ILockSupport.class);
			if (lockSupport != null) {
				try {
					lock = lockSupport.getLock();
					lock.acquire(this, ILockLevel.SHARED);
				} catch (Exception e) {
					// 
				}
			}
		}
	}

	/**
	 * ATTENTION: this method must be called in a synchronized context
	 * 
	 * @return the wrapped bytes if any
	 */
	protected byte[] basicGetBytes() {
		if (bytes == null) {
			return null;
		}
		return (byte[]) bytes.get();
	}

	/**
	 * ATTENTION: this method must be called in a synchronized context
	 * 
	 */
	protected void basicSetBytes(byte[] pBytes) {
		this.bytes = new SoftReference(pBytes);
	}

	@Override
	protected ILocator createChildLocator(ILocator locator) {
		return new MemoryTrustedLocator(getFactory(), locator);
	}

	synchronized protected void dumpBytes(byte[] pBytes) throws IOException {
		// while dumping, digest is inconsistent with data -> synchronized
		basicSetBytes(pBytes);
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new ByteArrayInputStream(pBytes);
			os = getWrapped().getOutputStream();
			StreamTools.copyStream(is, os);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
		// if bytes changed, recompute digest
		// this will receive indirectly "pBytes" argument
		initDigest();

		// after this method pBytes is only weakly referenced, but
		// successfully dumped
	}

	synchronized protected byte[] getBytes() throws IOException {
		byte[] tempBytes = basicGetBytes();
		if (tempBytes == null) {
			// exception may be created in constructor
			if (getException() != null) {
				throw getException();
			}
			tempBytes = StreamTools.toByteArray(basicGetInputStream());
			basicSetBytes(tempBytes);
			if (getDigest() != null) {
				// we have already a snapshot, check if newly read content valid
				InputStream is = new ByteArrayInputStream(tempBytes);
				IDigest newDigest = getFactory().createDigest(is);
				if (!getDigest().equals(newDigest)) {
					throw new IOException("digest mismatch"); //$NON-NLS-1$
				}
			}
		}
		return tempBytes;
	}

	public InputStream getInputStream() throws IOException {
		// read only, no need to copy
		byte[] tempBytes = getBytes();
		acquire();
		// after acquire, creation may not fail (or must release())
		return new ByteArrayInputStream(tempBytes) {
			private boolean closed = false;

			@Override
			public void close() throws IOException {
				if (closed) {
					return;
				}
				// may close exactly once - get out of synch otherwise
				closed = true;
				super.close();
				release();
			}
		};
	}

	public OutputStream getOutputStream() throws IOException {
		acquire();
		// after acquire, creation may not fail (or must release())
		return new ByteArrayOutputStream() {
			private boolean closed = false;

			@Override
			public void close() throws IOException {
				if (closed) {
					return;
				}
				// may close exactly once - get out of synch otherwise
				closed = true;
				flush();
				super.close();
				release();
			}

			@Override
			public void flush() throws IOException {
				super.flush();
				// hold on bytes - otherwise we have a weak reference only
				dumpBytes(toByteArray());
			}

		};
	}

	public IRandomAccess getRandomAccess() throws IOException {
		// create local strong reference
		byte[] tempBytes;
		try {
			tempBytes = getBytes();
		} catch (FileNotFoundException e) {
			// ignore - create new buffer
			tempBytes = null;
		}
		acquire();
		// after acquire, creation may not fail (or must release())
		return new RandomAccessByteArray(tempBytes) {
			private boolean changed = false;

			private boolean closed = false;

			@Override
			public void close() throws IOException {
				if (closed) {
					return;
				}
				// may close exactly once - get out of synch otherwise
				closed = true;
				super.close();
				release();
			}

			@Override
			public void flush() throws IOException {
				super.flush();
				if (changed) {
					changed = false;
					// synchronize changes with locator
					// hold on bytes - otherwise we have a weak reference only
					dumpBytes(toByteArray());
				}
			}

			@Override
			public void setLength(long newLength) {
				changed = true;
				super.setLength(newLength);
			}

			@Override
			public void write(byte[] buffer) {
				changed = true;
				super.write(buffer);
			}

			@Override
			public void write(byte[] buffer, int start, int numBytes) {
				changed = true;
				super.write(buffer, start, numBytes);
			}

			@Override
			public void write(int b) {
				changed = true;
				super.write(b);
			}
		};
	}

	/**
	 * Release the wrapped {@link ILocator} resource.
	 */
	synchronized protected void release() {
		if (--lockCount == 0) {
			if (lock != null) {
				lock.release(this);
			}
		}
	}

	@Override
	public String toString() {
		return super.toString() + " [" + getWrapped() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
