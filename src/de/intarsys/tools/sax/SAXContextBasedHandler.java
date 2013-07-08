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

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.functor.Args;

/**
 * The {@link DefaultHandler} for a SAX parsing framework.
 * 
 * {@link SAXContextBasedHandler} acts as an facade to context specific
 * {@link ISAXElementHandler} instances. These {@link ISAXElementHandler}
 * instances are built up in a stack like manner with each
 * startElement/EndElement event.
 * 
 */
public abstract class SAXContextBasedHandler extends DefaultHandler {

	//
	private int nesting = 0;

	private Locator locator = null;

	private ISAXElementHandler documentElement = null;

	private ISAXElementHandler current = null;

	private ClassLoader classLoader;

	// an exception encountered while parsing if any
	private Exception exception;

	private IStringEvaluator templateEvaluator;

	private ISAXElementHandler[] stack = new ISAXElementHandler[10];

	private int stackIndex = -1;

	public SAXContextBasedHandler() {
		super();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		current.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		if (!isStarted()) {
			throw new SAXException("document not started");
		}
		if (!isDocumentElementLevel()) {
			throw new SAXException("document has open elements");
		}
		current.endDocumentElement();
		pop();
	}

	@Override
	public void endElement(String uri, String local, String name)
			throws SAXException {
		ISAXElementHandler element = current;
		markEndLocation(element);
		element.endElement(uri, local, name);
		pop();
		current.endChildElement(element);
		nesting--;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		setException(e);
		throw e;
	}

	public void fail() {
		reset();
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		setException(e);
		throw e;
	}

	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}
		return classLoader;
	}

	public ISAXElementHandler getCurrent() {
		return current;
	}

	public abstract ISAXElementHandler getDefaultDocumentElement();

	public ISAXElementHandler getDocumentElement() {
		if (documentElement == null) {
			documentElement = getDefaultDocumentElement();
		}
		return documentElement;
	}

	public String getErrorString(SAXParseException e) {
		String errorString = "";
		if (current != null) {
			errorString = "in <" + current.getLocalName() + "> at ";
		} else {
			errorString = "at ";
		}
		errorString = errorString + getLocationString(e) + " " + e.getMessage();
		return errorString;
	}

	public IStringEvaluator getTemplateEvaluator() {
		return templateEvaluator;
	}

	public Exception getException() {
		return exception;
	}

	public String getLocationString(SAXParseException e) {
		StringBuilder str = new StringBuilder();
		String systemId = e.getSystemId();
		if (systemId != null) {
			int index = systemId.lastIndexOf('/');
			if (index != -1) {
				systemId = systemId.substring(index + 1);
			}
			str.append(systemId);
			str.append(':');
		}
		str.append("line ");
		str.append(e.getLineNumber());
		str.append(", column ");
		str.append(e.getColumnNumber());
		return str.toString();
	}

	public org.xml.sax.Locator getLocator() {
		return locator;
	}

	public int getNesting() {
		return nesting;
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		current.ignorableWhitespace(ch, start, length);
	}

	public boolean isDocumentElementLevel() {
		return current == getDocumentElement();
	}

	public boolean isEmpty() {
		return stackIndex < 0;
	}

	public boolean isError() {
		return getException() != null;
	}

	public boolean isStarted() {
		return stackIndex >= 0;
	}

	protected void markEndLocation(ISAXElementHandler element) {
		Locator tempLocator = getLocator();
		if (tempLocator != null) {
			element.markEndLocation(tempLocator.getLineNumber(),
					tempLocator.getColumnNumber());
		}
	}

	protected void markStartLocation(ISAXElementHandler newHandler) {
		Locator tempLocator = getLocator();
		if (tempLocator != null) {
			newHandler.markStartLocation(tempLocator.getLineNumber(),
					tempLocator.getColumnNumber());
		}
	}

	private void pop() {
		stack[stackIndex--] = null;
		if (stackIndex < 0) {
			current = null;
		} else {
			current = stack[stackIndex];
		}
	}

	private void push(ISAXElementHandler newHandler) {
		newHandler.setContextHandler(this);
		newHandler.setParent(current);
		current = newHandler;
		if (++stackIndex == stack.length) {
			ISAXElementHandler[] newStack = new ISAXElementHandler[stack.length * 2];
			System.arraycopy(stack, 0, newStack, 0, stack.length);
			stack = newStack;
		}
		stack[stackIndex] = newHandler;
	}

	public void reset() {
		current = null;
		documentElement = null;
		nesting = 0;
		locator = null;
		exception = null;
		stackIndex = -1;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setDocumentElement(ISAXElementHandler e) {
		documentElement = e;
	}

	@Override
	public void setDocumentLocator(Locator newlocator) {
		locator = newlocator;
	}

	public void setTemplateEvaluator(IStringEvaluator evaluator) {
		this.templateEvaluator = evaluator;
	}

	public void setException(Exception newException) {
		exception = newException;
	}

	@Override
	public void startDocument() throws SAXException {
		if (isStarted()) {
			throw new SAXException("document already started");
		}
		nesting = 0;
		push(getDocumentElement());
		current.initializeDocumentElement();
	}

	@Override
	public void startElement(String uri, String local, String name,
			Attributes attrs) throws SAXException {
		nesting++;
		ISAXElementHandler previous = current;
		Attributes newAttrs = substitute(attrs);
		ISAXElementHandler newHandler = previous.startElement(uri, local, name,
				newAttrs);
		push(newHandler);
		markStartLocation(newHandler);
		newHandler.initialize(uri, local, name, newAttrs);
		if (newAttrs != null) {
			newHandler.processAttributes(newAttrs);
		}
		newHandler.started();
		previous.startChildElement(newHandler);
	}

	public Attributes substitute(Attributes attributes) {
		if ((getTemplateEvaluator() == null) || (attributes == null)) {
			return attributes;
		}
		Attributes result = attributes;
		int length = result.getLength();
		for (int i = 0; i < length; i++) {
			String value = result.getValue(i);
			if (value.indexOf('$') >= 0) {
				if (result == attributes) {
					result = new AttributesImpl(attributes);
				}
				Object expanded;
				try {
					expanded = getTemplateEvaluator().evaluate(value, Args.create());
				} catch (EvaluationException e) {
					expanded = "<error expanding '" + value + "'";
				}
				((AttributesImpl) result).setValue(i, String.valueOf(expanded));
			}
		}
		return result;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		setException(e);
		throw e;
	}
}
