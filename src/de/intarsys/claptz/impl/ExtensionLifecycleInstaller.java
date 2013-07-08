/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.claptz.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.PACKAGE;
import de.intarsys.tools.infoset.IElement;

public class ExtensionLifecycleInstaller extends ExtensionPointHandlerAdapter {

	private static final Logger Log = PACKAGE.Log;

	public static final String XE_INSTALL = "install"; //$NON-NLS-1$

	public static final String XE_UNINSTALL = "uninstall"; //$NON-NLS-1$

	@Override
	protected Object basicInstallInsert(IExtension extension, IElement element) {
		if (XE_INSTALL.equals(element.getName())) {
			try {
				performFunctor(extension, element);
			} catch (Exception e) {
				String msg = "error installing extension";
				log(Log, Level.SEVERE, extension, element, msg, e);
			}
		} else if (XE_UNINSTALL.equals(element.getName())) {
			// we will handle this on uninstall
		} else {
			return super.basicInstallInsert(extension, element);
		}
		return null;
	}

	@Override
	protected void basicUninstall(IExtension extension, IElement element) {
		if (XE_INSTALL.equals(element.getName())) {
			// we will handle this on install
		} else if (XE_UNINSTALL.equals(element.getName())) {
			try {
				performFunctor(extension, element);
			} catch (Exception e) {
				String msg = "error uninstalling extension";
				log(Log, Level.SEVERE, extension, element, msg, e);
			}
		} else {
			super.basicUninstall(extension, element);
		}
	}
}
