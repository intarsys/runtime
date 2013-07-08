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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An {@link ISAXElementHandler} that delegates to another handler that is
 * explicitly mentioned in an attribute in an element attribute.
 * 
 * This allows the use of generic containers that do not necessarily know about
 * their content.
 * 
 * The default attribute to be used is "deserializer"
 * 
 */
public class DelegatingElementHandler extends SAXAbstractElementHandler {

	private ISAXElementHandler delegate;

	private Object result;

	private Map<String, String> aliases = new HashMap<String, String>();

	final private String handlerAttribute;

	public DelegatingElementHandler() {
		super();
		handlerAttribute = "deserializer";
	}

	public DelegatingElementHandler(String handlerAttribute) {
		super();
		this.handlerAttribute = handlerAttribute;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		delegate.characters(ch, start, length);
	}

	protected ISAXElementHandler createNestedHandler(String handlerClass)
			throws SAXException {
		String tempClass = aliases.get(handlerClass);
		if (tempClass == null) {
			tempClass = handlerClass;
		}
		try {
			Class handlerClazz = Class.forName(tempClass);
			ISAXElementHandler handler = (ISAXElementHandler) handlerClazz
					.newInstance();
			return handler;
		} catch (ClassNotFoundException e) {
			throw new SAXException(e);
		} catch (InstantiationException e) {
			throw new SAXException(e);
		} catch (IllegalAccessException e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void endChildElement(ISAXElementHandler element) throws SAXException {
		delegate.endChildElement(element);
		result = element.getResult();
	}

	@Override
	public void endElement(String pUri, String local, String name)
			throws SAXException {
		delegate.endElement(pUri, local, name);
	}

	public String getHandlerAttribute() {
		return handlerAttribute;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public void initialize(String pUri, String local, String name,
			Attributes attrs) throws SAXException {
		super.initialize(pUri, local, name, attrs);
		String nestedHandler = attrs.getValue(getHandlerAttribute());
		delegate = createNestedHandler(nestedHandler);
		delegate.setContextHandler(getContextHandler());
		delegate.setParent(getParent());
		delegate.initialize(pUri, local, name, attrs);
	}

	@Override
	public void processAttributes(Attributes attrs) throws SAXException {
		delegate.processAttributes(attrs);
	}

	public void registerAlias(String alias, String name) {
		aliases.put(alias, name);
	}

	@Override
	public void startChildElement(ISAXElementHandler element)
			throws SAXException {
		super.startChildElement(element);
		delegate.startChildElement(element);
	}

	@Override
	public void started() throws SAXException {
		super.started();
		delegate.started();
	}

	@Override
	public ISAXElementHandler startElement(String pUri, String local,
			String name, Attributes attrs) throws SAXException {
		return delegate.startElement(pUri, local, name, attrs);
	}

	public void unregisterAlias(String alias) {
		aliases.remove(alias);
	}
}
