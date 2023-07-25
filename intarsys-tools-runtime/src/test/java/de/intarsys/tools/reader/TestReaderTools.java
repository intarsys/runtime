package de.intarsys.tools.reader;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.junit.Test;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.Token;

public class TestReaderTools {

	@Test
	public void testCreateReaderScanBom() throws IOException {
		byte[] bytes;
		InputStream is;
		InputStreamReader reader;
		String encoding;
		String value;
		//
		bytes = new byte[0];
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		assertTrue(reader == null);
		//
		bytes = "test".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		assertTrue(reader == null);
		//
		// utf 32 be
		bytes = HexTools.hexStringToBytes("0000feff");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF-32BE".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("0000feff"), value.getBytes("UTF-32BE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF-32BE".equals(encoding));
		//
		// utf 32 le
		bytes = HexTools.hexStringToBytes("fffe0000");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF-32LE".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("fffe0000"), value.getBytes("UTF-32LE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF-32LE".equals(encoding));
		//
		// utf 16 be
		bytes = HexTools.hexStringToBytes("feff");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UnicodeBigUnmarked".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("feff"), value.getBytes("UTF-16BE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UnicodeBigUnmarked".equals(encoding));
		//
		// utf 16 le
		bytes = HexTools.hexStringToBytes("fffe");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UnicodeLittleUnmarked".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("fffe"), value.getBytes("UTF-16LE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UnicodeLittleUnmarked".equals(encoding));
		//
		// utf 8
		bytes = HexTools.hexStringToBytes("efbbbf");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF8".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("efbbbf"), value.getBytes("UTF-8"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanBom(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF8".equals(encoding));
	}

	@Test
	public void testCreateReaderScanMeta() throws IOException {
		byte[] bytes;
		InputStream is;
		InputStreamReader reader;
		String encoding;
		String value;
		//
		bytes = new byte[0];
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanMeta(is);
		assertTrue(reader == null);
		//
		bytes = "test".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanMeta(is);
		assertTrue(reader == null);
		//
		bytes = "$$$ diedel\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanMeta(is);
		assertTrue(reader == null);
		//
		bytes = "$$$ encoding=ASCII\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanMeta(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("$$$ encoding=ASCII\ntest".equals(value));
		assertTrue("ASCII".equals(encoding));
		//
		bytes = "$$$ encoding=ASCII\n$$$ diedel=doedel\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createReaderScanMeta(is);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("$$$ encoding=ASCII\n$$$ diedel=doedel\ntest".equals(value));
		assertTrue("ASCII".equals(encoding));
	}

	@Test
	public void testCreateTaggedReader() throws IOException {
		byte[] bytes;
		InputStream is;
		TaggedReader reader;
		String encoding;
		String value;
		//
		bytes = new byte[0];
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF8".equals(encoding));
		//
		bytes = "test".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("test".equals(value));
		assertTrue("UTF8".equals(encoding));
		//
		bytes = "$$$ x=y\r\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("test".equals(value));
		assertTrue("UTF8".equals(encoding));
		assertTrue(reader.getProperty("x").equals("y"));
		//
		// utf 32 be
		bytes = HexTools.hexStringToBytes("0000feff");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF-32BE".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("0000feff"), value.getBytes("UTF-32BE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF-32BE".equals(encoding));
		//
		value = "$$$ x=y\r\näöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("0000feff"), value.getBytes("UTF-32BE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF-32BE".equals(encoding));
		assertTrue(reader.getProperty("x").equals("y"));
		//
		// utf 32 le
		bytes = HexTools.hexStringToBytes("fffe0000");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF-32LE".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("fffe0000"), value.getBytes("UTF-32LE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF-32LE".equals(encoding));
		//
		value = "$$$ x=y\r\näöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("fffe0000"), value.getBytes("UTF-32LE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF-32LE".equals(encoding));
		assertTrue(reader.getProperty("x").equals("y"));
		//
		// utf 16 be
		bytes = HexTools.hexStringToBytes("feff");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UnicodeBigUnmarked".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("feff"), value.getBytes("UTF-16BE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UnicodeBigUnmarked".equals(encoding));
		//
		value = "$$$ x=y\r\näöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("feff"), value.getBytes("UTF-16BE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UnicodeBigUnmarked".equals(encoding));
		assertTrue(reader.getProperty("x").equals("y"));
		//
		// utf 16 le
		bytes = HexTools.hexStringToBytes("fffe");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UnicodeLittleUnmarked".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("fffe"), value.getBytes("UTF-16LE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UnicodeLittleUnmarked".equals(encoding));
		//
		value = "$$$ x=y\r\näöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("fffe"), value.getBytes("UTF-16LE"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UnicodeLittleUnmarked".equals(encoding));
		assertTrue(reader.getProperty("x").equals("y"));
		//
		// utf 8
		bytes = HexTools.hexStringToBytes("efbbbf");
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("".equals(value));
		assertTrue("UTF8".equals(encoding));
		//
		value = "äöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("efbbbf"), value.getBytes("UTF-8"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF8".equals(encoding));
		//
		value = "$$$ x=y\r\näöüß€";
		bytes = ByteArrayTools.concat(HexTools.hexStringToBytes("efbbbf"), value.getBytes("UTF-8"));
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("äöüß€".equals(value));
		assertTrue("UTF8".equals(encoding));
		assertTrue(reader.getProperty("x").equals("y"));
		//
		// single byte encoding
		bytes = "$$$ diedel\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("test".equals(value));
		assertTrue("UTF8".equals(encoding));
		assertTrue(reader.getProperty("diedel").equals(""));
		//
		bytes = "$$$ encoding=ASCII\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("test".equals(value));
		assertTrue("ASCII".equals(encoding));
		assertTrue(reader.getProperty("encoding").equals("ASCII"));
		//
		bytes = "$$$ encoding=ASCII\n$$$ diedel=doedel\ntest".getBytes();
		is = new ByteArrayInputStream(bytes);
		reader = ReaderTools.createTaggedReader(is, "UTF-8", 1024);
		encoding = reader.getEncoding();
		value = StreamTools.getString(reader);
		assertTrue("test".equals(value));
		assertTrue("ASCII".equals(encoding));
		assertTrue(reader.getProperty("encoding").equals("ASCII"));
		assertTrue(reader.getProperty("diedel").equals("doedel"));
	}

	@Test
	public void testReadEntryAlternateSeparator() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = "a=b\nx=y";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, '\n');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("b"));
		entry = ReaderTools.readEntry(r, '\n');
		assertTrue(entry.getKey().equals("x"));
		assertTrue(entry.getValue().equals("y"));
		//
		value = "a=b\r\nx=y";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, '\n');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("b"));
		entry = ReaderTools.readEntry(r, '\n');
		assertTrue(entry.getKey().equals("x"));
		assertTrue(entry.getValue().equals("y"));
	}

	@Test
	public void testReadEntryBackslash() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = "a =mit\\slash";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit\\slash"));
		//
		value = "a =mit\\\\drin";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit\\\\drin"));
		//
		value = "a =mit\\;b=x";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit\\"));
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("b"));
		assertTrue(entry.getValue().equals("x"));
	}

	@Test
	public void testReadEntryEmpty() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = "";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry == null);
	}

	@Test
	public void testReadEntryKeyOnly() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = "a";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals(""));
		//
		value = " a";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals(""));
		//
		value = "a ";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals(""));
		//
		value = "x1_ ";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("x1_"));
		assertTrue(entry.getValue().equals(""));
	}

	@Test
	public void testReadEntryKeyValue() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = " a =b";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("b"));
		//
		value = " a = b ";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals(" b "));
	}

	@Test
	public void testReadEntryMultiKeyValue() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = "a1_ = x ;b= y ";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a1_"));
		assertTrue(entry.getValue().equals(" x "));
		//
		value = ";b2_= y ";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey() == null);
		assertTrue(entry.getValue() == null);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("b2_"));
		assertTrue(entry.getValue().equals(" y "));
	}

	@Test
	public void testReadEntryQuoted() throws IOException {
		Reader r;
		String value;
		Map.Entry<String, String> entry;
		//
		value = "a =\"mit\";b=x";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit"));
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("b"));
		assertTrue(entry.getValue().equals("x"));
		//
		value = "a =\"mit\"test;b=x";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit"));
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("b"));
		assertTrue(entry.getValue().equals("x"));
		//
		value = "a =\"mit;c=y\";b=x";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit;c=y"));
		//
		value = "a =\"mit\\\";c=y\";b=x";
		r = new StringReader(value);
		entry = ReaderTools.readEntry(r, ';');
		assertTrue(entry.getKey().equals("a"));
		assertTrue(entry.getValue().equals("mit\";c=y"));
	}

	@Test
	public void testReadMetaEncoding() throws IOException {
		Reader r;
		BufferedReader buffer;
		String input;
		String encoding;
		String output;
		//
		input = "";
		r = new StringReader(input);
		buffer = new BufferedReader(r);
		encoding = ReaderTools.readMetaEncoding(buffer);
		output = StreamTools.getString(buffer);
		assertTrue(encoding == null);
		assertTrue("".equals(output));
		//
		input = "test";
		r = new StringReader(input);
		buffer = new BufferedReader(r);
		encoding = ReaderTools.readMetaEncoding(buffer);
		output = StreamTools.getString(buffer);
		assertTrue(encoding == null);
		assertTrue("test".equals(output));
		//
		input = "test\ndiedel";
		r = new StringReader(input);
		buffer = new BufferedReader(r);
		encoding = ReaderTools.readMetaEncoding(buffer);
		output = StreamTools.getString(buffer);
		assertTrue(encoding == null);
		assertTrue("test\ndiedel".equals(output));
		//
		input = "$$$ test";
		r = new StringReader(input);
		buffer = new BufferedReader(r);
		encoding = ReaderTools.readMetaEncoding(buffer);
		output = StreamTools.getString(buffer);
		assertTrue(encoding == null);
		assertTrue("$$$ test".equals(output));
		//
		input = "$$$ encoding =utf-8";
		r = new StringReader(input);
		buffer = new BufferedReader(r);
		encoding = ReaderTools.readMetaEncoding(buffer);
		output = StreamTools.getString(buffer);
		assertTrue("utf-8".equals(encoding));
		assertTrue("$$$ encoding =utf-8".equals(output));
	}

	@Test
	public void testReadTokenNewline() throws IOException {
		Reader r;
		String input;
		Token output;
		//
		input = "";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue(null == output);
		//
		input = " ";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue(" ".equals(output.getValue()));
		//
		input = "test";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("test".equals(output.getValue()));
		//
		input = "\"test\"";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("test".equals(output.getValue()));
		//
		input = "\"test\"diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("test".equals(output.getValue()));
		//
		input = "diedel\"test\"diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("diedel\"test\"diedel".equals(output.getValue()));
		//
		input = "\ndiedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("".equals(output.getValue()));
		//
		input = "test\n";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("test".equals(output.getValue()));
		//
		input = "test\ndiedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("test".equals(output.getValue()));
		//
		input = "test\r\ndiedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '\n');
		assertTrue("test".equals(output.getValue()));

	}

	@Test
	public void testReadTokenSlashDot() throws IOException {
		Reader r;
		String input;
		Token output;
		//
		input = "";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = " ";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue(" ".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "'test'";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == '\'');
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "'te\"st'";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("te\"st".equals(output.getValue()));
		assertTrue(output.getQuote() == '\'');
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "'test'diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == '\'');
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "diedel'test'diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel'test'diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = ".diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test..diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.foo.diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("foo".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.'foo'.diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("foo".equals(output.getValue()));
		assertTrue(output.getQuote() == '\'');
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.'foo.bar'.diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("foo.bar".equals(output.getValue()));
		assertTrue(output.getQuote() == '\'');
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);

	}

	@Test
	public void testReadTokenSlashSlashDot() throws IOException {
		Reader r;
		String input;
		Token output;
		//
		input = "";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = " ";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue(" ".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "\"test\"";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == '"');
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "\"te'st\"";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("te'st".equals(output.getValue()));
		assertTrue(output.getQuote() == '"');
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "\"test\"diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == '"');
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "diedel\"test\"diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel\"test\"diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = ".diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test..diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.foo.diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("foo".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.\"foo\".diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("foo".equals(output.getValue()));
		assertTrue(output.getQuote() == '"');
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);
		//
		input = "test.\"foo.bar\".diedel";
		r = new StringReader(input);
		output = ReaderTools.readToken(r, '.');
		assertTrue("test".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue("foo.bar".equals(output.getValue()));
		assertTrue(output.getQuote() == '"');
		output = ReaderTools.readToken(r, '.');
		assertTrue("diedel".equals(output.getValue()));
		assertTrue(output.getQuote() == 0);
		output = ReaderTools.readToken(r, '.');
		assertTrue(null == output);

	}
}
