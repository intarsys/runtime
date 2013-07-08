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
 * A group of {@link IDeclarationElement} instances.
 * 
 */
public interface IDeclarationBlock extends IDeclaration {

	/**
	 * Add an {@link IDeclarationElement} to this block;
	 * 
	 * @param element
	 */
	public void addDeclarationElement(IDeclarationElement element);

	/**
	 * Remove all {@link IDeclarationElement} instances from this block;
	 * 
	 */
	public void clear();

	/**
	 * The {@link IDeclarationElement} for the given name.
	 * 
	 * @param name
	 * @return The {@link IDeclarationElement} for the given name.
	 */
	public IDeclarationElement getDeclarationElement(String name);

	/**
	 * The array of {@link IDeclarationElement} instances in this block.
	 * 
	 * @return The array of {@link IDeclarationElement} instances in this block.
	 */
	public IDeclarationElement[] getDeclarationElements();

	/**
	 * Move element to the position after its current position. This means that
	 * the element declaration will be performed later.
	 * <p>
	 * If the element is the last declaration or not contained in the block this
	 * method does nothing.
	 * 
	 * @param element
	 */
	public void moveDown(IDeclarationElement element);

	/**
	 * Move element to the position before its current position. This means that
	 * the element declaration will be performed earlier.
	 * <p>
	 * If the element is the first declaration or not contained in the block
	 * this method does nothing.
	 * 
	 * @param element
	 */
	public void moveUp(IDeclarationElement element);

	/**
	 * Remove an {@link IDeclarationElement} from this block;
	 * 
	 * @param element
	 * @return <code>true</code> if declaration was removed
	 */
	public boolean removeDeclarationElement(IDeclarationElement element);

	/**
	 * The number of declarations in this block.
	 * 
	 * @return The number of declarations in this block.
	 */
	public int size();

}
