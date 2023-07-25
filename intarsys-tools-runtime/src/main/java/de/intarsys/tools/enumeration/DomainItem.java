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
package de.intarsys.tools.enumeration;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.lang.LangTools;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.presentation.IPresentationSupport;

/**
 * An item in a {@link Domain} enumeration. A {@link DomainItem} links together
 * tuples of objects with presentation logic to make them suitable for
 * presentation in a user interface.
 * 
 * The {@link DomainItem} has a main object and optional details. This can be
 * used to create "adhoc" definitions for selections in a drop down box.
 * 
 */
@SuppressWarnings("java:S2160")
public class DomainItem<T> extends EnumItem {

	public static final Object MATCH_ANY = new Object();

	private T object;

	protected DomainItem(Domain<T> domain, String id, T object) {
		super(domain, id, null, domain.size());
		this.object = object;
		if (object instanceof IPresentationSupport) {
			setLabel(((IPresentationSupport) object).getLabel());
		} else if (object instanceof IMessage) {
			setLabel(object);
		} else {
			setLabel(id);
		}
	}

	protected boolean accept(T pObject) {
		if (pObject == null) {
			return getObject() == null;
		}
		if ((getObject() instanceof IArgs) && (pObject instanceof IArgs)) {
			for (IBinding binding : (IArgs) getObject()) {
				Object a = binding.getValue();
				Object b = ((IArgs) pObject).get(binding.getName());
				if (a == MATCH_ANY || b == MATCH_ANY) {
					continue;
				}
				if (!LangTools.equals(a, b)) {
					return false;
				}
			}
			return true;
		}
		return pObject.equals(getObject());
	}

	public T getObject() {
		return object;
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
	public void setLabel(Object label) {
		super.setLabel(label);
	}

}
