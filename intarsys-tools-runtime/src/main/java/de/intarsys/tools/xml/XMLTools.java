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
import java.util.function.UnaryOperator;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import de.intarsys.tools.exception.UnreachableCodeError;
import de.intarsys.tools.stream.StreamTools;

public class XMLTools {
	private static final String PRETTY_PRINT_STYLESHEET = """
			<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			    <xsl:strip-space elements="*"/>
			    <xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="yes"/>

			    <xsl:template match="@*|node()">
			        <xsl:copy>
			            <xsl:apply-templates select="@*|node()"/>
			        </xsl:copy>
			    </xsl:template>
			</xsl:stylesheet>
			""";

	private static final int DEFAULT_INDENT_AMOUNT = 4;

	public static String decodeSpecialChars(String encodedString) throws IOException {
		try (EntityDecoder decoder = new EntityDecoder(new StringReader(encodedString), false)) {
			return StreamTools.getString(decoder);
		}
	}

	public static String encodeDefault(String plainString) {
		return encode(plainString, reader -> new HTMLEncodeLineBreaks(new XMLEncodeSpecialChars(reader)));
	}

	public static String encodeLineBreaks(String plainString) {
		return encode(plainString, HTMLEncodeLineBreaks::new);
	}

	public static String encodeSpecialChars(String plainString) {
		return encode(plainString, XMLEncodeSpecialChars::new);
	}

	private static String encode(String input, UnaryOperator<Reader> encoderFactory) {
		try (Reader reader = encoderFactory.apply(new StringReader(input));
				Writer writer = new StringWriter()) {
			StreamTools.copy(reader, writer);
			return writer.toString();
		} catch (IOException exception) {
			throw new UnreachableCodeError("String readers and writers should not throw IOExceptions", exception);
		}
	}

	/**
	 * Serializes the given {@code Node} to a string.
	 */
	public static String serialize(Node node) {
		try (StringWriter output = new StringWriter()) {
			Transformer transformer = TransformerTools
					.createSecureTransformerFactory()
					.newTransformer();
			transformer.transform(new DOMSource(node), new StreamResult(output));
			return output.toString();
		} catch (TransformerException | IOException exception) {
			throw new UnreachableCodeError("Should never happen with a wellformed Document and a StringWriter", exception);
		}
	}

	/**
	 * Serializes the given {@code Node} to a string and adds additional whitespace in order to indent elements nicely
	 * (see {@link #prettyPrint(Source, Result)}).
	 */
	public static String prettyPrint(Node node) {
		try (StringWriter output = new StringWriter()) {
			prettyPrint(new DOMSource(node), new StreamResult(output));
			return output.toString();
		} catch (TransformerException | IOException exception) {
			throw new UnreachableCodeError("Should never happen with a wellformed Node and a StringWriter", exception);
		}
	}

	/**
	 * Reformats the given XML string by adding additional whitespace to indent elements nicely (see
	 * {@link #prettyPrint(Source, Result)}). If the given string is not well-formed XML, no error is raised and the
	 * string is returned unaltered.
	 *
	 * @param xml the XML string to be reformatted
	 * @return the reformatted string or, if the given string is not well-formed XML, the unaltered given string
	 */
	public static String prettyPrint(String xml) {
		try (StringReader input = new StringReader(xml);
				StringWriter output = new StringWriter()) {
			prettyPrint(new StreamSource(input), new StreamResult(output));
			return output.toString();
		} catch (TransformerException | IOException exception) {
			return xml;
		}
	}

	/**
	 * Adds additional whitespace to an XML source in order to indent elements nicely.
	 *
	 * <p>
	 * Note: It is usually not safe to use indentation with document types that include elements with mixed content
	 * (that is, elements that contain other elements as well as text nodes).
	 *
	 * @param source the source of the transformation
	 * @param result the result of the transformation
	 * @throws TransformerException if an error occurs during the transformation
	 */
	public static void prettyPrint(Source source, Result result) throws TransformerException {
		// Since Java 9, Transformer also outputs whitespace-only nodes, which results in empty newlines. Therefore,
		// we use the pretty-print stylesheet to remove those nodes (see https://bugs.openjdk.org/browse/JDK-8262285).
		try (StringReader stylesheet = new StringReader(PRETTY_PRINT_STYLESHEET)) {
			Transformer transformer = TransformerTools
				.createSecureTransformerFactory()
				.newTransformer(new StreamSource(stylesheet));
			TransformerTools.setIndentAmount(transformer, DEFAULT_INDENT_AMOUNT);
			transformer.transform(source, result);
		}
	}

	private XMLTools() {
		super();
	}
}
