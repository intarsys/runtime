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
package de.intarsys.tools.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import de.intarsys.tools.stream.StreamTools;
import junit.framework.TestCase;

public class TestXMLEncodeReader extends TestCase {
	public TestXMLEncodeReader() {
		super();
	}

	public TestXMLEncodeReader(String name) {
		super(name);
	}

	protected void check(String in, String out) throws IOException {
		Reader reader;
		Reader xmlReader;
		Writer writer;

		reader = new StringReader(in);
		xmlReader = new XMLEncodeSpecialChars(reader);
		writer = new StringWriter();
		StreamTools.copy(xmlReader, writer);
		assertTrue("in " + in, writer.toString().equals(out));
	}

	public void testString() throws IOException {
		check("", "");
		check("test", "test");
		check("test test", "test test");
		check("<", "&lt;");
		check(">", "&gt;");
		check("&", "&amp;");
		check("\"", "&quot;");
		check("<<", "&lt;&lt;");
		check(">>", "&gt;&gt;");
		check("&&", "&amp;&amp;");
		check("\"\"", "&quot;&quot;");
		check("test<", "test&lt;");
		check("test>", "test&gt;");
		check("test&", "test&amp;");
		check("test\"", "test&quot;");
		check("<test<", "&lt;test&lt;");
		check(">test>", "&gt;test&gt;");
		check("&test&", "&amp;test&amp;");
		check("\"test\"", "&quot;test&quot;");
	}
}
