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
package de.intarsys.tools.enumeration;

import de.intarsys.tools.lang.LangTools;
import de.intarsys.tools.string.StringTools;

/**
 * An item in a {@link Domain} enumeration. A {@link DomainItem} links together
 * tuples of objects with presentation logic to make them suitable for
 * presentation in a user interface.
 * 
 */
public class DomainItem<T> extends EnumItem {

	private static int counter = 0;

	private Object[] object;

	public static final Object MATCH_ANY = new Object();

	protected DomainItem(Domain domain, Object[] object) {
		super(domain, "" + counter++, StringTools.safeString(object), WEIGHT++);
		if (object == null) {
			throw new IllegalArgumentException();
		}
		this.object = object;
	}

	public T getObject() {
		if (object.length == 0) {
			return null;
		}
		return (T) object[0];
	}

	public Object getObject(int index) {
		return object[index];
	}

	public boolean isObject(Object[] pObject) {
		if (object.length != pObject.length) {
			return false;
		}
		for (int i = 0; i < pObject.length; i++) {
			if (!LangTools.equals(object[i], pObject[i])) {
				if (object[i] != MATCH_ANY && pObject[i] != MATCH_ANY) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void setDefault() {
		super.setDefault();
	}

	@Override
	public void setIconName(String iconName) {
		super.setIconName(iconName);
	}

	@Override
	public void setLabel(String label) {
		super.setLabel(label);
	}

}
