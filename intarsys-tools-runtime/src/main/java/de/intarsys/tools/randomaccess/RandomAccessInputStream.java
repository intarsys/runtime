package de.intarsys.tools.randomaccess;

import java.io.IOException;
import java.io.InputStream;

public class RandomAccessInputStream extends InputStream {

	private final IRandomAccess randomAccess;

	private long offset;

	public RandomAccessInputStream(IRandomAccess randomAccess) {
		this(randomAccess, 0);
	}

	public RandomAccessInputStream(IRandomAccess randomAccess, long offset) {
		this.randomAccess = randomAccess;
		this.offset = offset;
	}

	@Override
	public void close() throws IOException {
		// do not close....
	}

	@Override
	public int read() throws IOException {
		randomAccess.seek(offset);
		int i = randomAccess.read();
		if (i != -1) {
			offset++;
		}
		return i;
	}

	@Override
	public int read(byte[] b) throws IOException {
		randomAccess.seek(offset);
		int i = randomAccess.read(b);
		if (i != -1) {
			offset += i;
		}
		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		randomAccess.seek(offset);
		int i = randomAccess.read(b, off, len);
		if (i != -1) {
			offset += i;
		}
		return i;
	}
}
