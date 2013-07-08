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

/**
 * A generic implementation for {@link IDeclarationBlock}.
 * 
 */
public class DeclarationBlock extends Declaration implements IDeclarationBlock {

	private IDeclarationElement[] declarations;

	public DeclarationBlock(Object declarationContext) {
		super(declarationContext);
		declarations = new IDeclarationElement[0];
	}

	public DeclarationBlock(Object declarationContext,
			IDeclarationElement[] declarations) {
		super(declarationContext);
		if (declarations == null) {
			throw new IllegalArgumentException("declarations can't be null");
		}
		this.declarations = declarations;
	}

	public void addDeclarationElement(IDeclarationElement pDeclaration) {
		for (int i = 0; i < declarations.length; i++) {
			IDeclarationElement declaration = declarations[i];
			if ((declaration).getName().equals(pDeclaration.getName())) {
				declarations[i] = pDeclaration;
				return;
			}
		}
		IDeclarationElement[] newDeclarations = new IDeclarationElement[declarations.length + 1];
		System.arraycopy(declarations, 0, newDeclarations, 0,
				declarations.length);
		newDeclarations[declarations.length] = pDeclaration;
		declarations = newDeclarations;
	}

	@Override
	public void clear() {
		if (declarations.length == 0) {
			return;
		}
		declarations = new IDeclarationElement[0];
	}

	@Override
	public IDeclaration copy() {
		IDeclarationElement[] copyDeclarations = new IDeclarationElement[size()];
		for (int i = 0; i < declarations.length; i++) {
			IDeclarationElement declaration = declarations[i];
			copyDeclarations[i] = (IDeclarationElement) declaration.copy();
		}
		return new DeclarationBlock(getDeclarationContext(), copyDeclarations);
	}

	@Override
	public IDeclarationElement getDeclarationElement(String name) {
		for (int i = 0; i < declarations.length; i++) {
			IDeclarationElement declaration = declarations[i];
			if ((declaration).getName().equals(name)) {
				return declaration;
			}
		}
		return null;
	}

	public IDeclarationElement[] getDeclarationElements() {
		return declarations;
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	public void moveDown(IDeclarationElement declaration) {
		for (int i = 0; i < declarations.length - 1; i++) {
			if (declarations[i] == declaration) {
				declarations[i] = declarations[i + 1];
				declarations[i + 1] = declaration;
				break;
			}
		}
	}

	public void moveUp(IDeclarationElement declaration) {
		for (int i = 1; i < declarations.length; i++) {
			if (declarations[i] == declaration) {
				declarations[i] = declarations[i - 1];
				declarations[i - 1] = declaration;
				break;
			}
		}
	}

	public boolean removeDeclarationElement(IDeclarationElement declaration) {
		for (int i = 0; i < declarations.length; i++) {
			if (declarations[i] == declaration) {
				IDeclarationElement[] newDeclarations = new IDeclarationElement[declarations.length - 1];
				System.arraycopy(declarations, 0, newDeclarations, 0, i);
				System.arraycopy(declarations, i + 1, newDeclarations, i,
						declarations.length - i - 1);
				declarations = newDeclarations;
				return true;
			}
		}
		return false;
	}

	public int size() {
		return declarations.length;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IDeclarationElement element : declarations) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
