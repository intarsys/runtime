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
package de.intarsys.tools.variable;

import java.util.Iterator;
import java.util.Map;

/**
 * This defines the ability of a component to support generic key/value mappings
 * from string to string.
 * <p>
 * Variables are used most often for string replacements. You should not use
 * this feature for parameter or attribute passing between components.
 * 
 */
public interface IVariableNamespace {
	/**
	 * The string variable stored with <code>key</code>.
	 * 
	 * @param key
	 *            The name of the string variable
	 * 
	 * @return The string variable stored with <code>key</code>.
	 */
	public String getVariable(String key);

	/**
	 * The string variable stored with <code>key</code> or
	 * <code>defaultValue</code> if the result would be <code>null</code>.
	 * 
	 * @param key
	 *            The name of the string variable
	 * @param defaultValue
	 *            The value to use if result would be null
	 * 
	 * @return The string variable stored with <code>key</code> or
	 *         <code>defaultValue</code> if the result would be
	 *         <code>null</code>.
	 */
	public String getVariable(String key, String defaultValue);

	/**
	 * An iterator over the entries (Map.Entry) of the Map.
	 * 
	 * @return An iterator over the entries (Map.Entry) of the Map.
	 */
	public Iterator getVariableIterator();

	/**
	 * The map holding the assoications from names to values.
	 * 
	 * @return The map holding the assoications from names to values.
	 */
	public Map getVariables();

	/**
	 * Enter a name/value binding in the map.
	 * 
	 * @param key
	 *            The name of the string variable
	 * @param value
	 *            The value to use for the variable.
	 */
	public void putVariable(String key, String value);

}
