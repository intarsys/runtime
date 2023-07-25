/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.facade;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.attribute.IAttributeSupport;

/**
 * 
 */
public class DelegatingFacadeFactory extends FacadeFactory {

	private static final Object ATTR_FACADE = new Object();

	private List<IFacadeFactory> factories = new ArrayList<>();

	/**
	 * 
	 */
	public DelegatingFacadeFactory() {
		super();
	}

	public void addFactory(IFacadeFactory factory) {
		factories.add(factory);
	}

	protected IFacade basicCreate(Object nativeObject) {
		for (IFacadeFactory factory : factories) {
			IFacade result = factory.createFacade(nativeObject);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public final IFacade createFacade(Object nativeObject) {
		if (nativeObject instanceof IAttributeSupport) {
			IAttributeSupport as = (IAttributeSupport) nativeObject;
			IFacade result = (IFacade) as.getAttribute(ATTR_FACADE);
			if (result == null) {
				result = basicCreate(nativeObject);
				if (result != null) {
					as.setAttribute(ATTR_FACADE, result);
				}
			}
			return result;
		}
		if (nativeObject != null) {
			return basicCreate(nativeObject);
		}
		return null;
	}

	public void removeFactory(IFacadeFactory factory) {
		factories.remove(factory);
	}
}
