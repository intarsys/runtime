package de.intarsys.tools.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import de.intarsys.tools.stream.StreamTools;
import junit.framework.TestCase;

public class TestUnEscapeReader extends TestCase {

	public void testIgnoreReader() throws IOException {
		String out;
		Reader ir;
		Reader er;
		//
		ir = new StringReader("");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("".equals(out));
		//
		ir = new StringReader("a");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("a".equals(out));
		//
		ir = new StringReader("ab");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("ab".equals(out));
		//
		ir = new StringReader("\\\\");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("\\".equals(out));
		//
		ir = new StringReader("\\n");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("\n".equals(out));
		//
		ir = new StringReader("a");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("a".equals(out));
		//
		ir = new StringReader("\\a");
		er = new UnEscapeReader(ir, true);
		out = StreamTools.getString(er);
		assertTrue("a".equals(out));
	}

	public void testPlainReader() throws IOException {
		String out;
		Reader ir;
		Reader er;
		//
		ir = new StringReader("");
		er = new UnEscapeReader(ir);
		out = StreamTools.getString(er);
		assertTrue("".equals(out));
		//
		ir = new StringReader("a");
		er = new UnEscapeReader(ir);
		out = StreamTools.getString(er);
		assertTrue("a".equals(out));
		//
		ir = new StringReader("ab");
		er = new UnEscapeReader(ir);
		out = StreamTools.getString(er);
		assertTrue("ab".equals(out));
		//
		ir = new StringReader("\\\\");
		er = new UnEscapeReader(ir);
		out = StreamTools.getString(er);
		assertTrue("\\".equals(out));
		//
		ir = new StringReader("\\n");
		er = new UnEscapeReader(ir);
		out = StreamTools.getString(er);
		assertTrue("\n".equals(out));
		//
		ir = new StringReader("a");
		er = new UnEscapeReader(ir);
		out = StreamTools.getString(er);
		assertTrue("a".equals(out));
	}
}
