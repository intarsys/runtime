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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestRandomAccessRead {

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
				{ supplierRandomAccessFile(), 256, 1 }, //
				{ supplierRandomAccessFile(), 256, 2 }, //
				{ supplierRandomAccessFile(), 256, 5 }, //
				{ supplierRandomAccessFile(), 256, 10 }, //
				{ supplierBufferedRandomAccessFile(1), 256, 1 }, //
				{ supplierBufferedRandomAccessFile(1), 256, 2 }, //
				{ supplierBufferedRandomAccessFile(1), 256, 5 }, //
				{ supplierBufferedRandomAccessFile(1), 256, 10 }, //
				{ supplierBufferedRandomAccessFile(2), 256, 1 }, //
				{ supplierBufferedRandomAccessFile(2), 256, 2 }, //
				{ supplierBufferedRandomAccessFile(2), 256, 5 }, //
				{ supplierBufferedRandomAccessFile(2), 256, 10 }, //
				{ supplierBufferedRandomAccessFile(2048), 256, 1 }, //
				{ supplierBufferedRandomAccessFile(2048), 256, 2 }, //
				{ supplierBufferedRandomAccessFile(2048), 256, 5 }, //
				{ supplierBufferedRandomAccessFile(2048), 256, 10 }, //
				{ supplierRandomAccessByteArray(), 256, 1 }, //
				{ supplierRandomAccessByteArray(), 256, 2 }, //
				{ supplierRandomAccessByteArray(), 256, 5 }, //
				{ supplierRandomAccessByteArray(), 256, 10 }, //
		};
	}

	protected static Function<Long, IRandomAccess> supplierBufferedRandomAccessFile(int bufferSize) {
		return (fileSize) -> {
			try {
				return new BufferedRandomAccess(new RandomAccessFile(createFile(fileSize)), bufferSize);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		};
	}

	protected static Function<Long, IRandomAccess> supplierRandomAccessByteArray() {
		return (fileSize) -> {
			return new RandomAccessByteArray(createBytes(fileSize));
		};
	}

	protected static Function<Long, IRandomAccess> supplierRandomAccessFile() {
		return (fileSize) -> {
			try {
				return new RandomAccessFile(createFile(fileSize));
			} catch (IOException e) {
				throw new RuntimeException();
			}
		};
	}

	private Function<Long, IRandomAccess> randomAccessSupplier;

	private int blockLength;

	private long fileSize;

	public TestRandomAccessRead(Function<Long, IRandomAccess> randomAccessSupplier, long fileSize, int blockLength) {
		super();
		this.randomAccessSupplier = randomAccessSupplier;
		this.fileSize = fileSize;
		this.blockLength = blockLength;
	}

	public int getLength() {
		return blockLength;
	}

	public IRandomAccess getRandomAccess() {
		return randomAccessSupplier.apply(fileSize);
	}

	@Test
	public void testForwardReadBlock() throws IOException {
		byte[] bytes = new byte[(int) fileSize + blockLength];
		IRandomAccess ra = getRandomAccess();
		int bytesRead = 0;
		while (bytesRead < fileSize) {
			int count = ra.read(bytes, bytesRead, blockLength);
			if (count == -1) {
				break;
			}
			bytesRead += count;
		}
		ra.close();
		//
		assertThat(bytesRead, is((int) fileSize));
		for (int index = 0; index < fileSize; index++) {
			assertThat(bytes[index], is((byte) index));
		}
	}

	@Test
	public void testForwardReadByte() throws IOException {
		byte[] bytes = new byte[(int) fileSize];
		IRandomAccess ra = getRandomAccess();
		int bytesRead = 0;
		while (bytesRead < fileSize) {
			int value = ra.read();
			if (value == -1) {
				break;
			}
			bytes[bytesRead] = (byte) value;
			bytesRead += 1;
		}
		ra.close();
		//
		assertThat(bytesRead, is((int) fileSize));
		for (int index = 0; index < fileSize; index++) {
			assertThat(bytes[index], is((byte) index));
		}
	}

	@Test
	public void testReverseReadBlock() throws IOException {
		byte[] bytes = new byte[(int) fileSize + blockLength];
		IRandomAccess ra = getRandomAccess();
		int bytesRead = 0;
		int seek = (int) (fileSize - 1);
		while (bytesRead < fileSize) {
			if (seek < 0) {
				bytesRead += seek;
				seek = 0;
			}
			ra.seek(seek);
			int count = ra.read(bytes, seek, blockLength);
			if (count == -1) {
				break;
			}
			bytesRead += count;
			seek -= blockLength;
		}
		ra.close();
		//
		assertThat(bytesRead, is((int) fileSize));
		for (int index = 0; index < fileSize; index++) {
			assertThat(bytes[index], is((byte) index));
		}
	}

	@Test
	public void testReverseReadByte() throws IOException {
		byte[] bytes = new byte[(int) fileSize];
		IRandomAccess ra = getRandomAccess();
		int bytesRead = 0;
		int seek = (int) (fileSize - 1);
		while (bytesRead < fileSize) {
			ra.seek(seek);
			int value = ra.read();
			if (value == -1) {
				break;
			}
			bytes[seek] = (byte) value;
			bytesRead += 1;
			seek -= 1;
		}
		ra.close();
		//
		assertThat(bytesRead, is((int) fileSize));
		for (int index = 0; index < (int) fileSize; index++) {
			assertThat(bytes[index], is((byte) index));
		}
	}
}
