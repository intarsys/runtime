/*
 * Copyright (c) 2012, intarsys GmbH
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
package de.intarsys.tools.ipc;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

/**
 * Tool methods for dealing with interprocess references.
 * 
 */
public class IPCTools {

	private static final Attribute ATTR_IPC_SCOPE = new Attribute("ipcScope"); //$NON-NLS-1$

	public static <T> T createObject(Object value, IIPCStubFactory<T> factory) throws ObjectCreationException {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			if (StringTools.isEmpty((String) value)) {
				return null;
			}
			throw new ObjectCreationException("invalid data type");
		} else if (value instanceof IPCHandle) {
			IPCHandle handle = (IPCHandle) value;
			if (handle.isResolved()) {
				return (T) handle.getObject();
			}
			T object = factory.createObject(handle);
			((IPCHandle) value).resolve(object);
			return object;
		} else if (factory.getTargetClass().isInstance(value)) {
			return (T) value;
		} else {
			throw new ObjectCreationException("invalid data type");
		}
	}

	public static void restore(IAttributeSupport as) {
		IIPCScope scope = null;
		if (as != null) {
			// lookup if IIPCScope already exists
			scope = (IIPCScope) as.getAttribute(ATTR_IPC_SCOPE);
		}
		// this may be null
		IPCScope.set(scope);
	}

	public static void store(IAttributeSupport as) {
		if (as != null) {
			as.setAttribute(ATTR_IPC_SCOPE, IPCScope.lookup());
		}
	}

	private IPCTools() {
	}

}
