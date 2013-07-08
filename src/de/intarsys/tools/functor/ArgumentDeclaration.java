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

import de.intarsys.tools.crypto.Secret;

/**
 * An argument declaration supporting argument naming, default values and
 * typing.
 * 
 */
public class ArgumentDeclaration extends DeclarationElement implements
		IArgumentDeclaration, IDeclarationSupport {

	public static ArgumentDeclaration declare(
			IDeclarationBlock declarationBlock, String pPath,
			String pModifiers, Object pDefaultValue, Class pType)
			throws DeclarationException {
		return declare(declarationBlock, pPath, pModifiers, pDefaultValue,
				pType, null);
	}

	public static ArgumentDeclaration declare(
			IDeclarationBlock declarationBlock, String pPath,
			String pModifiers, Object pDefaultValue, Class pType,
			String description) throws DeclarationException {
		String[] segments;
		if (".".equals(pPath)) {
			segments = new String[] { "." };
		} else {
			segments = pPath.split("\\.");
		}
		IDeclarationBlock currentBlock = declarationBlock;
		IDeclarationElement declarationElement = null;
		for (int i = 0; i < segments.length; i++) {
			String segment = segments[i];
			declarationElement = currentBlock.getDeclarationElement(segment);
			if (declarationElement == null) {
				declarationElement = new ArgumentDeclaration(
						currentBlock.getDeclarationContext(), segment, null);
				currentBlock.addDeclarationElement(declarationElement);
			}
			if (declarationElement instanceof IDeclarationBlock) {
				currentBlock = (IDeclarationBlock) declarationElement;
			} else if (declarationElement instanceof IDeclarationSupport) {
				currentBlock = ((IDeclarationSupport) declarationElement)
						.getDeclarationBlock();
			} else {
				throw new DeclarationException("unexpected declaration element");
			}
		}
		if (declarationElement instanceof ArgumentDeclaration) {
			ArgumentDeclaration argumentDeclaration = (ArgumentDeclaration) declarationElement;
			argumentDeclaration.setModifierString(pModifiers);
			argumentDeclaration.setType(pType);
			argumentDeclaration.setDefaultValue(pDefaultValue);
			argumentDeclaration.setDescription(description);
			return argumentDeclaration;
		} else {
			throw new DeclarationException("unexpected declaration element");
		}
	}

	private IFunctor defaultFunctor;

	private Object defaultValue;

	private Class type;

	final private DeclarationBlock declarationBlock;

	public static final Object VALUE_UNDEFINED = new Object();

	public ArgumentDeclaration(Object declarationContext, String name,
			String modifiers) {
		this(declarationContext, name, modifiers, null);
	}

	public ArgumentDeclaration(Object declarationContext, String name,
			String modifiers, Object defaultValue, Class type) {
		this(declarationContext, name, modifiers, defaultValue, type, null);
	}

	public ArgumentDeclaration(Object declarationContext, String name,
			String modifiers, Object defaultValue, Class type,
			String description) {
		super(declarationContext, name, modifiers, description);
		declarationBlock = new DeclarationBlock(declarationContext);
		setDefaultValue(defaultValue);
		setType(type);
	}

	protected ArgumentDeclaration(Object declarationContext, String name,
			String modifiers, Object defaultValue, IFunctor defaultFunctor,
			Class type, DeclarationBlock block) {
		super(declarationContext, name, modifiers);
		this.defaultValue = defaultValue;
		this.defaultFunctor = defaultFunctor;
		setType(type);
		this.declarationBlock = block;
	}

	public ArgumentDeclaration(Object declarationContext, String name,
			String modifiers, String description) {
		super(declarationContext, name, modifiers, description);
		declarationBlock = new DeclarationBlock(declarationContext);
		this.defaultFunctor = null;
		this.defaultValue = ArgumentDeclaration.VALUE_UNDEFINED;
		setType(Object.class);
	}

	@Override
	public ArgumentDeclaration copy() {
		return new ArgumentDeclaration(getDeclarationContext(), getName(),
				getModifierString(), defaultValue, defaultFunctor, getType(),
				(DeclarationBlock) getDeclarationBlock().copy());
	}

	public ArgumentDeclaration declare(String pName, String pModifiers,
			Object pDefaultValue, Class pType) {
		ArgumentDeclaration newDeclaration = new ArgumentDeclaration(
				getDeclarationContext(), pName, pModifiers, pDefaultValue,
				pType);
		getDeclarationBlock().addDeclarationElement(newDeclaration);
		return newDeclaration;
	}

	public IDeclarationBlock getDeclarationBlock() {
		return declarationBlock;
	}

	public IFunctor getDefaultFunctor() {
		return defaultFunctor;
	}

	public String getDefaultLabel() {
		if (defaultValue == ArgumentDeclaration.VALUE_UNDEFINED) {
			if (defaultFunctor instanceof ConstantFunctor) {
				return String.valueOf(((ConstantFunctor) defaultFunctor)
						.getConstant());
			} else if (defaultFunctor == null) {
				return "";
			} else {
				return String.valueOf(defaultFunctor);
			}
		} else {
			return String.valueOf(defaultValue);
		}
	}

	@Override
	public Object getDefaultValue(IArgs scope)
			throws FunctorInvocationException {
		if (declarationBlock.size() > 0) {
			return Args.create();
		}
		if (defaultValue != ArgumentDeclaration.VALUE_UNDEFINED) {
			return defaultValue;
		} else {
			if (getDefaultFunctor() != null) {
				return getDefaultFunctor()
						.perform(new FunctorCall(null, scope));
			}
			return null;
		}
	}

	public Class getType() {
		return type;
	}

	public String getTypeLabel() {
		return getType().getName();
	}

	@Override
	public boolean isDefaultDefined() {
		if (defaultValue == ArgumentDeclaration.VALUE_UNDEFINED) {
			return defaultFunctor != null;
		} else {
			return true;
		}
	}

	public void setDefaultFunctor(IFunctor defaultFunctor) {
		this.defaultFunctor = defaultFunctor;
		this.defaultValue = ArgumentDeclaration.VALUE_UNDEFINED;
		if (defaultFunctor != null) {
			declarationBlock.clear();
		}
	}

	public void setDefaultValue(Object defaultValue) {
		if (defaultValue instanceof Secret) {
			Secret secret = (Secret) defaultValue;
			this.defaultValue = secret.getValue();
			addModifier("secret");
		} else {
			this.defaultValue = defaultValue;
		}
		this.defaultFunctor = null;
		if (defaultValue != ArgumentDeclaration.VALUE_UNDEFINED) {
			declarationBlock.clear();
		}
	}

	public void setDefaultValueUndefined() {
		this.defaultValue = ArgumentDeclaration.VALUE_UNDEFINED;
		this.defaultFunctor = null;
	}

	public void setType(Class pType) {
		if (pType == null || pType == IArgs.class) {
			pType = Object.class;
		}
		this.type = pType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTypeLabel());
		sb.append(" ");
		if (getModifierString() != null) {
			sb.append(getModifierString());
			sb.append(" ");
		}
		sb.append(getName());
		if (isDefaultDefined()) {
			sb.append(" = ");
			try {
				sb.append(getDefaultValue(Args.create()));
			} catch (FunctorInvocationException e) {
				sb.append("<evaluation error>");
			}
		}
		sb.append(";");
		for (IDeclarationElement element : getDeclarationBlock()
				.getDeclarationElements()) {
			String[] lines = element.toString().split("\\n");
			for (String line : lines) {
				sb.append("\n\t");
				sb.append(line);
			}
		}
		return sb.toString();
	}

}
