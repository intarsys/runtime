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
 * A declaration allows to modify the state and or behavior of an
 * {@link IFunctorCall}. The {@link IDeclaration} is attached to an object
 * owning an {@link IFunctor} and should be executed against the
 * {@link IFunctorCall} prior to performing the call.
 * <p>
 * An example for a declaration is {@link IArgumentDeclaration}, allowing for
 * argument naming and ordering and to provide default values when an argument
 * is missing.
 * <p>
 * Syntax, semantics and application to the {@link IFunctorCall} of declarations
 * are up to the client.
 * 
 */
public interface IDeclaration {

	/**
	 * Create a copy of this.
	 * 
	 * @return A copy of this.
	 */
	public IDeclaration copy();

	/**
	 * An optional declaration context. This may be for example the object that
	 * will launch the {@link IFunctorCall} later and has parsed some
	 * declarations on startup.
	 * 
	 * @return An optional declaration context.
	 */
	public Object getDeclarationContext();

	/**
	 * <code>true</code> if this declaration has child elements itself.
	 * <p>
	 * A {@link IDeclarationElement} may be a {@link IDeclarationBlock},
	 * supporting nested declarations. You should not use "instanceof
	 * IDeclarationBlock" to check this behavior but this method.
	 * 
	 * @return <code>true</code> if this declaration has child elements itself.
	 */
	public boolean isBlock();
}
