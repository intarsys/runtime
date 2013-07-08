/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import de.intarsys.tools.stream.StreamTools;

public class XMLTools {

	public static String decodeSpecialChars(String encodedString)
			throws IOException {
		EntityDecoder decoder = new EntityDecoder(new StringReader(
				encodedString), false);
		try {
			return StreamTools.toString(decoder);
		} finally {
			StreamTools.close(decoder);
		}
	}

	public static String encodeDefault(String plainString) {
		Reader in = new HTMLEncodeLineBreaks(new XMLEncodeSpecialChars(
				new StringReader(plainString)));
		Writer out = new StringWriter();
		try {
			StreamTools.copyEncoded(in, out);
		} catch (IOException e) {
			// these readers do not produce such errors.
		}
		return out.toString();
	}

	public static String encodeLineBreaks(String plainString) {
		Reader in = new HTMLEncodeLineBreaks(new StringReader(plainString));
		Writer out = new StringWriter();
		try {
			StreamTools.copyEncoded(in, out);
		} catch (IOException e) {
			// these readers do not produce such errors.
		}
		return out.toString();
	}

	public static String encodeSpecialChars(String plainString) {
		Reader in = new XMLEncodeSpecialChars(new StringReader(plainString));
		Writer out = new StringWriter();
		try {
			StreamTools.copyEncoded(in, out);
		} catch (IOException e) {
			// these readers do not produce such errors.
		}
		return out.toString();
	}

	/**
	 * This is a non instantiable tool class
	 */
	private XMLTools() {
		super();
	}
}
