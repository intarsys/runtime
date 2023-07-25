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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

@SuppressWarnings({ "MagicNumber" })
public class TestRandomAccess extends TestCase {
	public static final byte[] TESTBYTES1;

	public static final byte[] TESTBYTES2;

	static {
		TESTBYTES1 = new byte[256];
		for (int i = 0; i < TESTBYTES1.length; i++) {
			TESTBYTES1[i] = (byte) i;
		}

		TESTBYTES2 = new byte[256];
		Arrays.fill(TESTBYTES2, (byte) 7);
	}

	public TestRandomAccess() {
		super();
	}

	public TestRandomAccess(String name) {
		super(name);
	}

	public void randomAccessTest(IRandomAccess data) throws IOException {
		assertEquals(0, data.getOffset());
		assertEquals(256, data.getLength());

		// read tests
		byte[] buffer = new byte[256];
		data.read(buffer);
		assertEquals(true, Arrays.equals(buffer, TESTBYTES1));

		data.seek(0);
		assertEquals(0, data.read());

		data.seek(data.getLength() - 1);
		assertEquals(255, data.read());

		// read more than available
		assertEquals(-1, data.read());

		if (!data.isReadOnly()) {
			// truncate data
			data.setLength(0);
			assertEquals(0, data.getLength());

			// write twice the data as before
			data.write(TESTBYTES2);
			data.write(TESTBYTES2);
			data.seek(0);
			data.read(buffer);
			assertEquals(true, Arrays.equals(buffer, TESTBYTES2));
			data.read(buffer);
			assertEquals(true, Arrays.equals(buffer, TESTBYTES2));
		}
		data.close();
	}

	public void testRandomAccessByteArray() throws IOException {
		randomAccessTest(new RandomAccessByteArray(Arrays.copyOf(TESTBYTES1, TESTBYTES1.length)));
	}

	public void testRandomAccessFile() throws IOException {
		File file = File.createTempFile("randomAccessTest", ".bin");
		file.deleteOnExit();
		OutputStream out = new FileOutputStream(file);
		out.write(TESTBYTES1);
		out.close();

		randomAccessTest(new RandomAccessFile(file));
	}
}
