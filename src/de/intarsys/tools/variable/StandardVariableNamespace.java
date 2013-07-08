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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.collection.EmptyIterator;

/**
 * A general implementation for an object providing {@link IVariableNamespace}.
 * 
 */
public class StandardVariableNamespace implements IVariableNamespace {

	private Map variables;

	/**
	 * Create a VariableScope
	 * 
	 */
	public StandardVariableNamespace() {
		super();
	}

	synchronized public String getVariable(String key) {
		if (variables == null) {
			return null;
		}
		return (String) variables.get(key);
	}

	synchronized public String getVariable(String key, String defaultValue) {
		if (variables == null) {
			return defaultValue;
		}
		String result = (String) variables.get(key);
		if (result == null) {
			return defaultValue;
		} else {
			return (String) variables.get(key);
		}
	}

	synchronized public Iterator getVariableIterator() {
		if (variables == null) {
			return EmptyIterator.UNIQUE;
		}
		return new HashMap(variables).entrySet().iterator();
	}

	synchronized public Map getVariables() {
		if (variables == null) {
			variables = new HashMap();
		}
		return new HashMap(variables);
	}

	synchronized public void putVariable(String key, String value) {
		if (variables == null) {
			variables = new HashMap();
		}
		variables.put(key, value);
	}

	synchronized public void putVariables(Map v) {
		if (variables == null) {
			variables = new HashMap();
		}
		variables.putAll(v);
	}
}
