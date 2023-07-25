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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.intarsys.tools.hex.HexTools;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class TestBufferedRandomAccess {

	@Test
	public void testReadSetLength() throws Exception {
		RandomAccessByteArray ra;
		BufferedRandomAccess bra;
		int value;
		//
		ra = new RandomAccessByteArray(HexTools.hexStringToBytes("00010203040506070809"));
		bra = new BufferedRandomAccess(ra, 4);
		assertThat(bra.getTotalOffset(), is(0L));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(1L));
		assertThat(value, is(0));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(2L));
		assertThat(value, is(1));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(3L));
		assertThat(value, is(2));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(4L));
		assertThat(value, is(3));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(5L));
		assertThat(value, is(4));
		bra.setLength(3);
		assertThat(bra.getTotalOffset(), is(3L));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(3L));
		assertThat(value, is(-1));
		//
		bra.close();
	}

	/**
	 * we must assert that seeking does not result in copying invalid buffer
	 * areas
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSeekWrite() throws Exception {
		RandomAccessByteArray ra;
		BufferedRandomAccess bra;
		int value;
		//
		ra = new RandomAccessByteArray(HexTools.hexStringToBytes("00010203040506070809"));
		bra = new BufferedRandomAccess(ra, 4);
		bra.seek(6);
		bra.read();
		bra.seek(0);
		bra.write(0xff);
		bra.seek(2);
		bra.write(0xff);
		bra.close();
		assertThat(ra.toByteArray(), equalTo(HexTools.hexStringToBytes("FF01FF03040506070809")));
	}

	@Test
	public void testWriteSetLengthAboveByteOffset() throws Exception {
		RandomAccessByteArray ra;
		BufferedRandomAccess bra;
		int value;
		//
		ra = new RandomAccessByteArray(HexTools.hexStringToBytes("00010203040506070809"));
		bra = new BufferedRandomAccess(ra, 4);
		assertThat(bra.getTotalOffset(), is(0L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(1L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(2L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(3L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(4L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(5L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(6L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(7L));
		assertThat(bra.getBytesOffset(), is(4L));
		bra.setLength(5);
		assertThat(bra.getTotalOffset(), is(5L));
		assertThat(bra.getBytesOffset(), is(4L));
		bra.seek(0);
		assertThat(bra.getTotalOffset(), is(0L));
		value = bra.read();
		assertThat(bra.getBytesOffset(), is(0L));
		assertThat(bra.getTotalOffset(), is(1L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(2L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(3L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(4L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(5L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(5L));
		assertThat(value, is(-1));
		//
		bra.close();
	}

	@Test
	public void testWriteSetLengthBelowByteOffset() throws Exception {
		RandomAccessByteArray ra;
		BufferedRandomAccess bra;
		int value;
		//
		ra = new RandomAccessByteArray(HexTools.hexStringToBytes("00010203040506070809"));
		bra = new BufferedRandomAccess(ra, 4);
		assertThat(bra.getTotalOffset(), is(0L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(1L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(2L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(3L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(4L));
		bra.write(0xff);
		assertThat(bra.getTotalOffset(), is(5L));
		bra.setLength(3);
		assertThat(bra.getTotalOffset(), is(3L));
		bra.seek(0);
		assertThat(bra.getTotalOffset(), is(0L));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(1L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(2L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(3L));
		assertThat(value, is(0xff));
		value = bra.read();
		assertThat(bra.getTotalOffset(), is(3L));
		assertThat(value, is(-1));
		//
		bra.close();
	}
}
