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
package de.intarsys.tools.infoset;

import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.exception.TunnelingException;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.proxy.IProxy;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.system.SystemTools;

/**
 * A generic implementation to ease implementation of "deferred" objects.
 * <p>
 * This object encapsulates the {@link IElement}, preparing for realization of
 * the intended object on demand.
 */
public class ElementProxy<T> implements IElementConfigurable, IContextSupport, IProxy<T> {

	private IElement element;

	private T realized;

	private Class proxyClass;

	private String proxyClassAttribute;

	private Object context;

	public ElementProxy() {
		// reflective use
		proxyClass = Object.class;
		proxyClassAttribute = "class";
	}

	public ElementProxy(Class pProxyClass, IElement pElement, String classAttribute, Object context) {
		element = pElement;
		proxyClass = pProxyClass;
		proxyClassAttribute = classAttribute;
		this.context = context;
		if (SystemTools.isDebug("eager")) {
			getRealized();
		}
	}

	protected Object basicGetRealized() {
		return realized;
	}

	@Override
	public void configure(IElement pElement) {
		this.element = pElement;
	}

	public ClassLoader getClassLoader() {
		if (getContext() instanceof IClassLoaderSupport) {
			return ((IClassLoaderSupport) getContext()).getClassLoader();
		}
		return getClass().getClassLoader();
	}

	public Object getContext() {
		return context;
	}

	public IElement getElement() {
		return element;
	}

	public Class getProxyClass() {
		return proxyClass;
	}

	public String getProxyClassAttribute() {
		return proxyClassAttribute;
	}

	@Override
	public synchronized T getRealized() {
		if (realized == null) {
			try {
				realized = realize();
			} catch (ObjectCreationException e) {
				throw new TunnelingException(e);
			}
		}
		return realized;
	}

	protected T realize() throws ObjectCreationException {
		T object = (T) ElementTools.createObject(getElement(), getProxyClassAttribute(), getProxyClass(), getContext(),
				Args.create());
		return object;
	}

	@Override
	public void setContext(Object context) {
		this.context = context;
	}

	public void setProxyClass(Class proxyClass) {
		this.proxyClass = proxyClass;
	}

	public void setProxyClassAttribute(String proxyClassAttribute) {
		this.proxyClassAttribute = proxyClassAttribute;
	}

}
