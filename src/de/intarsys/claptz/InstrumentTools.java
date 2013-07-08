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
package de.intarsys.claptz;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.ExtensionInstallationException;
import de.intarsys.claptz.impl.ExtensionOperation;
import de.intarsys.claptz.impl.StandardInstrumentRegistry;
import de.intarsys.claptz.io.IInstrumentStore;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.reflect.ObjectCreationException;

public class InstrumentTools {

	private static final Logger Log = PACKAGE.Log;

	/**
	 * A tool method to simplify handling of "singleton" extensions, for example
	 * configuration information.
	 * 
	 * @param extensionPointId
	 * @return
	 */
	public static IExtension getLastExtension(IInstrumentRegistry registry,
			String extensionPointId) {
		IExtensionPoint ep = registry.lookupExtensionPoint(extensionPointId);
		if (ep == null) {
			Log.log(Level.WARNING, "extension point '" + extensionPointId //$NON-NLS-1$
					+ "' not found"); //$NON-NLS-1$
			return null;
		}
		IExtension[] extensions = ep.getExtensions();
		if (extensions.length > 0) {
			return extensions[extensions.length - 1];
		}
		return null;
	}

	static public IExtension getOrCreateExtension(IInstrument provider,
			String extensionPointId, String id) throws ObjectCreationException,
			InstrumentRegistryException {
		IExtension[] extensions = provider.getExtensions();
		IExtension targetExtension = null;
		for (IExtension extension : extensions) {
			if (!extension.getExtensionPoint().getId().equals(extensionPointId)) {
				continue;
			}
			if (id != null && !extension.getId().equals(id)) {
				continue;
			}
			targetExtension = extension;
			break;
		}
		if (targetExtension == null) {
			targetExtension = provider.createExtension(extensionPointId, id);
			IInstrument prerequisiteProvider = provider.getInstrumentRegistry()
					.lookupExtensionPoint(extensionPointId).getProvider();
			provider.addPrerequisite(prerequisiteProvider,
					StandardInstrumentRegistry.ACTION_SKIP);
			provider.registerExtension(targetExtension);
		}
		return targetExtension;
	}

	/**
	 * Helper method to register all extensions to an extension point correctly.
	 * This method takes care to call "functor" exactly once for all available
	 * and future extensions to {@link IExtensionPoint} "point".
	 * <p>
	 * It is safe to call this method repeatedly with the identical functor
	 * object. Access is synchronized and installation will be executed at most
	 * once.
	 * <p>
	 * You should take care to <code>uninstallExtensions</code> to protect
	 * against memory leaks.
	 * <p>
	 * 
	 * ATTENTION: "functor" must be a unique (not transient) object as it is
	 * used as a handle to guard against multiple installs.
	 * 
	 * @param point
	 * @param elementName
	 * @param functor
	 * @throws ExtensionInstallationException
	 */
	static public void installExtensions(IInstrumentRegistry registry,
			String point, final IExtensionPointHandler functor)
			throws InstrumentRegistryException {
		IExtensionPoint extensionPoint = registry.lookupExtensionPoint(point);
		if (extensionPoint == null) {
			Log.log(Level.WARNING, "extension point '" + point + "' not found"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		functor.setExtensionPoint(extensionPoint);
		functor.install();
	}

	/**
	 * Check if <code>newPrerequisite</code> is required already directly or
	 * indirectly
	 * 
	 * @param newPrerequisite
	 * @return
	 */
	static public boolean isPrerequisiteImplied(IInstrument instrument,
			IInstrument requiredInstrument) {
		if (requiredInstrument == instrument) {
			return true;
		}
		for (IInstrumentPrerequisite prerequisite : instrument
				.getPrerequisites()) {
			IInstrument tempInstrument = prerequisite.getInstrument();
			if (isPrerequisiteImplied(tempInstrument, requiredInstrument)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Lookup the {@link IExtension} within extensionPoint that originally
	 * defined the object (performed an "insert" operation).
	 * 
	 * @param extensionPoint
	 * @param object
	 * @return
	 */
	public static IExtension lookupDefiningExtension(
			IExtensionPoint extensionPoint, Object object) {
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			List<ExtensionOperation> attachments = extension.getOperations();
			for (ExtensionOperation attachment : attachments) {
				if (attachment.isInsert() && attachment.getObject() == object) {
					return extension;
				}
			}
		}
		return null;
	}

	public static IInstrument lookupInstrument(IInstrumentRegistry registry,
			String instrumentId) {
		IInstrument iInstrument = registry.lookupInstrument(instrumentId);
		if (iInstrument == null) {
			return null;
		}
		return iInstrument.getState().isStarted() ? iInstrument : null;
	}

	public static IInstrument lookupOrCreateInstrument(
			IInstrumentRegistry registry, String instrumentId,
			IInstrumentStore store) throws ObjectCreationException,
			InstrumentRegistryException {
		String tempId = instrumentId;
		int counter = 1;
		IInstrument iInstrument = registry.lookupInstrument(tempId);
		while (iInstrument != null && !iInstrument.getState().isStarted()) {
			tempId = instrumentId + "." + counter++; //$NON-NLS-1$
			iInstrument = registry.lookupInstrument(tempId);
		}
		if (iInstrument == null) {
			iInstrument = registry.createInstrument(tempId, store);
			registry.registerInstrument(iInstrument);
		}
		return iInstrument;
	}

	static public IExtension objectDelete(IInstrument provider,
			String extensionPointId, Object target)
			throws ObjectCreationException, InstrumentRegistryException,
			InstrumentRegistryException {
		IInstrument targetProvider = null;
		IExtension targetExtension = null;
		if (provider != null) {
			// if we are advised to use a dedicated provider - do so
			targetProvider = provider;
		} else {
			// todo? create a default provider, get a global provider?
			throw new IllegalStateException("provider can't be null");
		}
		targetExtension = InstrumentTools.getOrCreateExtension(targetProvider,
				extensionPointId, null);
		// now add target to the set of managed objects by this extension
		targetExtension.attachDelete(target);

		IExtension tempExtension = InstrumentTools.lookupDefiningExtension(
				targetExtension.getExtensionPoint(), target);
		if (tempExtension != null) {
			IInstrument tempProvider = tempExtension.getProvider();
			provider.addPrerequisite(tempProvider,
					StandardInstrumentRegistry.ACTION_SKIP);
		}

		IExtensionPointHandler installer = targetExtension.getExtensionPoint()
				.getInstaller();
		if (installer != null) {
			installer.update(targetExtension);
		}
		return targetExtension;
	}

	/**
	 * Initiate the insertion of the definition for object within an
	 * {@link IExtension} to provider.
	 * <p>
	 * The system looks up or creates the {@link IExtension} for
	 * extensionPointId, manages the prerequisites and requests the
	 * serialization of the object within the {@link IExtension}.
	 * 
	 * @param provider
	 * @param extensionPointId
	 * @param target
	 * @return
	 * @throws ObjectCreationException
	 * @throws ExtensionInstallationException
	 * @throws InstrumentRegistryException
	 */
	static public IExtension objectInsert(IInstrument provider,
			String extensionPointId, Object target)
			throws ObjectCreationException, InstrumentRegistryException {
		IInstrument targetProvider = null;
		IExtension targetExtension = null;
		if (provider != null) {
			// if we are advised to use a dedicated provider - do so
			targetProvider = provider;
		} else {
			// todo? create a default provider, get a global provider?
			throw new IllegalStateException("provide can't be null");
		}
		targetExtension = InstrumentTools.getOrCreateExtension(targetProvider,
				extensionPointId, null);
		// now add target to the set of managed objects by this extension
		targetExtension.attachInsert(target);
		if (target instanceof IContextSupport) {
			try {
				((IContextSupport) target).setContext(targetExtension
						.getProvider());
			} catch (ConfigurationException e) {
				throw new InstrumentRegistryException(e.getLocalizedMessage(),
						e);
			}
		}

		IExtension tempExtension = InstrumentTools.lookupDefiningExtension(
				targetExtension.getExtensionPoint(), target);
		if (tempExtension != null) {
			IInstrument tempProvider = tempExtension.getProvider();
			targetProvider.addPrerequisite(tempProvider,
					StandardInstrumentRegistry.ACTION_SKIP);
		}

		IExtensionPointHandler installer = targetExtension.getExtensionPoint()
				.getInstaller();
		if (installer != null) {
			installer.update(targetExtension);
		}

		return targetExtension;
	}

	static public IExtension objectUpdate(IInstrument provider,
			String extensionPointId, Object target)
			throws ObjectCreationException, InstrumentRegistryException {
		IInstrument targetProvider = null;
		IExtension targetExtension = null;
		if (provider != null) {
			// if we are advised to use a dedicated provider - do so
			targetProvider = provider;
		} else {
			// todo? create a default provider, get a global provider?
			throw new IllegalStateException("provide can't be null");
		}
		targetExtension = InstrumentTools.getOrCreateExtension(targetProvider,
				extensionPointId, null);
		// now add target to the set of managed objects by this extension
		targetExtension.attachUpdate(target);

		IExtension tempExtension = InstrumentTools.lookupDefiningExtension(
				targetExtension.getExtensionPoint(), target);
		if (tempExtension != null) {
			IInstrument tempProvider = tempExtension.getProvider();
			provider.addPrerequisite(tempProvider,
					StandardInstrumentRegistry.ACTION_SKIP);
		}

		IExtensionPointHandler installer = targetExtension.getExtensionPoint()
				.getInstaller();
		if (installer != null) {
			installer.update(targetExtension);
		}
		return targetExtension;
	}

	/**
	 * Clean up artifacts / references to functor.
	 * 
	 * @param point
	 * @param functor
	 * @throws ExtensionInstallationException
	 */
	static public void uninstallExtensions(IInstrumentRegistry registry,
			String point, final IExtensionPointHandler functor)
			throws InstrumentRegistryException {
		IExtensionPoint extensionPoint = registry.lookupExtensionPoint(point);
		if (extensionPoint == null) {
			Log.log(Level.WARNING, "extension point '" + point + "' not found"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		functor.uninstall();
	}
}
