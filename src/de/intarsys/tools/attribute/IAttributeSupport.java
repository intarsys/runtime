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
package de.intarsys.tools.attribute;

/**
 * This interface declares support for "generic attributes".
 * 
 * <p>
 * This means the object implementing this interface may be extended
 * transparently by its clients with name/value pairs. These attributes and
 * their values are stored but not interpreted by the object that implements
 * this interface.
 * </p>
 */
public interface IAttributeSupport {
	/**
	 * Get an attribute value from the context
	 * 
	 * @param key
	 *            the name of the attribute to get
	 * 
	 * @return The value of the attribute <code>key</code>
	 */
	public Object getAttribute(Object key);

	/**
	 * Remove an attribute binding in the context
	 * 
	 * @param key
	 *            the name of the attribute to remove
	 * 
	 * @return The previously associated value for <code>key</code>
	 */
	public Object removeAttribute(Object key);

	/**
	 * Set the value of an attribute in the context
	 * 
	 * @param key
	 *            the name of the attribute to set
	 * @param value
	 *            the new value the attribute
	 * 
	 * @return The previously associated value for <code>key</code>
	 */
	public Object setAttribute(Object key, Object value);
}
