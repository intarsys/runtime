/*
 * Copyright (c) 2011, intarsys consulting GmbH
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

import java.util.Iterator;
import java.util.Set;

/**
 * The arguments for executing an {@link IFunctor} implementation.
 * 
 */
public interface IArgs {

	/**
	 * A binding between an index or name and a value in an {@link IArgs}
	 * object.
	 * 
	 */
	public interface IBinding {

		public String getName();

		public Object getValue();

		public boolean isDefined();

		public void setName(String name);

		public void setValue(Object value);
	}

	/**
	 * Generic argument name for the transport of IArgs.
	 */
	public static final String ARG_ARGS = "args"; //$NON-NLS-1$

	/**
	 * Add an indexed value.
	 * 
	 * @param object
	 */
	public IBinding add(Object object);

	/**
	 * An {@link Iterator} over all available bindings.
	 * 
	 * @return An {@link Iterator} over all available bindings.
	 */
	public Iterator<IBinding> bindings();

	/**
	 * Clear all bindings.
	 */
	public void clear();

	/**
	 * Create a copy.
	 * 
	 * @return The new copy.
	 */
	public IArgs copy();

	/**
	 * Declare a name for a binding. This does NOT "define" in any way the
	 * binding.
	 * 
	 * @param name
	 * @return The binding resulting from the declaration.
	 */
	public IBinding declare(String name);

	/**
	 * The argument at position <code>index</code>.
	 * 
	 * @param index
	 *            The index of the argument to return.
	 * 
	 * @return The argument at position <code>index</code>.
	 */
	public Object get(int index);

	/**
	 * The argument at position <code>index</code>
	 * 
	 * @param index
	 *            The index of the argument to return.
	 * @param defaultValue
	 *            The default value to be returned if argument is not available.
	 * 
	 * @return The argument at position <code>index</code>
	 */
	public Object get(int index, Object defaultValue);

	/**
	 * The argument named <code>name</code> or null if not available.
	 * 
	 * @param name
	 *            The name of the argument to return.
	 * 
	 * @return The argument named <code>name</code> or null if not available.
	 */
	public Object get(String name);

	/**
	 * The argument named <code>name</code> or the defaultValue if not
	 * available.
	 * 
	 * @param name
	 *            The name of the argument to return.
	 * @param defaultValue
	 *            The default value to be returned if argument is not available.
	 * 
	 * @return The argument named <code>name</code>
	 */
	public Object get(String name, Object defaultValue);

	/**
	 * <code>true</code> if an argument at index is defined.
	 * 
	 * @param index
	 * @return <code>true</code> if an argument at index is defined.
	 */
	public boolean isDefined(int index);

	/**
	 * <code>true</code> if an argument named <code>name</code> is defined.
	 * 
	 * @param name
	 * @return <code>true</code> if an argument named <code>name</code> is
	 *         defined.
	 */
	public boolean isDefined(String name);

	/**
	 * The set of all argument names in the argument list if this argument list
	 * is not indexed or null.
	 * 
	 * @return The set of all argument names in the argument list if this
	 *         argument list is not indexed or null.
	 */
	public Set<String> names();

	/**
	 * Define an indexed binding for value.
	 * 
	 * @param index
	 * @param value
	 */
	public IBinding put(int index, Object value);

	/**
	 * Define a named binding for value.
	 * 
	 * @param name
	 * @param value
	 */
	public IBinding put(String name, Object value);

	/**
	 * The total number of arguments.
	 * 
	 * @return The total number of arguments.
	 */
	public int size();

	/**
	 * Undefine the binding at index.
	 * 
	 * @param index
	 */
	public void undefine(int index);

	/**
	 * Undefine the binding for name.
	 * 
	 * @param name
	 */
	public void undefine(String name);
}
