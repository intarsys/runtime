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
 * A concrete named declaration. An {@link IDeclarationElement} may have
 * optional modifiers, supporting fine tuning of its behavior.
 * 
 */
public interface IDeclarationElement extends IDeclaration {

	public static final String MOD_TRANSIENT = "transient";

	/**
	 * The string representation of all modifiers set.
	 * 
	 * @return
	 */
	public String getModifierString();

	/**
	 * An optional name for the declaration element.
	 * 
	 * @return An optional name for the declaration element.
	 */
	public String getName();

	/**
	 * <code>true</code> if the declaration has the requested modifier.
	 * <p>
	 * An implementation is free to support modifiers and define their
	 * respective semantics.
	 * <p>
	 * An example for a modifier may be "persistent" to indicate the
	 * implementation should store argument values to be able to present them
	 * again in a later call (in a wizard for example).
	 * 
	 * @param modifier
	 *            The modifier name.
	 * 
	 * @return <code>true</code> if the declaration has the requested modifier.
	 * 
	 */
	public boolean hasModifier(String modifier);
}
