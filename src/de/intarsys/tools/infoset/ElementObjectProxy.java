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
package de.intarsys.tools.infoset;

import de.intarsys.claptz.IExtension;
import de.intarsys.tools.exception.TunnelingException;
import de.intarsys.tools.proxy.IProxy;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A generic implementation to ease implementation of "deferred" objects
 * declared via an {@link IExtension}.
 * <p>
 * This object encapsulates the {@link IExtension} and the {@link Element},
 * preparing for realization of the intended object on demand. Two common
 * scenarios are supported. In the first, the provider of the extension point
 * itself creates the {@link ElementObjectProxy} directly to avoid the cost of
 * reflective class access. In the second, a concrete factory object may be
 * derived from {@link ElementObjectProxy} to inherit its lazyness with regard
 * to hosting an object to be realized. The concrete proxy class name may be
 * declared in an another element attribute than "class".
 * 
 */
public class ElementObjectProxy implements IElementConfigurable, IProxy {

	/**
	 * The link to the definition element in the extension
	 */
	private IElement element;

	private Object realized;

	final private Class proxyClass;

	final private String proxyClassAttribute;

	final private ClassLoader classLoader;

	public ElementObjectProxy() {
		// reflective use
		// extension and element are set via "configure"
		proxyClass = Object.class;
		proxyClassAttribute = "class";
		classLoader = getClass().getClassLoader();
	}

	/**
	 * 
	 */
	public ElementObjectProxy(Class pProxyClass, IElement pElement,
			ClassLoader pClassLoader) {
		element = pElement;
		proxyClass = pProxyClass;
		classLoader = pClassLoader;
		proxyClassAttribute = "class";
	}

	public ElementObjectProxy(Class pProxyClass, IElement pElement,
			String classAttribute, ClassLoader pClassLoader) {
		element = pElement;
		proxyClass = pProxyClass;
		classLoader = pClassLoader;
		proxyClassAttribute = classAttribute;
	}

	protected Object basicGetRealized() {
		return realized;
	}

	@Override
	public void configure(IElement pElement) {
		this.element = pElement;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
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
	synchronized public Object getRealized() {
		if (realized == null) {
			try {
				realized = realize();
			} catch (ObjectCreationException e) {
				throw new TunnelingException(e);
			}
		}
		return realized;
	}

	protected Object realize() throws ObjectCreationException {
		Object object = ElementTools.createObject(getElement(),
				getProxyClassAttribute(), getProxyClass(), getClassLoader());
		return object;
	}

}
