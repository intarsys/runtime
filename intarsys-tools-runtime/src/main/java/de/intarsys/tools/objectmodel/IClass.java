/*
 * Copyright (c) 2008, intarsys GmbH
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
package de.intarsys.tools.objectmodel;

/**
 * The abstraction of a class in this object model.
 * 
 */
public interface IClass {

	/**
	 * All known {@link IField} instances of this. If no fields are available,
	 * this method returns an empty array.
	 * 
	 * @return All known {@link IField} instances of this.
	 */
	public IField[] getFields();

	/**
	 * All known {@link IMethod} instances of this. If no methods are available,
	 * this method returns an empty array.
	 * 
	 * @return All known {@link IMethod} instances of this.
	 */
	public IMethod[] getMethods();

	/**
	 * The unique selector to this {@link IClass}.
	 * 
	 * @return The unique selector to this {@link IClass}.
	 */
	public IClassSelector getSelector();

	/**
	 * The single {@link IField} with the given name.
	 * 
	 * @param name
	 *            The name of the {@link IField} to lookup.
	 * 
	 * @return The single {@link IField} with the given name.
	 */
	public IField lookupField(String name);

	/**
	 * The single {@link IMethod} with the given name.
	 * 
	 * @param name
	 *            The name of the {@link IMethod} to lookup.
	 * 
	 * @return The single {@link IMethod} with the given name.
	 */
	public IMethod lookupMethod(String name);

}
