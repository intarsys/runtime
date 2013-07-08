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
package de.intarsys.tools.sax;

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.SAXException;

import de.intarsys.tools.string.StringTools;

/**
 * A special {@link SAXContextBasedHandler} that simplifies writing.
 * 
 */
public abstract class SAXContextBasedWriter extends SAXContextBasedHandler {

	private IWriterFactory writerFactory;

	private Writer output;

	public SAXContextBasedWriter(IWriterFactory wf) {
		super();
		setWriterFactory(wf);
	}

	public SAXContextBasedWriter(Writer w) {
		super();
		setOutput(w);
	}

	public void closeWriter() throws SAXException {
		try {
			if (getOutput() != null) {
				if (getWriterFactory() != null) {
					getWriterFactory().closeWriter(getOutput());
					setOutput(null);
				}
			}
		} catch (Exception e) {
			throw new SAXException("error closing writer (" + e.getMessage()
					+ ")", e);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		closeWriter();
	}

	public java.io.Writer getOutput() {
		return output;
	}

	public IWriterFactory getWriterFactory() {
		if (writerFactory == null) {
			writerFactory = new DefaultWriterFactory();
		}
		return writerFactory;
	}

	public void openWriter() throws SAXException {
		if (getOutput() != null) {
			return;
		}
		try {
			setOutput(getWriterFactory().createWriter());
		} catch (Exception e) {
			throw new SAXException("error opening writer (" + e.getMessage()
					+ ")", e);
		}
	}

	protected void setOutput(java.io.Writer newOutput) {
		output = newOutput;
	}

	protected void setWriterFactory(IWriterFactory newWriterFactory) {
		writerFactory = newWriterFactory;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		openWriter();
	}

	protected void write(char c) throws SAXException {
		try {
			getOutput().write(c);
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	protected void write(char[] ch, int start, int length) throws SAXException {
		for (int i = start; i < start + length; i++) {
			write(ch[i]);
		}
	}

	protected void write(String s) throws SAXException {
		try {
			getOutput().write(s);
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	protected void writeln() throws SAXException {
		try {
			getOutput().write(StringTools.LS);
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
}
