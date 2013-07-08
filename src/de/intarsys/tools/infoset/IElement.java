/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.infoset;

import java.util.Iterator;

/**
 * An {@link IElement} is an information container in the infoset abstraction.
 * 
 * The {@link IElement} contains optional other {@link IAttribute} or
 * {@link IElement} instances and a text value.
 * 
 */
public interface IElement {

	/**
	 * This {@link IElement} serialized as an XML string.
	 * 
	 * @return
	 */
	public String asXML();

	/**
	 * The {@link IAttribute} named "name" within this element or null if it
	 * does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public IAttribute attribute(String name);

	/**
	 * An {@link Iterator} providing all attribute names attached to this
	 * element. There's no guarantee on the order of the names.
	 * 
	 * @return
	 */
	public Iterator<String> attributeNames();

	/**
	 * If this implementation supports template expansion, return the template
	 * (unexpanded) value of the {@link IAttribute} named "name" in this element
	 * or null if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public String attributeTemplate(String name);

	/**
	 * The value of the {@link IAttribute} named "name" in this element or
	 * "defaultValue" if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public String attributeValue(String name, String defaultValue);

	/**
	 * The (first) {@link IElement} named "name" within this element or null if
	 * it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public IElement element(String name);

	/**
	 * An {@link Iterator} providing all {@link IElement} instances attached to
	 * this element.
	 * 
	 * @return
	 */
	public Iterator<IElement> elementIterator();

	/**
	 * An {@link Iterator} providing all {@link IElement} instances attached to
	 * this element having a name of "name".
	 * 
	 * @return
	 */
	public Iterator<IElement> elementIterator(String name);

	/**
	 * Remove targeElement from this.
	 * 
	 * @param targetElement
	 */
	public void elementRemove(IElement targetElement);

	/**
	 * Remove all child {@link IElement} instances .
	 */
	public void elementsClear();

	/**
	 * The text content of the {@link IElement} named "name" within this element
	 * or null if the child does not exist.
	 * 
	 * @param string
	 * @return
	 */
	public String elementText(String name);

	/**
	 * The name of this {@link IElement}.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The text content of this element. This method never returns null.
	 * 
	 * @return
	 */
	public String getText();

	/**
	 * true if this {@link IElement} has {@link IAttribute} instances with name
	 * 
	 * @return
	 */
	public boolean hasAttribute(String name);

	/**
	 * true if this {@link IElement} has {@link IAttribute} instances.
	 * 
	 * @return
	 */
	public boolean hasAttributes();

	/**
	 * true if this {@link IElement} has child {@link IElement} instances.
	 * 
	 * @return
	 */
	public boolean hasElements();

	/**
	 * true if this {@link IElement} has child {@link IElement} instances with
	 * name "name"
	 * 
	 * @return
	 */
	public boolean hasElements(String name);

	/**
	 * Create and attach a new {@link IElement}
	 * 
	 * @param name
	 * @return
	 */
	public IElement newElement(String name);

	/**
	 * If this implementation supports template expansion, set the template
	 * (unexpanded) value for an attribute. If the attribute already exists, the
	 * value is replaced. If value is null, an existing attribute will be
	 * removed.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public void setAttributeTemplate(String name, String template);

	/**
	 * Set the value for an attribute. IF the attribute already exists, the
	 * value is replaced. If value is null, an existing attribute will be
	 * removed.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public void setAttributeValue(String name, String value);

	/**
	 * Set the element name to "name".
	 * 
	 * @param xeQuery
	 */
	public void setName(String name);

	/**
	 * Set the element text content to "value".
	 * 
	 * @param value
	 */
	public void setText(String value);

}
