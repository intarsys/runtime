/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package de.intarsys.tools.infoset;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import de.intarsys.tools.xml.XMLTools;

public class XMLWriter {

	protected static final OutputFormat DEFAULT_FORMAT = new OutputFormat();

	private Writer writer;

	private OutputFormat format;

	private int indentLevel = 0;

	private int maximumAllowedCharacter;

	public XMLWriter(OutputStream out) throws UnsupportedEncodingException {
		this.format = DEFAULT_FORMAT;
		this.writer = createWriter(out, format.getEncoding());
	}

	public XMLWriter(OutputStream out, OutputFormat format)
			throws UnsupportedEncodingException {
		this.format = format;
		this.writer = createWriter(out, format.getEncoding());
	}

	public XMLWriter(Writer writer) {
		this(writer, DEFAULT_FORMAT);
	}

	public XMLWriter(Writer writer, OutputFormat format) {
		this.writer = writer;
		this.format = format;
	}

	protected Writer createWriter(OutputStream outStream, String encoding)
			throws UnsupportedEncodingException {
		return new BufferedWriter(new OutputStreamWriter(outStream, encoding));
	}

	protected int defaultMaximumAllowedCharacter() {
		String encoding = format.getEncoding();

		if (encoding != null) {
			if (encoding.equals("US-ASCII")) {
				return 127;
			}
		}

		// no encoding for things like ISO-*, UTF-8 or UTF-16
		return -1;
	}

	public int getMaximumAllowedCharacter() {
		if (maximumAllowedCharacter == 0) {
			maximumAllowedCharacter = defaultMaximumAllowedCharacter();
		}

		return maximumAllowedCharacter;
	}

	protected OutputFormat getOutputFormat() {
		return format;
	}

	protected boolean isExpandEmptyElements() {
		return format.isExpandEmptyElements();
	}

	protected void println() throws IOException {
		writer.write(format.getLineSeparator());
	}

	public void setIndentLevel(int indentLevel) {
		this.indentLevel = indentLevel;
	}

	public void setMaximumAllowedCharacter(int maximumAllowedCharacter) {
		this.maximumAllowedCharacter = maximumAllowedCharacter;
	}

	protected boolean shouldEncodeChar(char c) {
		int max = getMaximumAllowedCharacter();

		return (max > 0) && (c > max);
	}

	public void write(IAttribute attribute) throws IOException {
		writeAttribute(attribute.getName(), attribute.getTemplate());
	}

	public void write(IDocument doc) throws IOException {
		writeDeclaration();
		write(doc.getRootElement());
		writeln();
	}

	public void write(IElement element) throws IOException {
		writeElement(element);
	}

	protected void writeAttribute(String name, String value) throws IOException {
		writer.write(" ");
		writer.write(name);
		writer.write("=");
		char quote = format.getAttributeQuoteCharacter();
		writer.write(quote);
		writeEscapeAttributeEntities(value);
		writer.write(quote);
	}

	protected void writeAttributes(IElement element) throws IOException {
		Iterator<String> it = element.attributeNames();
		while (it.hasNext()) {
			String name = it.next();
			writeAttribute(name, element.attributeTemplate(name));
		}
	}

	protected void writeDeclaration() throws IOException {
		String encoding = format.getEncoding();
		if (!format.isSuppressDeclaration()) {
			// Assume 1.0 version
			if (encoding.equals("UTF8")) {
				writer.write("<?xml version=\"1.0\"");

				if (!format.isOmitEncoding()) {
					writer.write(" encoding=\"UTF-8\"");
				}

				writer.write("?>");
			} else {
				writer.write("<?xml version=\"1.0\"");

				if (!format.isOmitEncoding()) {
					writer.write(" encoding=\"" + encoding + "\"");
				}

				writer.write("?>");
			}
			if (format.isNewLineAfterDeclaration()) {
				println();
			}
		}
	}

	protected void writeElement(IElement element) throws IOException {
		String qualifiedName = element.getName();

		writeln();
		writeIndent();

		writer.write("<");
		writer.write(qualifiedName);

		writeAttributes(element);

		boolean empty = true;
		if (element.getText() == null && !element.hasElements()) {
			// Simply close up
			if (!format.isExpandEmptyElements()) {
				writer.write("/>");
			} else {
				writer.write("></");
				writer.write(qualifiedName);
				writer.write(">");
			}
		} else {
			writer.write(">");
			++indentLevel;
			writeElementContent(element);
			--indentLevel;
			writeln();
			writeIndent();
			writer.write("</");
			writer.write(qualifiedName);
			writer.write(">");
		}
	}

	protected void writeElementContent(IElement element) throws IOException {
		String value;
		value = element.getText();
		boolean trim = format.isTrimText();
		if (value != null) {
			if (trim) {
				writeString(value.trim());
			} else {
				writeString(value);
			}
		}
		Iterator<IElement> it = element.elementIterator();
		while (it.hasNext()) {
			IElement node = it.next();
			write(node);
		}
	}

	protected void writeEscapeAttributeEntities(String txt) throws IOException {
		if (txt != null) {
			String escapedText = XMLTools.encodeSpecialChars(txt);
			writer.write(escapedText);
		}
	}

	protected void writeIndent() throws IOException {
		String indent = format.getIndent();
		if ((indent != null) && (indent.length() > 0)) {
			for (int i = 0; i < indentLevel; i++) {
				writer.write(indent);
			}
		}
	}

	protected void writeln() throws IOException {
		if (format.isNewlines()) {
			writer.write(format.getLineSeparator());
		}
	}

	protected void writeString(String text) throws IOException {
		if ((text != null) && (text.length() > 0)) {
			if (format.isEscapeText()) {
				text = XMLTools.encodeSpecialChars(text);
			}
			if (format.isTrimText()) {
				writer.write(text.trim());
			} else {
				writer.write(text);
			}
		}
	}
}
