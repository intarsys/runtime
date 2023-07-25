package de.intarsys.tools.randomaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("resource")
public class TestRandomAccessViewport {
	private static final int TOTAL_LENGTH = 100;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void canGetLength() throws IOException {
		IRandomAccess viewport = createViewport(50, 33);
		assertEquals(33, viewport.getLength());
	}

	@Test
	public void canGetLengthOpenEnded() throws IOException {
		IRandomAccess viewport = createViewport(50, -1);
		assertEquals(TOTAL_LENGTH - 50, viewport.getLength());
	}

	@Test
	public void canGetOffset() throws IOException {
		IRandomAccess viewport = createViewport(50, 10);

		for (int i = 0; i < 10; ++i) {
			assertEquals(i, viewport.getOffset());
			viewport.read();
		}
	}

	@Test
	public void doesNotSeekToStart() throws IOException {
		IRandomAccess ra = createRandomAccess();
		assertEquals(0, ra.getOffset());

		IRandomAccess viewport = new RandomAccessViewport(ra, 50, 10);
		// We are still at the original offset, outside of the viewport's bounds
		assertEquals(-50, viewport.getOffset());

		// ... and any attempt to read outside the viewport will throw an exception
		thrown.expect(IOException.class);
		viewport.read();
	}

	@Test
	public void canReadByteWise() throws IOException {
		int start = 10;
		int length = 33;
		IRandomAccess viewport = createViewport(start, length);
		for (int i = 0; i < length; ++i) {
			assertEquals(start + i, viewport.read());
		}

		assertEquals(-1, viewport.read());
	}

	@Test
	public void canReadBuffer() throws IOException {
		int start = 10;
		int length = 79;
		IRandomAccess viewport = createViewport(start, length);

		byte[] buffer = new byte[8];
		int totalBytesRead = 0;
		while (totalBytesRead < length) {
			int bytesRead = viewport.read(buffer);
			assertTrue(bytesRead >= 0);
			assertTrue(bytesRead <= buffer.length);

			for (int i = 0; i < bytesRead; ++i) {
				assertEquals(start + totalBytesRead + i, buffer[i]);
			}

			totalBytesRead += bytesRead;
		}

		assertEquals(-1, viewport.read(buffer));
	}

	@Test
	public void canReadPartialBuffer() throws IOException {
		int start = 10;
		int length = 79;
		IRandomAccess viewport = createViewport(start, length);

		byte[] buffer = new byte[length];
		int totalBytesRead = 0;
		while (totalBytesRead < length) {
			int bytesToRead = Math.min(7, buffer.length - totalBytesRead);
			int bytesRead = viewport.read(buffer, totalBytesRead, bytesToRead);
			assertTrue(bytesRead >= 0);
			assertTrue(bytesRead <= bytesToRead);

			totalBytesRead += bytesRead;
		}

		for (int i = 0; i < length; ++i) {
			assertEquals(start + i, buffer[i]);
		}

		assertEquals(-1, viewport.read(buffer));
	}

	@Test
	public void canSeek() throws IOException {
		IRandomAccess viewport = createViewport(25, 50);
		viewport.seek(33L);
		assertEquals(58, viewport.read());

		viewport.seek(17L);
		assertEquals(42L, viewport.read());
	}

	@Test
	public void canSeekBeyondEnd() throws IOException {
		IRandomAccess viewport = createViewport(25, 50);
		viewport.seek(60L);
		assertEquals(-1, viewport.read());
	}

	@Test
	public void cannotSeekToNegativeOffset() throws IOException {
		IRandomAccess viewport = createViewport(25, 50);

		thrown.expect(IOException.class);
		viewport.seek(-10L);
	}

	@Test
	public void canSeekByDelta() throws IOException {
		IRandomAccess viewport = createViewport(25, 50);
		viewport.seekBy(33L); // 33
		assertEquals(58, viewport.read()); // 33 + 1 = 34

		viewport.seekBy(-9L); // 34 - 9 = 25
		assertEquals(50L, viewport.read()); // 25 + 1 = 26

		viewport.seekBy(20L); // 26 + 20 = 46
		assertEquals(71, viewport.read());
	}

	@Test
	public void cannotSeekByDeltaToNegativeOffset() throws IOException {
		IRandomAccess viewport = createViewport(25, 50);
		viewport.seek(10L);

		thrown.expect(IOException.class);
		viewport.seekBy(-36L);
	}

	@Test
	public void canSeekByDeltaBeyondEnd() throws IOException {
		IRandomAccess viewport = createViewport(25, 50);
		viewport.seek(60L);

		assertEquals(-1, viewport.read());
	}

	@Test
	public void isReadOnly() throws IOException {
		IRandomAccess viewport = createViewport(10, 20);
		assertTrue(viewport.isReadOnly());
	}

	@Test
	public void cannotSetLength() throws IOException {
		IRandomAccess viewport = createViewport(10, 20);

		thrown.expect(IOException.class);
		viewport.setLength(10L);
	}

	@Test
	public void cannotWrite() throws IOException {
		IRandomAccess viewport = createViewport(10, 20);

		thrown.expect(IOException.class);
		viewport.write(123);
	}

	@Test
	public void cannotWriteBuffer() throws IOException {
		IRandomAccess viewport = createViewport(10, 20);

		thrown.expect(IOException.class);
		byte[] buffer = { (byte) 1, (byte) 2, (byte) 3 };
		viewport.write(buffer);
	}

	@Test
	public void cannotWritePartialBuffer() throws IOException {
		IRandomAccess viewport = createViewport(10, 20);

		thrown.expect(IOException.class);
		byte[] buffer = { (byte) 1, (byte) 2, (byte) 3 };
		viewport.write(buffer, 1, 1);
	}

	private IRandomAccess createViewport(int start, int length) throws IOException {
		IRandomAccess randomAccess = createRandomAccess();
		IRandomAccess viewport = new RandomAccessViewport(randomAccess, start, length);
		viewport.seek(0L);

		return viewport;
	}

	private IRandomAccess createRandomAccess() {
		byte[] bytes = new byte[TOTAL_LENGTH];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = (byte) i;
		}

		return new RandomAccessByteArray(bytes);
	}
}
