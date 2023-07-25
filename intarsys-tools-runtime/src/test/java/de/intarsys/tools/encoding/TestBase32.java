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
package de.intarsys.tools.encoding;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * 
 */
public class TestBase32 extends TestCase {

	public void testBasic() {
		byte[] inputBytes;
		String outputBytes;
		byte[] tempBytes;
		//
		inputBytes = new byte[] {};
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { 0 };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { 0, 0 };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { 0, 0, 0 };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { 0, 0, 0, 0 };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { 0, 0, 0, 0, 0 };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { (byte) 0xff };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { (byte) 0xff, (byte) 0xff };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
		//
		inputBytes = "foo\\bar".getBytes();
		outputBytes = Base32.encode(inputBytes);
		tempBytes = Base32.decode(outputBytes);
		assertTrue(Arrays.equals(inputBytes, tempBytes));
	}

}
