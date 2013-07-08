package de.intarsys.tools.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

/**
 * This reader extracts meta information from the data header.
 * <p>
 * The syntax for an entry is
 * 
 * <pre>
 * '$$$' ws* key ws* '=' value '\n'
 * value = string | quoted_string
 * quoted_string = '"' [ char | escape ]* '"'
 * </pre>
 * 
 * Meta data is scanned until the first line not starting with '$$$'. So the
 * first character returned is the first character on the first line not
 * starting with '$$$'.
 * 
 */
public class TaggedReader extends Reader {

	final private Map<String, String> properties;

	final private Reader reader;

	final private BufferedReader buffer;

	public TaggedReader(Reader reader, int size) throws IOException {
		super();
		this.reader = reader;
		this.buffer = new BufferedReader(reader, size);
		this.properties = ReaderTools.readMetaData(buffer);
	}

	@Override
	public void close() throws IOException {
		buffer.close();
	}

	public String getEncoding() {
		if (reader instanceof InputStreamReader) {
			return ((InputStreamReader) reader).getEncoding();
		}
		return null;
	}

	public String getProperty(String name) {
		return properties.get(name);
	}

	public Iterator<String> getPropertyNames() {
		return properties.keySet().iterator();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		buffer.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() {
		return buffer.markSupported();
	}

	public void putProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	public int read() throws IOException {
		return buffer.read();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return buffer.read(cbuf, off, len);
	}

	@Override
	public boolean ready() throws IOException {
		return buffer.ready();
	}

	@Override
	public void reset() throws IOException {
		buffer.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return buffer.skip(n);
	}

}
