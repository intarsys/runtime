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
package de.intarsys.tools.functor;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.functor.common.DeclarationIO;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * An {@link IFunctor} that will add declaration features to a nested
 * {@link IFunctor}.
 */
public class DeclarationFunctor<T> implements IFunctor<T>,
		IElementConfigurable, IElementSerializable, IDeclarationSupport,
		IContextSupport {

	private IDeclarationBlock declarationBlock = new DeclarationBlock(this);

	private IFunctor functor;

	private Object context;

	public DeclarationFunctor() {
		super();
	}

	public DeclarationFunctor(IFunctor functor) {
		super();
		this.functor = functor;
	}

	@Override
	public void configure(IElement pElement)
			throws ConfigurationException {
		setFunctor(createImplementor(pElement));
		try {
			declarationBlock = new DeclarationBlock(this);
			new DeclarationIO().deserializeDeclarationBlock(declarationBlock,
					pElement);
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	protected IFunctorCall createCall(IFunctorCall call)
			throws FunctorInvocationException {
		try {
			new ArgumentDeclarator().apply(getDeclarationBlock(),
					call.getArgs());
			return call;
		} catch (DeclarationException e) {
			throw new FunctorInvocationException(e);
		}
	}

	protected IFunctor createImplementor(IElement element)
			throws ConfigurationException {
		IElement implementorElement = element.element("implementor"); //$NON-NLS-1$
		if (implementorElement != null) {
			try {
				return ElementTools.createObject(implementorElement,
						IFunctor.class, getContext());
			} catch (ObjectCreationException e) {
				throw new ConfigurationException(e);
			}
		}
		return null;
	}

	public Object getContext() {
		return context;
	}

	@Override
	public IDeclarationBlock getDeclarationBlock() {
		return declarationBlock;
	}

	public IFunctor getFunctor() {
		return functor;
	}

	@Override
	public T perform(IFunctorCall call) throws FunctorInvocationException {
		try {
			if (getFunctor() != null) {
				IFunctorCall forwardCall = createCall(call);
				return (T) getFunctor().perform(forwardCall);
			}
			return null;
		} catch (FunctorInvocationException e) {
			throw e;
		} catch (Throwable t) {
			throw new FunctorInvocationException("unexpected error", t); //$NON-NLS-1$
		}
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		element.setAttributeValue("class", getClass().getName());
		IElement implementorElement = element.newElement("implementor"); //$NON-NLS-1$
		if (getFunctor() instanceof IElementSerializable) {
			((IElementSerializable) getFunctor()).serialize(implementorElement);
		} else {
			if (getFunctor() != null) {
				implementorElement.setAttributeValue("class", getFunctor() //$NON-NLS-1$
						.getClass().getName());
			}
		}
		new DeclarationIO().serializeDeclarationBlock(getDeclarationBlock(),
				element);
	}

	@Override
	public void setContext(Object context) {
		this.context = context;
	}

	public void setFunctor(IFunctor functor) {
		this.functor = functor;
	}

}
