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
package de.intarsys.tools.randomaccess;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.file.FileTools;

@RunWith(Parameterized.class)
@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class TestRandomAccessWrite {

	private static final int STEP = 19;

	protected static byte[] createBytes(long size) {
		byte[] bytes = new byte[(int) size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) i;
		}
		return bytes;
	}

	protected static File createFile(long size) throws IOException, FileNotFoundException {
		File file = File.createTempFile("randomAccessTest", ".bin");
		file.deleteOnExit();
		OutputStream os = new FileOutputStream(file);
		for (int i = 0; i < size; i++) {
			os.write(i);
		}
		os.close();
		return file;
	}

	@Parameters
	public static Object[][] parameters() throws IOException {
		return new Object[][] { //
				paramsRandomAccessFile(100), //
				paramsBufferedRandomAccessFile(100, 1), //
				paramsBufferedRandomAccessFile(100, 1), //
				paramsBufferedRandomAccessFile(100, 10), //
				paramsBufferedRandomAccessFile(100, 10), //
				paramsBufferedRandomAccessFile(100, 2048), //
				paramsBufferedRandomAccessFile(100, 2048), //
				paramsRandomAccessByteArray(100), //
				paramsRandomAccessByteArray(100), //
				paramsBufferedRandomAccessByteArray(100, 1), //
				paramsBufferedRandomAccessByteArray(100, 1), //
				paramsBufferedRandomAccessByteArray(100, 10), //
				paramsBufferedRandomAccessByteArray(100, 10), //
				paramsBufferedRandomAccessByteArray(100, 2048), //
				paramsBufferedRandomAccessByteArray(100, 2048), //
		};
	}

	protected static Object[] paramsBufferedRandomAccessByteArray(int endSize, int bufferSize)
			throws FileNotFoundException, IOException {
		byte[] bytes = createBytes(255);
		Function supplierBytes = (ra) -> {
			return ((RandomAccessByteArray) ((BufferedRandomAccess) ra).getRandomAccess()).toByteArray();
		};
		Supplier supplierRandomAccess = () -> {
			try {
				return new BufferedRandomAccess(new RandomAccessByteArray(bytes), bufferSize);
			} catch (IOException e) {
				throw ExceptionTools.wrap(e);
			}
		};
		return new Object[] { supplierBytes, supplierRandomAccess, endSize };
	}

	protected static Object[] paramsBufferedRandomAccessFile(int endSize, int bufferSize)
			throws FileNotFoundException, IOException {
		File file = createFile(255);
		Function supplierBytes = (ra) -> {
			try {
				return FileTools.getBytes(file);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		};
		Supplier supplierRandomAccess = () -> {
			try {
				return new BufferedRandomAccess(new RandomAccessFile(file), bufferSize);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		};
		return new Object[] { supplierBytes, supplierRandomAccess, endSize };
	}

	protected static Object[] paramsRandomAccessByteArray(int endSize) throws FileNotFoundException, IOException {
		byte[] bytes = createBytes(255);
		Function supplierBytes = (ra) -> {
			return ((RandomAccessByteArray) ra).toByteArray();
		};
		Supplier supplierRandomAccess = () -> {
			return new RandomAccessByteArray(bytes);
		};
		return new Object[] { supplierBytes, supplierRandomAccess, endSize };
	}

	protected static Object[] paramsRandomAccessFile(int endSize) throws FileNotFoundException, IOException {
		File file = createFile(255);
		Function supplierBytes = (ra) -> {
			try {
				return FileTools.getBytes(file);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		};
		Supplier supplierRandomAccess = () -> {
			try {
				return new RandomAccessFile(file);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		};
		return new Object[] { supplierBytes, supplierRandomAccess, endSize };
	}

	private Supplier<IRandomAccess> randomAccessSupplier;

	private Function<IRandomAccess, byte[]> bytesSupplier;

	private int endSize;

	public TestRandomAccessWrite(Function<IRandomAccess, byte[]> bytesSupplier,
			Supplier<IRandomAccess> randomAccessSupplier, int endSize) {
		super();
		this.bytesSupplier = bytesSupplier;
		this.randomAccessSupplier = randomAccessSupplier;
		this.endSize = endSize;
	}

	public byte[] getBytes(IRandomAccess randomAccess) {
		return bytesSupplier.apply(randomAccess);
	}

	public IRandomAccess getRandomAccess() {
		return randomAccessSupplier.get();
	}

	@Test
	public void testReverseWriteBlock() throws IOException {
		IRandomAccess ra = getRandomAccess();
		ra.setLength(0);
		byte[] out = new byte[10];
		int written = 0;
		while (written < endSize) {
			for (int index = out.length - 1; index >= 0; index--) {
				out[index] = (byte) (endSize - written - out.length + index);
			}
			if ((written + out.length) > endSize) {
				ra.seek(0);
				ra.write(out, out.length - endSize + written, endSize - written);
			} else {
				ra.seek(endSize - written - out.length);
				ra.write(out, 0, out.length);
			}
			written += out.length;
		}
		ra.close();
		//
		byte[] bytes = getBytes(ra);
		assertThat(bytes.length, equalTo(endSize));
		for (int i = 0; i < endSize; i++) {
			assertThat(bytes[i], equalTo((byte) i));
		}
	}

	@Test
	public void testReverseWriteByte() throws IOException {
		IRandomAccess ra = getRandomAccess();
		ra.setLength(0);
		for (int i = endSize - 1; i >= 0; i--) {
			ra.seek(i);
			ra.write(i);
		}
		ra.close();
		//
		byte[] bytes = getBytes(ra);
		assertThat(bytes.length, equalTo(endSize));
		for (int i = 0; i < endSize; i++) {
			assertThat(bytes[i], equalTo((byte) i));
		}
	}

	/**
	 * we must assert that write buffers are not dropped
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSeekReadWrite() throws Exception {
		IRandomAccess ra = getRandomAccess();
		ra.seek(ra.getLength() - 5);
		ra.read();
		ra.seek(0);
		ra.write(0xff);
		ra.seek(ra.getLength() - 5);
		ra.read();
		ra.seek(0);
		int value = ra.read();
		ra.close();
		assertThat(value, equalTo(0xff));
	}

	/**
	 * we must assert that seeking does not result in copying invalid buffer
	 * areas
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSeekWrite() throws Exception {
		IRandomAccess ra = getRandomAccess();
		ra.seek(ra.getLength() - 5);
		ra.read();
		ra.seek(0);
		ra.write(0xff);
		ra.seek(2);
		ra.write(0xff);
		ra.close();
		byte[] bytes = getBytes(ra);
		assertThat(bytes[0], equalTo((byte) 0xff));
		assertThat(bytes[1], equalTo((byte) 1));
		assertThat(bytes[2], equalTo((byte) 0xff));
	}

	@Test
	public void testSeekWriteByte() throws IOException {
		IRandomAccess ra = getRandomAccess();
		ra.setLength(0);
		int index = 0;
		while (index < endSize) {
			ra.seek(index);
			ra.write(index);
			index += STEP;
		}
		ra.close();
		//
		byte[] bytes = getBytes(ra);
		assertTrue(bytes.length == index - STEP + 1);
		index = 0;
		while (index < endSize) {
			assertThat(bytes[index], equalTo((byte) index));
			index += STEP;
		}
	}

	@Test
	public void testSequentialWriteBlock() throws IOException {
		IRandomAccess ra = getRandomAccess();
		ra.setLength(0);
		byte[] out = new byte[10];
		int written = 0;
		while (written < endSize) {
			for (int index = 0; index < out.length; index++) {
				out[index] = (byte) (written + index);
			}
			if ((written + out.length) > endSize) {
				ra.write(out, 0, endSize - written);
			} else {
				ra.write(out, 0, out.length);
			}
			written += out.length;
		}
		ra.close();
		//
		byte[] bytes = getBytes(ra);
		assertThat(bytes.length, equalTo(endSize));
		for (int i = 0; i < endSize; i++) {
			assertThat(bytes[i], equalTo((byte) i));
		}
	}

	@Test
	public void testSequentialWriteByte() throws IOException {
		IRandomAccess ra = getRandomAccess();
		ra.setLength(0);
		for (int i = 0; i < endSize; i++) {
			ra.write(i);
		}
		ra.close();
		//
		byte[] bytes = getBytes(ra);
		assertTrue(bytes.length == endSize);
		for (int i = 0; i < endSize; i++) {
			assertThat(bytes[i], equalTo((byte) i));
		}
	}
}
