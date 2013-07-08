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
package de.intarsys.tools.attribute;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Tool class for handling generic attributes.
 * 
 */
public class AttributeTools {

	private static final Map<Object, AttributeMap> objectAttributes = new WeakHashMap<Object, AttributeMap>();

	/**
	 * Get an attribute value from object
	 * 
	 * @param object
	 *            The target object hosting the attribute
	 * @param key
	 *            the name of the attribute to get
	 * 
	 * @return The value of the attribute <code>key</code>
	 */
	static public Object getAttribute(Object object, Object key) {
		if (object instanceof IAttributeSupport) {
			return ((IAttributeSupport) object).getAttribute(key);
		}
		return getAttributeBasic(object, key);
	}

	/**
	 * Get an attribute value from object
	 * 
	 * @param object
	 *            The target object hosting the attribute
	 * @param key
	 *            the name of the attribute to get
	 * 
	 * @return The value of the attribute <code>key</code>
	 */
	synchronized static public Object getAttributeBasic(Object object,
			Object key) {
		AttributeMap attributes = objectAttributes.get(object);
		if (attributes == null) {
			return null;
		}
		return attributes.getAttribute(key);
	}

	/**
	 * Remove an attribute binding from object
	 * 
	 * @param object
	 *            The target object hosting the attribute
	 * @param key
	 *            the name of the attribute to remove
	 * 
	 * @return The previously associated value for <code>key</code>
	 */
	static public Object removeAttribute(Object object, Object key) {
		if (object instanceof IAttributeSupport) {
			return ((IAttributeSupport) object).removeAttribute(key);
		}
		return removeAttributeBasic(object, key);
	}

	/**
	 * Remove an attribute binding from object
	 * 
	 * @param object
	 *            The target object hosting the attribute
	 * @param key
	 *            the name of the attribute to remove
	 * 
	 * @return The previously associated value for <code>key</code>
	 */
	synchronized static public Object removeAttributeBasic(Object object,
			Object key) {
		AttributeMap attributes = objectAttributes.get(object);
		if (attributes == null) {
			return null;
		}
		Object previous = attributes.removeAttribute(key);
		if (attributes.getKeys().length == 0) {
			objectAttributes.remove(object);
		}
		return previous;
	}

	/**
	 * Set the value of an attribute in object
	 * 
	 * @param object
	 *            The target object hosting the attribute
	 * @param key
	 *            the name of the attribute to set
	 * @param value
	 *            the new value the attribute
	 * 
	 * @return The previously associated value for <code>key</code>
	 */
	static public Object setAttribute(Object object, Object key, Object value) {
		if (object instanceof IAttributeSupport) {
			return ((IAttributeSupport) object).setAttribute(key, value);
		}
		return setAttributeBasic(object, key, value);
	}

	/**
	 * Set the value of an attribute in object
	 * 
	 * @param object
	 *            The target object hosting the attribute
	 * @param key
	 *            the name of the attribute to set
	 * @param value
	 *            the new value the attribute
	 * 
	 * @return The previously associated value for <code>key</code>
	 */
	synchronized static public Object setAttributeBasic(Object object,
			Object key, Object value) {
		AttributeMap attributes = objectAttributes.get(object);
		if (attributes == null) {
			attributes = new AttributeMap();
			objectAttributes.put(object, attributes);
		}
		return attributes.setAttribute(key, value);
	}

}
