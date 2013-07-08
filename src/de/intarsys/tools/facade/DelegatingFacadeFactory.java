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
package de.intarsys.tools.facade;

import de.intarsys.tools.attribute.IAttributeSupport;

/**
 * 
 */
public class DelegatingFacadeFactory extends FacadeFactory {
	private IFacadeFactory[] factories = new IFacadeFactory[10];

	private int count = 0;

	/**
	 * 
	 */
	public DelegatingFacadeFactory() {
		super();
	}

	protected IFacade basicCreate(Object nativeObject) {
		for (int i = 0; i < count; i++) {
			IFacade result = factories[i].createFacade(nativeObject);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public void addFactory(IFacadeFactory factory) {
		if (count >= factories.length) {
			IFacadeFactory[] tempFactories = new IFacadeFactory[count + 2];
			System.arraycopy(factories, 0, tempFactories, 0, count);
			factories = tempFactories;
		}
		factories[count++] = factory;
	}

	public void removeFactory(IFacadeFactory factory) {
		for (int i = 0; i < count; i++) {
			IFacadeFactory oldFactory = factories[i];
			if (oldFactory == factory) {
				if ((i + 1) < count) {
					System.arraycopy(factories, i + 1, factories, i, count - i
							- 1);
				}
				factories[count--] = null;
			}
		}
	}

	final public IFacade createFacade(Object nativeObject) {
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

	private static final Object ATTR_FACADE = new Object();
}
