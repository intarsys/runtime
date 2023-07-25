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
package de.intarsys.tools.action;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.presentation.IPresentationSupport;

/**
 * An {@link IFunctor} that adds information for GUI representation.
 * <p>
 * Most often this is used to make an objects implementation available to an
 * user interface component such like a button or menu.
 * </p>
 */
public interface IAction
		extends IFunctor, IIdentifiable, IPresentationSupport, IAttributeSupport, INotificationSupport {
	/**
	 * 
	 */
	public static final String ATTR_CHECKED = "checked";

	/**
	 * 
	 */
	public static final String ATTR_ENABLED = "enabled";

	/**
	 * 
	 */
	public static final String ATTR_STYLE = "style";

	/**
	 * <code>true</code> if this action is "checkstyle" and is currently
	 * "checked" or "on".
	 * 
	 * @param call
	 * 
	 */
	public boolean isChecked(IFunctorCall call);

	/**
	 * <code>true</code> if this action is "checkstyle". This means the actions
	 * has built in state that toggles from "checked" or "on" to "unchecked" or
	 * "off".
	 * 
	 * @return <code>true</code> if this action is "checkstyle".
	 */
	public boolean isCheckStyle();

	/**
	 * <code>true</code> if this action is currently enabled.
	 * 
	 * @param call
	 * 
	 */
	public boolean isEnabled(IFunctorCall call);

	/**
	 * <code>true</code> if this action is "pushstyle". This means the actions
	 * simply serves as a stateless source for events.
	 * 
	 * @return <code>true</code> if this action is "pushstyle".
	 */
	public boolean isPushStyle();

}
