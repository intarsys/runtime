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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.IExtensionPoint;
import de.intarsys.claptz.IExtensionPointHandler;
import de.intarsys.claptz.InstrumentRegistryException;
import de.intarsys.claptz.InstrumentTools;
import de.intarsys.claptz.PACKAGE;
import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reporter.IReporter;
import de.intarsys.tools.reporter.Reporter;
import de.intarsys.tools.string.StringTools;

/**
 * An adapter providing a default implementation for
 * {@link IExtensionPointHandler}.
 */
public class ExtensionPointHandlerAdapter implements IExtensionPointHandler {

	private static final Logger Log = PACKAGE.Log;

	private final Attribute ATTR_INSTALLED = new Attribute("installed"); //$NON-NLS-1$

	private IExtensionPoint extensionPoint;

	private boolean installed = false;

	protected List<ExtensionOperation> basicGetAttachments(IExtension extension) {
		return extension.getOperations();
	}

	protected void basicInstall() throws InstrumentRegistryException {
		IExtension[] extensions = extensionPoint.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			try {
				install(extension);
			} catch (Throwable t) {
				Reporter.get().reportError(
						"Error",
						StringTools.safeString(extension)
								+ " unexpected installation error", t,
						IReporter.STYLE_NONE);
			}
		}
	}

	protected void basicInstall(IExtension extension) {
		IElement root = extension.getElement();
		for (Iterator<IElement> it = root.elementIterator(); it.hasNext();) {
			IElement childElement = it.next();
			try {
				String op = childElement.attributeValue(
						ExtensionOperation.EA_OPERATION, null);
				if (op == null || ExtensionOperation.EAV_INSERT.equals(op)) {
					Object object = basicInstallInsert(extension, childElement);
					if (object != null) {
						extension.attachInsert(object);
					}
				} else if (ExtensionOperation.EAV_UPDATE.equals(op)) {
					Object object = basicInstallUpdate(extension, childElement);
					if (object != null) {
						extension.attachUpdate(object);
					}
				} else if (ExtensionOperation.EAV_DELETE.equals(op)) {
					Object object = basicInstallDelete(extension, childElement);
					if (object != null) {
						extension.attachDelete(object);
					}
				} else {
					Reporter.get().reportError(
							"Error",
							StringTools.safeString(extension) + " element "
									+ childElement.getName()
									+ " unsupported operation " + op, null,
							IReporter.STYLE_NONE);
				}
			} catch (Throwable t) {
				Reporter.get().reportError(
						"Error",
						StringTools.safeString(extension) + " element "
								+ childElement.getName()
								+ " unexpected installation error", t,
						IReporter.STYLE_NONE);
			}
		}
	}

	protected Object basicInstallDelete(IExtension extension, IElement element) {
		Log.log(Level.WARNING, StringTools.safeString(extension)
				+ " delete not supported for element '" + element.getName()
				+ "'");
		return null;
	}

	protected Object basicInstallInsert(IExtension extension, IElement element) {
		Log.log(Level.WARNING, StringTools.safeString(extension)
				+ " unknown element '" + element.getName() + "'");
		return null;
	}

	protected Object basicInstallUpdate(IExtension extension, IElement element) {
		Log.log(Level.WARNING, StringTools.safeString(extension)
				+ " update not supported for element '" + element.getName()
				+ "'");
		return null;
	}

	protected void basicUninstall() {
		IExtension[] extensions = getExtensionPoint().getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			try {
				uninstall(extension);
			} catch (Throwable t) {
				Reporter.get().reportError(
						"Error",
						StringTools.safeString(extension)
								+ " unexpected uninstall error", t,
						IReporter.STYLE_NONE);
			}
		}
	}

	protected void basicUninstall(IExtension extension) {
		IElement root = extension.getElement();
		for (Iterator<IElement> it = root.elementIterator(); it.hasNext();) {
			IElement childElement = it.next();
			try {
				basicUninstall(extension, childElement);
			} catch (Throwable t) {
				Reporter.get().reportError(
						"Error",
						StringTools.safeString(extension) + " element "
								+ childElement.getName()
								+ " unexpected uninstallation error", t,
						IReporter.STYLE_NONE);
			}
		}
	}

	protected void basicUninstall(IExtension extension, IElement element) {
		// nothing todo by default
	}

	@Override
	public IExtensionPoint getExtensionPoint() {
		return extensionPoint;
	}

	protected List<Object> getManagedObjects(IExtension extension) {
		return new ArrayList<Object>();
	}

	protected void initialize() {
	}

	@Override
	final public void install() throws InstrumentRegistryException {
		synchronized (this) {
			// the IExtensionPoint is installed exactly once
			if (isInstalled()) {
				return;
			}
			setInstalled(true);
			basicInstall();
		}
	}

	@Override
	final public void install(IExtension extension)
			throws InstrumentRegistryException {
		synchronized (this) {
			// the IExtension is installed exactly once
			if (extension.getAttribute(ATTR_INSTALLED) != null) {
				return;
			}
			extension.setAttribute(ATTR_INSTALLED, Boolean.TRUE);
			basicInstall(extension);
		}
	}

	@Override
	public boolean isActive() {
		return isInstalled() || !isDeferred();
	}

	public boolean isDeferred() {
		return false;
	}

	protected boolean isInstalled() {
		return installed;
	}

	protected void log(Logger log, Level level, IExtension extension,
			IElement element, String msg, Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringTools.safeString(extension));
		sb.append(" element '");
		sb.append(element.getName());
		sb.append("': ");
		sb.append(msg);
		log.log(level, sb.toString(), e);
	}

	protected void log(Logger log, Level level, IExtension extension,
			String msg, Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringTools.safeString(extension));
		sb.append(": ");
		sb.append(msg);
		log.log(level, sb.toString(), e);
	}

	protected Object performFunctor(IExtension extension, IElement element)
			throws ObjectCreationException, FunctorInvocationException {
		IFunctor functor = ElementTools.createFunctor(extension.getProvider(), element,
		extension.getProvider());
		Args args = Args.create();
		args.put("installer", this);
		args.put("extension", this);
		args.put("element", this);
		IFunctorCall call = new FunctorCall(extension, args);
		return functor.perform(call);
	}

	protected Object performNestedFunctor(IExtension extension,
			IElement element, String functorElementName)
			throws ObjectCreationException, FunctorInvocationException {
		IFunctor functor = ElementTools.createFunctor(extension.getProvider(), element.element(functorElementName),
		extension.getProvider());
		Args args = Args.create();
		args.put("installer", this);
		args.put("extension", this);
		args.put("element", this);
		IFunctorCall call = new FunctorCall(extension, args);
		return functor.perform(call);
	}

	@Override
	final public void setExtensionPoint(IExtensionPoint extensionPoint) {
		this.extensionPoint = extensionPoint;
		initialize();
	}

	protected void setInstalled(boolean installed) {
		this.installed = installed;
	}

	@Override
	final public void uninstall() throws InstrumentRegistryException {
		synchronized (this) {
			// the IExtensionPoint is uninstalled exactly once
			if (getExtensionPoint() == null
					|| getExtensionPoint().getAttribute(ATTR_INSTALLED) == null) {
				return;
			}
			extensionPoint = null;
			getExtensionPoint().setAttribute(ATTR_INSTALLED, null);
			basicUninstall();
		}
	}

	@Override
	final public void uninstall(IExtension extension)
			throws InstrumentRegistryException {
		synchronized (this) {
			// the IExtensionPoint is uninstalled exactly once
			if (extension.getAttribute(ATTR_INSTALLED) == null) {
				return;
			}
			extension.setAttribute(ATTR_INSTALLED, null);
			basicUninstall(extension);
		}
	}

	@Override
	public void update(IExtension extension) throws InstrumentRegistryException {
		// update the serialized version of the current object model
		List<ExtensionOperation> attachments = basicGetAttachments(extension);
		extension.getElement().elementsClear();
		for (ExtensionOperation attachment : attachments) {
			try {
				attachment.perform(extension);
			} catch (Exception e) {
				log(Log, Level.WARNING, extension, e.getLocalizedMessage(), e);
			}
		}
	}

}
