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
package de.intarsys.tools.objectmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * A "meta" class for any object. This is used to dynamically extend the objects
 * features via the registration of additional methods (and maybe attributes
 * some time).
 * 
 */
public class Class implements IClass {

	final private Map methods = new HashMap();

	final private Map fields = new HashMap();

	final private IClassSelector selector;

	public Class(IClassSelector selector) {
		this.selector = selector;
	}

	/**
	 * 
	 */
	public Class(java.lang.Class javaclass) {
		this(new JavaClassSelector(javaclass));
	}

	public Class(java.lang.Class javaclass, Object id) {
		this(new JavaInstanceSelector(javaclass, id));
	}

	public IField[] getFields() {
		return (IField[]) fields.values().toArray(new IField[fields.size()]);
	}

	public IMethod[] getMethods() {
		return (IMethod[]) methods.values()
				.toArray(new IMethod[methods.size()]);
	}

	public IClassSelector getSelector() {
		return selector;
	}

	public IField lookupField(String name) {
		return (IField) fields.get(name);
	}

	public IMethod lookupMethod(String name) {
		return (IMethod) methods.get(name);
	}

	public void registerField(IField property) {
		fields.put(property.getName(), property);
	}

	public void registerMethod(IMethod method) {
		methods.put(method.getName(), method);
	}
}
