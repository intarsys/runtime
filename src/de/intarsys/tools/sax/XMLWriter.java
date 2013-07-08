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

import java.io.Writer;

import org.xml.sax.SAXException;

/**
 * A SAX handler that writes out an XML compatible representation of the SAX
 * events.
 * 
 * @deprecated merge with other XMLWriter implementations.
 * 
 */
@Deprecated
public class XMLWriter extends SAXContextBasedWriter {
	private int indent = 0;

	private boolean prettyPrint = false;

	private boolean omitDeclaration;

	private boolean fixTag;

	private boolean writeAttributes = false;

	private java.lang.String destinationEncoding;

	public XMLWriter(IWriterFactory wf) {
		super(wf);
	}

	public XMLWriter(Writer w) {
		super(w);
	}

	@Override
	public void endDocument() throws SAXException {
		writeln();
		super.endDocument();
	}

	@Override
	public ISAXElementHandler getDefaultDocumentElement() {
		return new XMLElementWriter();
	}

	public java.lang.String getDestinationEncoding() {
		return destinationEncoding;
	}

	public int getIndent() {
		return indent;
	}

	public boolean isFixTag() {
		return fixTag;
	}

	public boolean isOmitDeclaration() {
		return omitDeclaration;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public boolean isWriteAttributes() {
		return writeAttributes;
	}

	public void setDestinationEncoding(java.lang.String newDestinationEncoding) {
		destinationEncoding = newDestinationEncoding;
	}

	public void setFixTag(boolean newFixTag) {
		fixTag = newFixTag;
	}

	public void setIndent(int newIndent) {
		indent = newIndent;
	}

	public void setOmitDeclaration(boolean newOmitDeclaration) {
		omitDeclaration = newOmitDeclaration;
	}

	public void setPrettyPrint(boolean newPrettyPrint) {
		prettyPrint = newPrettyPrint;
	}

	public void setWriteAttributes(boolean newWriteAttributes) {
		writeAttributes = newWriteAttributes;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		if (!isOmitDeclaration()) {
			write("<?xml version=\"1.0\"");
			if (getDestinationEncoding() != null) {
				write(" encoding=\"" + getDestinationEncoding() + "\"");
			}
			write(" ?>");
			writeln();
		}
	}
}
