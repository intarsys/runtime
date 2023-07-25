package de.intarsys.tools.randomaccess;

import java.io.IOException;
import java.io.OutputStream;

public class RandomAccessOutputStream extends OutputStream {

	private final IRandomAccess randomAccess;

	private long offset;

	public RandomAccessOutputStream(IRandomAccess randomAccess) {
		this(randomAccess, 0);
	}

	public RandomAccessOutputStream(IRandomAccess randomAccess, long offset) {
		this.randomAccess = randomAccess;
		this.offset = offset;
	}

	@Override
	public void close() throws IOException {
		randomAccess.flush();
		// do not close
	}

	@Override
	public void flush() throws IOException {
		randomAccess.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		randomAccess.seek(offset);
		randomAccess.write(b);
		offset += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		randomAccess.seek(offset);
		randomAccess.write(b, off, len);
		offset += len;
	}

	@Override
	public void write(int b) throws IOException {
		randomAccess.seek(offset);
		randomAccess.write(b);
		offset++;
	}
}