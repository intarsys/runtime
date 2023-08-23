/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.action;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A registry for managing {@link IAction} instances.
 * 
 */
@ServiceImplementation(StandardActionRegistry.class)
public interface IActionRegistry {
	/**
	 * The collection of all registered {@link IAction} instances.
	 * 
	 * @return All registered {@link IAction} instances.
	 */
	public IAction[] getActions();

	/**
	 * The {@link IAction} registered with <code>id</code> or <code>null</code>.
	 * 
	 * @param id
	 *            The id of the {@link IAction} to lookup.
	 * 
	 * @return The {@link IAction} registered with <code>id</code> or
	 *         <code>null</code>.
	 */
	public IAction lookupAction(String id);

	/**
	 * Register <code>action</code>.
	 * 
	 * @param action
	 *            The {@link IAction} to be registered.
	 */
	public void registerAction(IAction action);

	/**
	 * Unregister <code>action</code>.
	 * 
	 * @param action
	 *            The {@link IAction} to be unregistered.
	 */
	public void unregisterAction(IAction action);
}
