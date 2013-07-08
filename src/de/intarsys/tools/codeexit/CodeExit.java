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
package de.intarsys.tools.codeexit;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.ExpressionEvaluator;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorSupport;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.StaticArgsResolver;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.ArgumentDeclarator;
import de.intarsys.tools.functor.DeclarationBlock;
import de.intarsys.tools.functor.DeclarationException;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationSupport;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.common.DeclarationIO;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.reflect.IClassLoaderAccess;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

/**
 * A CodeExit is an {@link IFunctor} implemented in a "foreign" language such as
 * a scripting language or a proprietary syntax.
 * <p>
 * This is a flexible and powerful pattern if you need pluggable functors in
 * your application to dynamically add features.
 */
public class CodeExit<T> implements IFunctor<T>, IClassLoaderAccess,
		IElementConfigurable, IElementSerializable, IDeclarationSupport {

	public static final String EA_NAME = "name"; //$NON-NLS-1$

	public static final String EE_ARG = "arg"; //$NON-NLS-1$

	public static final String EE_HANDLER = "handler"; //$NON-NLS-1$

	public static final String EE_DECLARATIONS = "declarations"; //$NON-NLS-1$

	public static final String EA_SOURCE = "source"; //$NON-NLS-1$

	public static final String EA_TYPE = "type"; //$NON-NLS-1$

	public static CodeExit<?> createFromElement(IElement element)
			throws ObjectCreationException {
		CodeExit<?> codeExit = new CodeExit();
		try {
			codeExit.configure(element);
		} catch (ConfigurationException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			if (cause instanceof ObjectCreationException) {
				throw (ObjectCreationException) cause;
			} else {
				throw new ObjectCreationException(e);
			}
		}
		return codeExit;
	}

	private String source;

	private Object compiledSource;

	private IDeclarationBlock declarationBlock = new DeclarationBlock(this);

	/**
	 * The implementation context. This may be interpreted as the object
	 * implementing the business logic represented by this.
	 */
	private Object owner;

	/** The class loader for accessing dependent resources */
	private ClassLoader classLoader;

	/** The type of code exit we want to call */
	private String type;

	private String name = "codeexit"; //$NON-NLS-1$

	private IElement element;

	private ICodeExitHandler handler;

	/**
	 * A no-arg constructor for easy reflective access.
	 */
	public CodeExit() {
		super();
	}

	public CodeExit(Object owner) {
		super();
		setOwner(owner);
	}

	public void configure(IElement pElement)
			throws ConfigurationException {
		this.element = pElement;
		String tempType = pElement.attributeValue(EA_TYPE, null);
		setType(tempType);
		String tempSource = pElement.attributeValue(EA_SOURCE, null);
		if (tempSource == null) {
			IElement sourceElement = pElement.element(EA_SOURCE);
			if (sourceElement != null) {
				tempSource = sourceElement.getText();
			}
		}
		setSource(tempSource);
		try {
			declarationBlock = new DeclarationBlock(this);
			new DeclarationIO().deserializeDeclarationBlock(declarationBlock,
					element);
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	public CodeExit<?> copy() {
		CodeExit<?> clone = new CodeExit();
		clone.classLoader = classLoader;
		clone.declarationBlock = (DeclarationBlock) declarationBlock.copy();
		clone.name = name;
		clone.owner = owner;
		clone.source = source;
		clone.type = type;
		return clone;
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

	protected MapResolver createExpressionEvaluator(IArgs args) {
		MapResolver resolver = new MapResolver(true);
		resolver.put("args", new StaticArgsResolver(args));
		return resolver;
	}

	public boolean exists() {
		ICodeExitHandler handler = getCodeExitHandler();
		if (handler instanceof ICodeExitMetaHandler) {
			return ((ICodeExitMetaHandler) handler).exists(this);
		}
		return true;
	}

	/**
	 * The {@link ClassLoader} to be used when accessing resources.
	 * 
	 * @return The {@link ClassLoader} to be used when accessing resources.
	 */
	public ClassLoader getClassLoader() {
		ClassLoader result = classLoader;
		if (result == null) {
			if (getOwner() == null) {
				result = getClass().getClassLoader();
			} else {
				if (getOwner() instanceof IClassLoaderSupport) {
					result = ((IClassLoaderSupport) getOwner())
							.getClassLoader();
				} else {
					result = getOwner().getClass().getClassLoader();
				}
			}
		}
		return result;
	}

	public ICodeExitHandler getCodeExitHandler() {
		return CodeExitHandlerRegistry.get().lookupCodeExitHandler(getType());
	}

	public Object getCompiledSource() {
		return compiledSource;
	}

	public IDeclarationBlock getDeclarationBlock() {
		return declarationBlock;
	}

	/**
	 * The {@link IElement} that was used to configure this.
	 * 
	 * @return The element that was used to configure this.
	 */
	public IElement getElement() {
		return element;
	}

	/**
	 * The {@link IElement} that was used configured to be forwarded to a
	 * {@link ICodeExitHandler}.
	 * 
	 * @return The {@link IElement} that was used configured to be forwarded to
	 *         a handler.
	 */
	public IElement getHandlerElement() {
		return element.element(EE_HANDLER);
	}

	public String getName() {
		return name;
	}

	/**
	 * A generic link to an object that is the "owner" of this.
	 * 
	 * @return A generic link to an object that is the "owner" of this.
	 */
	public Object getOwner() {
		return owner;
	}

	/**
	 * The source code.
	 * 
	 * @return The source code.
	 */
	public String getSource() {
		return source;
	}

	public String getSourceExpanded(IArgs args) {
		IStringEvaluator evaluator = TemplateEvaluator.get();
		if (owner instanceof IStringEvaluatorSupport) {
			IStringEvaluator resolver = ((IStringEvaluatorSupport) owner)
					.getStringEvaluator();
			evaluator = TaggedStringEvaluator.decorate(
					createExpressionEvaluator(args), resolver,
					ExpressionEvaluator.get());
		} else {
			evaluator = TaggedStringEvaluator.decorate(
					createExpressionEvaluator(args), ExpressionEvaluator.get());
		}
		try {
			return StringTools.safeString(evaluator.evaluate(source,
					Args.create()));
		} catch (EvaluationException e) {
			return source;
		}
	}

	/**
	 * The type. This token selects under a variety of {@link ICodeExitHandler}
	 * instances to support different implementation languages.
	 * 
	 * @return The type.
	 */
	public String getType() {
		return type;
	}

	public boolean isDefinedSource() {
		return !StringTools.isEmpty(source);
	}

	public boolean isDefinedType() {
		return !StringTools.isEmpty(type);
	}

	public T perform(IFunctorCall call) throws FunctorInvocationException {
		try {
			IFunctorCall forwardCall = createCall(call);
			if (handler == null) {
				handler = getCodeExitHandler();
				if (handler == null) {
					throw new FunctorInvocationException(
							"no ICodeExitHandler for type '" //$NON-NLS-1$
									+ getType() + "'"); //$NON-NLS-1$
				}
			}
			return (T) handler.perform(this, forwardCall);
		} catch (FunctorInvocationException e) {
			throw e;
		} catch (Throwable t) {
			throw new FunctorInvocationException("unexpected error", t); //$NON-NLS-1$
		}
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		IElement child = element.newElement("perform");
		child.setAttributeValue("type", getType()); //$NON-NLS-1$
		child.setAttributeValue("source", getSource()); //$NON-NLS-1$
		new DeclarationIO().serializeDeclarationBlock(getDeclarationBlock(),
				child);
	}

	/**
	 * Set the {@link ClassLoader} to be used with this {@link CodeExit}. This
	 * should be the {@link ClassLoader} instance that would give access to all
	 * resources needed by the {@link CodeExit}'s business logic implementation.
	 * 
	 * @param classLoader
	 *            The {@link ClassLoader} to be used to access the resources
	 *            needed by the {@link CodeExit}'s business logic
	 *            implementation.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.compiledSource = null;
	}

	public void setCompiledSource(Object compiledSource) {
		this.compiledSource = compiledSource;
	}

	public void setName(String name) {
		this.name = name;
		this.compiledSource = null;
	}

	/**
	 * Set an owner for the {@link CodeExit}.
	 * <p>
	 * This should be used in conjunction with the no-arg constructor in
	 * reflective programming only.
	 * 
	 * @param owner
	 */
	public void setOwner(Object owner) {
		if (this.owner != null && this.owner != owner) {
			throw new IllegalStateException("can not reassign owner"); //$NON-NLS-1$
		}
		this.owner = owner;
		this.compiledSource = null;
	}

	/**
	 * Assign the source.
	 * 
	 * @param value
	 *            The source string.
	 */
	public void setSource(String value) {
		this.source = value;
		this.compiledSource = null;
	}

	/**
	 * Assign the type.
	 * 
	 * @param type
	 *            The type string.
	 */
	public void setType(String type) {
		this.type = type;
		this.compiledSource = null;
	}
}
