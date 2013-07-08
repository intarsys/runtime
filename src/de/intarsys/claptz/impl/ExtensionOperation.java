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
package de.intarsys.claptz.impl;


import de.intarsys.claptz.IExtension;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementSerializable;

/**
 * The representation of an operation attachment to an {@link IExtension}
 * instance.
 */
abstract public class ExtensionOperation {

	public static final String EAV_DELETE = "delete";

	public static final String EAV_UPDATE = "update";

	public static final String EAV_INSERT = "insert";

	public static final String EA_OPERATION = "_op";

	final private Object object;

	public ExtensionOperation(Object object) {
		super();
		this.object = object;
	}

	protected void basicSerialize(Object target, IElement targetElement)
			throws ElementSerializationException {
		if (target instanceof IElementSerializable) {
			((IElementSerializable) target).serialize(targetElement);
		}
	}

	public Object getObject() {
		return object;
	}

	public boolean isDelete() {
		return false;
	}

	public boolean isInsert() {
		return false;
	}

	public boolean isUpdate() {
		return false;
	}

	abstract public void perform(IExtension extension) throws Exception;
}
