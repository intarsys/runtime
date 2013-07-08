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
package de.intarsys.tools.presentation;

/**
 * These objects can be presented by a GUI.
 */
public interface IPresentationSupport {

	public static final Object ATTR_DESCRIPTION = "description"; //$NON-NLS-1$

	public static final Object ATTR_LABEL = "label"; //$NON-NLS-1$

	public static final Object ATTR_ICON = "icon"; //$NON-NLS-1$

	public static final Object ATTR_TIP = "tip"; //$NON-NLS-1$

	/**
	 * A long, descriptive string representation.
	 * 
	 * @return A long, descriptive string representation.
	 */
	public String getDescription();

	/**
	 * A name for an icon.
	 * 
	 * @return A name for an icon.
	 */
	public String getIconName();

	/**
	 * A short string representation (suitable for example for use with an icon
	 * or menu item).
	 * 
	 * @return A short string representation (suitable for example for use with
	 *         an icon or menu item).
	 */
	public String getLabel();

	/**
	 * An "intermediate" length description (suitable for example with a
	 * tooltip).
	 * 
	 * @return An "intermediate" length description (suitable for example with a
	 *         tooltip).
	 */
	public String getTip();
}
