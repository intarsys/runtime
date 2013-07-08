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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgumentDeclaration;
import de.intarsys.tools.functor.IDeclaration;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.reflect.ObjectTools;

/**
 * An {@link ICodeExitHandler} that calls a static method.
 * 
 * The source is not expanded.
 */
public class StaticMethodCodeExitHandler extends CommonCodeExitHandler {

	/**
	 * todo use {@link ObjectTools}
	 */
	public StaticMethodCodeExitHandler() {
		super();
	}

	protected IFunctor createMethod(CodeExit codeExit)
			throws FunctorInvocationException {
		checkCodeExitSource(codeExit);
		IFunctor functor = null;
		final String name = codeExit.getSource();
		try {
			int index = name.lastIndexOf('.');
			if (index <= 0) {
				throw new FunctorInvocationException(
						"illegal static method name '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			String classname = name.substring(0, index);
			String methodname = name.substring(index);
			if (!methodname.endsWith("()")) { //$NON-NLS-1$
				throw new FunctorInvocationException(
						"illegal static method name '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			methodname = methodname.substring(1, methodname.length() - 2);
			Class clazz = Class.forName(classname, true,
					codeExit.getClassLoader());
			final IDeclarationBlock declarationBlock = codeExit
					.getDeclarationBlock();
			Class[] parameterTypes = getParameterTypes(declarationBlock);
			try {
				final Method method = clazz.getMethod(methodname,
						parameterTypes);
				functor = new IFunctor() {
					public Object perform(IFunctorCall call)
							throws FunctorInvocationException {
						try {
							Object[] parameters = getParameters(
									declarationBlock, call);
							return method.invoke(null, parameters);
						} catch (Throwable e) {
							throw new FunctorInvocationException(
									"static method invocation for " + name
											+ " failed", e);
						}
					}
				};
				return functor;
			} catch (NoSuchMethodException e1) {
				//
			}
			try {
				final Method method = clazz.getMethod(methodname,
						IFunctorCall.class);
				functor = new IFunctor() {
					public Object perform(IFunctorCall call)
							throws FunctorInvocationException {
						try {
							return method.invoke(null, call);
						} catch (Throwable e) {
							throw new FunctorInvocationException(
									"static method invocation for " + name
											+ " failed", e);
						}
					}
				};
				return functor;
			} catch (NoSuchMethodException e2) {
				//
			}
			final Method method = clazz.getMethod(methodname, IArgs.class);
			functor = new IFunctor() {
				public Object perform(IFunctorCall call)
						throws FunctorInvocationException {
					try {
						return method.invoke(null, call.getArgs());
					} catch (Throwable e) {
						throw new FunctorInvocationException(
								"static method invocation for " + name
										+ " failed", e);
					}
				}
			};
			return functor;
		} catch (Exception e) {
			throw new FunctorInvocationException(
					"static method invocation for " + name + " failed", e);
		}
	}

	protected Object[] getParameters(IDeclarationBlock declarationBlock,
			IFunctorCall call) {
		List<Object> objects = new ArrayList<Object>();
		getParameters(declarationBlock, call, objects);
		return objects.toArray(new Object[objects.size()]);
	}

	protected void getParameters(IDeclarationBlock declarationBlock,
			IFunctorCall call, List<Object> objects) {
		if (declarationBlock == null) {
			return;
		}
		for (IDeclaration declaration : declarationBlock
				.getDeclarationElements()) {
			if (declaration instanceof IArgumentDeclaration) {
				String name = ((IArgumentDeclaration) declaration).getName();
				Object value = call.getArgs().get(name);
				objects.add(value);
			}
		}
	}

	protected Class[] getParameterTypes(IDeclarationBlock declarationBlock) {
		List<Class> declarations = new ArrayList<Class>();
		getParameterTypes(declarationBlock, declarations);
		return declarations.toArray(new Class[declarations.size()]);
	}

	protected void getParameterTypes(IDeclarationBlock declarationBlock,
			List<Class> declarations) {
		if (declarationBlock == null) {
			return;
		}
		for (IDeclaration declaration : declarationBlock
				.getDeclarationElements()) {
			if (declaration instanceof IArgumentDeclaration) {
				declarations
						.add(((IArgumentDeclaration) declaration).getType());
			}
		}
	}

	public Object perform(CodeExit codeExit, IFunctorCall call)
			throws FunctorInvocationException {
		IFunctor functor = (IFunctor) codeExit.getCompiledSource();
		if (functor == null) {
			functor = createMethod(codeExit);
			codeExit.setCompiledSource(functor);
		}
		return functor.perform(call);
	}
}
