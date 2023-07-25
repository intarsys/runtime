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
package de.intarsys.tools.collection;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.randomaccess.RandomAccessByteArray;

@SuppressWarnings({ "MagicNumber" })
public class TestByteArrayTools {

	public TestByteArrayTools() {
		super();
	}

	@Test
	public void testBigInteger() {
		byte[] bytes;
		byte[] ref;
		BigInteger value;
		//
		value = new BigInteger(new byte[] { (byte) 0x00 });
		bytes = ByteArrayTools.toBytes(value, 0);
		Assert.assertTrue(bytes.length == 0);

		value = new BigInteger(new byte[] { (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 0);
		Assert.assertTrue(bytes.length == 0);

		value = new BigInteger(new byte[] { (byte) 0xff });
		bytes = ByteArrayTools.toBytes(value, 0);
		Assert.assertTrue(bytes.length == 0);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0x00 });
		bytes = ByteArrayTools.toBytes(value, 0);
		Assert.assertTrue(bytes.length == 0);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 0);
		Assert.assertTrue(bytes.length == 0);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0xff });
		bytes = ByteArrayTools.toBytes(value, 0);
		Assert.assertTrue(bytes.length == 0);

		// ************

		value = new BigInteger(new byte[] { (byte) 0x00 });
		bytes = ByteArrayTools.toBytes(value, 1);
		Assert.assertTrue(bytes.length == 1);

		value = new BigInteger(new byte[] { (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 1);
		Assert.assertTrue(bytes.length == 1);

		value = new BigInteger(new byte[] { (byte) 0xff });
		bytes = ByteArrayTools.toBytes(value, 1);
		Assert.assertTrue(bytes.length == 1);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0x00 });
		bytes = ByteArrayTools.toBytes(value, 1);
		ref = new byte[] { (byte) 0x00 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 1);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 1);
		ref = new byte[] { (byte) 0x01 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 1);

		value = new BigInteger(new byte[] { (byte) 0xff, (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 1);
		ref = new byte[] { (byte) 0x01 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 1);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0xff });
		bytes = ByteArrayTools.toBytes(value, 1);
		ref = new byte[] { (byte) 0xff };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 1);

		// ************

		value = new BigInteger(new byte[] { (byte) 0x00 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0x00, (byte) 0x00 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

		value = new BigInteger(new byte[] { (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0x00, (byte) 0x01 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

		value = new BigInteger(new byte[] { (byte) 0xf0 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0xff, (byte) 0xf0 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0x00 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0x00, (byte) 0x00 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0x00, (byte) 0x01 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

		value = new BigInteger(new byte[] { (byte) 0x00, (byte) 0xf0 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0x00, (byte) 0xf0 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

		value = new BigInteger(new byte[] { (byte) 0xFF, (byte) 0x01 });
		bytes = ByteArrayTools.toBytes(value, 2);
		ref = new byte[] { (byte) 0xff, (byte) 0x01 };
		Assert.assertTrue(Arrays.equals(bytes, ref));
		Assert.assertTrue(bytes.length == 2);

	}

	@Test
	public void testIndex() {
		byte[] source = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		byte[] pattern;
		int index;
		//
		pattern = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		index = ByteArrayTools.indexOf(source, 0, source.length, pattern, 0, pattern.length, 0);
		Assert.assertTrue(index == 0);
		//
		pattern = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		index = ByteArrayTools.indexOf(source, 0, source.length, pattern, 0, pattern.length, 0);
		Assert.assertTrue(index == 0);
		//
		pattern = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		index = ByteArrayTools.indexOf(source, 0, source.length, pattern, 0, pattern.length, 0);
		Assert.assertTrue(index == 1);
		//
		pattern = new byte[] { 0 };
		index = ByteArrayTools.indexOf(source, 0, source.length, pattern, 0, pattern.length, 0);
		Assert.assertTrue(index == 0);
		//
		pattern = new byte[] { 1 };
		index = ByteArrayTools.indexOf(source, 0, source.length, pattern, 0, pattern.length, 0);
		Assert.assertTrue(index == 1);
		//
		pattern = new byte[] { 9 };
		index = ByteArrayTools.indexOf(source, 0, source.length, pattern, 0, pattern.length, 0);
		Assert.assertTrue(index == 9);
	}

	@Test
	public void toBigEndianInt() {
		byte[] bytes;
		int value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0xff, (byte) 0xff };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0x01, (byte) 0xff };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 255 + 1 * 256);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianInt(bytes, 2, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
	}

	@Test
	public void toBigEndianIntUnsigned() {
		byte[] bytes;
		int value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, 0x00 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255 * 256);
		//
		bytes = new byte[] { (byte) 0xff, 0x01 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1 + 255 * 256);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianIntUnsigned(bytes, 2, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
	}

	@Test
	public void toBigEndianLong() {
		byte[] bytes;
		long value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0xff, (byte) 0xff };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0x01, (byte) 0xff };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 255 + 1 * 256);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianLong(bytes, 2, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
	}

	@Test
	public void toBigEndianLongUnsigned() {
		byte[] bytes;
		long value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, 0x00 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255 * 256);
		//
		bytes = new byte[] { (byte) 0xff, 0x01 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1 + 255 * 256);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toBigEndianLongUnsigned(bytes, 2, 4);
		Assert.assertTrue(value == 4 + 3 * 256 + 2 * 256 * 256 + 1 * 256 * 256 * 256);
	}

	@Test
	public void toBytesBigEndianInt() throws Exception {
		byte[] bytes;
		//
		bytes = ByteArrayTools.toBytesBigEndian(0, 0);
		assertArrayEquals(new byte[] {}, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(0, 1);
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(0, 2);
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(1, 1);
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(1, 2);
		assertArrayEquals(new byte[] { 0x00, 0x01 }, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(0x01020304, 2);
		assertArrayEquals(new byte[] { 0x03, 0x04 }, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(0x01020304, 4);
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		bytes = ByteArrayTools.toBytesBigEndian(0x01020304, 8);
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04 }, bytes);
	}

	@Test
	public void toBytesBigEndianShortest() {
		byte[] bytes;
		int value;
		//
		value = 0;
		bytes = ByteArrayTools.toBytesBigEndian(value);
		Assert.assertTrue(Arrays.equals(bytes, new byte[] { 0 }));
		//
		value = 1;
		bytes = ByteArrayTools.toBytesBigEndian(value);
		Assert.assertTrue(Arrays.equals(bytes, new byte[] { 1 }));
		//
		value = 255;
		bytes = ByteArrayTools.toBytesBigEndian(value);
		Assert.assertTrue(Arrays.equals(bytes, new byte[] { (byte) 0xff }));
		//
		value = 256;
		bytes = ByteArrayTools.toBytesBigEndian(value);
		Assert.assertTrue(Arrays.equals(bytes, new byte[] { (byte) 0x01, (byte) 0x00 }));
		//
		value = 511;
		bytes = ByteArrayTools.toBytesBigEndian(value);
		Assert.assertTrue(Arrays.equals(bytes, new byte[] { (byte) 0x01, (byte) 0xff }));
	}

	@Test
	public void toLittleEndianInt() {
		byte[] bytes;
		int value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0xff, 0x00 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, 0x01 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 511);
		//
		bytes = new byte[] { (byte) 0xff, (byte) 0xff };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 0, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianInt(bytes, 2, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
	}

	@Test
	public void toLittleEndianIntUnsigned() {
		byte[] bytes;
		int value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, 0x00 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, 0x01 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 511);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianIntUnsigned(bytes, 2, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
	}

	@Test
	public void toLittleEndianLong() {
		byte[] bytes;
		long value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0xff, 0x00 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, (byte) 0xff };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == -1);
		//
		bytes = new byte[] { (byte) 0xff, 0x01 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 511);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 0, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianLong(bytes, 2, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
	}

	@Test
	public void toLittleEndianLongUnsigned() {
		byte[] bytes;
		long value;
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 1);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] {};
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 0 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 0);
		Assert.assertTrue(value == 0);
		//
		bytes = new byte[] { 1 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1);
		//
		bytes = new byte[] { (byte) 0xff };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, 0x00 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255);
		//
		bytes = new byte[] { (byte) 0xff, (byte) 0xff };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 255 + 255 * 256);
		//
		bytes = new byte[] { (byte) 0xff, 0x01 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 511);
		//
		bytes = new byte[] { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 0, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
		//
		bytes = new byte[] { (byte) 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
		value = ByteArrayTools.toLittleEndianLongUnsigned(bytes, 2, 4);
		Assert.assertTrue(value == 1 + 2 * 256 + 3 * 256 * 256 + 4 * 256 * 256 * 256);
	}

	@Test
	public void writeBigEndianRandomInt() throws Exception {
		RandomAccessByteArray random;
		byte[] bytes;
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0, 0);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 1, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 1, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x01020304, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x03, 0x04 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x01020304, 4);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x01020304, 8);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04 }, bytes);
	}

	@Test
	public void writeBigEndianRandomLong() throws Exception {
		RandomAccessByteArray random;
		byte[] bytes;
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0L, 0);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0L, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0L, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 1L, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 1L, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x01020304L, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x03, 0x04 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x01020304L, 4);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x01020304L, 8);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x0102030405060708L, 8);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeBigEndian(random, 0x0102030405060708L, 16);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 }, bytes);
	}

	@Test
	public void writeBigEndianStreamInt() throws Exception {
		ByteArrayOutputStream os;
		byte[] bytes;
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0, 0);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 1, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 1, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x01020304, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x03, 0x04 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x01020304, 4);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x01020304, 8);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04 }, bytes);
	}

	@Test
	public void writeBigEndianStreamLong() throws Exception {
		ByteArrayOutputStream os;
		byte[] bytes;
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0L, 0);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0L, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0L, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 1L, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 1L, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x01020304L, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x03, 0x04 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x01020304L, 4);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x01020304L, 8);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x0102030405060708L, 8);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeBigEndian(os, 0x0102030405060708L, 16);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 }, bytes);
	}

	@Test
	public void writeLittleEndianRandomInt() throws Exception {
		RandomAccessByteArray random;
		byte[] bytes;
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0, 0);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 1, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 1, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0x01020304, 4);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0x01020304, 8);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00 }, bytes);
	}

	@Test
	public void writeLittleEndianRandomLong() throws Exception {
		RandomAccessByteArray random;
		byte[] bytes;
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0L, 0);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0L, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0L, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 1L, 1);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 1L, 2);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0x01020304L, 4);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0x01020304L, 8);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0x0102030405060708L, 8);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01 }, bytes);
		//
		random = new RandomAccessByteArray();
		ByteArrayTools.writeLittleEndian(random, 0x0102030405060708L, 16);
		bytes = random.toByteArray();
		assertArrayEquals(new byte[] { 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 }, bytes);
	}

	@Test
	public void writeLittleEndianStreamInt() throws Exception {
		ByteArrayOutputStream os;
		byte[] bytes;
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0, 0);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 1, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 1, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0x01020304, 4);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0x01020304, 8);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00 }, bytes);
	}

	@Test
	public void writeLittleEndianStreamLong() throws Exception {
		ByteArrayOutputStream os;
		byte[] bytes;
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0L, 0);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] {}, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0L, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0L, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 1L, 1);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 1L, 2);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x01, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0x01020304L, 4);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0x01020304L, 8);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0x0102030405060708L, 8);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01 }, bytes);
		//
		os = new ByteArrayOutputStream();
		ByteArrayTools.writeLittleEndian(os, 0x0102030405060708L, 16);
		bytes = os.toByteArray();
		assertArrayEquals(new byte[] { 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 }, bytes);
	}
}
