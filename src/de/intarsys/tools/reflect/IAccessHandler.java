/*
 * Copyright (c) 2008, intarsys consulting GmbH
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
package de.intarsys.tools.reflect;

/**
 * An object supporting handling reflective access to fields of a delegate.
 */
public interface IAccessHandler {
	/**
	 * Make a reflective get access to the field <code>name</code> within
	 * <code>receiver</code>
	 * 
	 * @param receiver
	 *            The object hosting the field.
	 * @param name
	 *            The name of the field to get.
	 * @return The field value
	 * @throws FieldException
	 */
	public Object getValue(Object receiver, String name) throws FieldException;

	/**
	 * Make a reflective set access to the field <code>name</code> within
	 * <code>receiver</code>
	 * 
	 * @param receiver
	 *            The object hosting the field.
	 * @param name
	 *            The name of the field to get.
	 * @param value
	 *            The new value for the field
	 * @return The previous field value. This is an optional feature.
	 * @throws FieldException
	 */
	public Object setValue(Object receiver, String name, Object value)
			throws FieldException;
}
