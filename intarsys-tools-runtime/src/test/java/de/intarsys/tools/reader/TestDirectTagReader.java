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
package de.intarsys.tools.reader;

import java.io.IOException;
import java.io.StringReader;

import de.intarsys.tools.stream.StreamTools;
import junit.framework.TestCase;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestDirectTagReader extends TestCase {

	public void testDouble() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return "ok";
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "${diedel}${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("okok"));
		//
		string = "${diedel} doedel ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("ok doedel ok"));
	}

	public void testEmpty() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return null;
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		string = "";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals(""));
	}

	public void testEscape() {
		String value;
		String result;
		//
		value = "test";
		result = DirectTagReader.escape(value);
		assertTrue(result.equals("test"));
		//
		value = "${test}";
		result = DirectTagReader.escape(value);
		assertTrue(result.equals("${${}test}"));
		//
		value = "${test}test${test}";
		result = DirectTagReader.escape(value);
		assertTrue(result.equals("${${}test}test${${}test}"));
	}

	public void testEscaped() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return "ok";
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "alles ${${}diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles ${diedel}"));
		//
		string = "alles ${di\\}edel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles ok"));
	}

	public void testNested1() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				if ("diedel".equals(tagContent)) {
					return "${";
				}
				throw new RuntimeException();
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "alles ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${"));
		//
		string = "alles ${diedel}${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${${"));
		//
		string = "alles ${diedel}test}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${test}"));
	}

	public void testNested2() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				if ("diedel".equals(tagContent)) {
					return "$";
				}
				throw new RuntimeException();
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "alles ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles $"));
		//
		string = "alles ${diedel}${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles $$"));
		//
		string = "alles ${diedel}{test}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${test}"));
	}

	public void testNested3() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				if ("diedel".equals(tagContent)) {
					return "${doedel}";
				}
				throw new RuntimeException();
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "alles ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${doedel}"));
		//
		string = "alles ${diedel}${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${doedel}${doedel}"));
		//
		string = "alles ${diedel}{test}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("alles ${doedel}{test}"));
	}

	public void testNoForceString() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		final Object marker = new Object() {
			@Override
			public String toString() {
				return "oops";
			}
		};
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				if ("diedel".equals(tagContent)) {
					return marker;
				}
				if ("doedel".equals(tagContent)) {
					return null;
				}
				if ("foo".equals(tagContent)) {
					return "bar";
				}
				throw new RuntimeException();
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals(""));
		assertTrue(!reader.hasResolvedObject());
		assertTrue(reader.getResolvedObject() == null);
		//
		string = "${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals(""));
		assertTrue(reader.hasResolvedObject());
		assertTrue(reader.getResolvedObject() == marker);
		//
		string = "${doedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals(""));
		assertTrue(reader.hasResolvedObject());
		assertTrue(reader.getResolvedObject() == null);
		//
		string = "${foo}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("bar"));
		assertTrue(reader.hasResolvedObject());
		assertTrue("bar".equals(reader.getResolvedObject()));
		//
		string = " ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals(" oops"));
		assertTrue(reader.hasResolvedObject());
		assertTrue(reader.getResolvedObject() == marker);
		//
		string = "${diedel} ";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("oops "));
		assertTrue(reader.hasResolvedObject());
		assertTrue(reader.getResolvedObject() == marker);
		//
		string = "${diedel}${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		reader.setForceToString(false);
		result = StreamTools.getString(reader);
		assertTrue(result.equals("oopsoops"));
		assertTrue(reader.hasResolvedObject());
		assertTrue(reader.getResolvedObject() == marker);
	}

	public void testNoReply() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return "";
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "alles ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles "));
		//
		string = "${diedel} doedel";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals(" doedel"));
		//
		string = "${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals(""));
		//
		string = "${diedel}${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals(""));
	}

	public void testNoTag() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return null;
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		string = "abc";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("abc"));
	}

	public void testPartial() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return "ok";
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "$";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("$"));
		//
		string = "alles $${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles $ok"));
		//
		string = "alles ${diedel}$";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles ok$"));
		//
		string = "alles $${diedel}{x";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles $ok{x"));
	}

	public void testTag() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return "ok";
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};
		//
		string = "alles ${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("alles ok"));
		//
		string = "${diedel} doedel";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("ok doedel"));
		//
		string = "${diedel}";
		sr = new StringReader(string);
		reader = new DirectTagReader(sr, handler, null);
		result = StreamTools.getString(reader);
		//
		assertTrue(result.equals("ok"));
	}

	public void testUnclosed() throws IOException {
		String string;
		String result;
		StringReader sr;
		DirectTagReader reader;
		IDirectTagHandler handler;
		//
		handler = new IDirectTagHandler() {
			@Override
			public Object endTag(String tagContent, Object context) {
				return "ok";
			}

			@Override
			public void setLocationProvider(ILocationProvider location) {
				// ignore
			}

			@Override
			public void startTag() {
				//
			}
		};

		//
		try {
			string = "alles ${diedel";
			sr = new StringReader(string);
			reader = new DirectTagReader(sr, handler, null);
			result = StreamTools.getString(reader);
			fail("should throw io execption");
		} catch (IOException e) {
			// everything fine
		}

		//
		try {
			string = "${diede";
			sr = new StringReader(string);
			reader = new DirectTagReader(sr, handler, null);
			result = StreamTools.getString(reader);
			fail("should throw io execption");
		} catch (IOException e) {
			// everything fine
		}
	}
}
