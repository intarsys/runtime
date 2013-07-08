package de.intarsys.tools.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.impl.ExtensionPointHandlerAdapter;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

public class FactoryInstaller extends ExtensionPointHandlerAdapter {

	private static final Logger Log = PACKAGE.Log;

	public static final String XE_FACTORY = "factory"; //$NON-NLS-1$

	public static final String XP_FACTORIES = "com.cabaret.claptz.factory.factories"; //$NON-NLS-1$

	public FactoryInstaller() {
		super();
	}

	@Override
	protected Object basicInstallInsert(IExtension extension, IElement element) {
		if (XE_FACTORY.equals(element.getName())) {
			try {
				IFactory factory = ElementTools.createObject(element,
						IFactory.class, extension.getProvider());
				Outlet.get().registerFactory(factory);
			} catch (ObjectCreationException e) {
				String msg = "error creating service factory";
				log(Log, Level.SEVERE, extension, element, msg, e);
			}
		} else {
			return super.basicInstallInsert(extension, element);
		}
		return null;
	}

}
