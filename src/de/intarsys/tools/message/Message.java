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
package de.intarsys.tools.message;

import de.intarsys.tools.reflect.ClassTools;

/**
 * 
 * 
 */
public class Message {

	final private String key;

	final private Class clazz;

	private MessageBundle bundle;

	public Message(MessageBundle bundle, String key) {
		this.bundle = bundle;
		this.key = key;
		this.clazz = null;
	}

	public Message(Object implementor, String suffix) {
		if (implementor instanceof Class) {
			this.clazz = (Class) implementor;
		} else {
			this.clazz = implementor.getClass();
		}
		this.key = ClassTools.getUnqualifiedName(clazz) + "." + suffix;
	}

	public String get() {
		return getBundle().getString(getKey());
	}

	public String get(String arg1) {
		return getBundle().getString(getKey(), arg1);
	}

	public String get(String arg1, String arg2) {
		return getBundle().getString(getKey(), arg1, arg2);
	}

	public String get(String arg1, String arg2, String arg3) {
		return getBundle().getString(getKey(), arg1, arg2, arg3);
	}

	public String get(String arg1, String arg2, String arg3, String arg4) {
		return getBundle().getString(getKey(), arg1, arg2, arg3, arg4);
	}

	public MessageBundle getBundle() {
		if (bundle == null) {
			bundle = MessageBundleTools.getMessageBundle(clazz);
		}
		return bundle;
	}

	public Object getClazz() {
		return clazz;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return get();
	}
}
