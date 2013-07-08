package de.intarsys.tools.locator;

import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;

/**
 * Provides lookup of an {@link ILocator} relative to a list of search path
 * parents.
 * 
 * The searchpath is defined as a ";" separated list of relative or absolute
 * path names. Relative names are looked up within parent.
 */
public class SearchPathLocatorFactory extends DelegatingLocatorFactory
		implements IElementConfigurable, IContextSupport {

	public static final String PATH_SEPARATOR = ";"; //$NON-NLS-1$

	public static final String EA_SEARCHPATH = "searchpath"; //$NON-NLS-1$

	private String searchpath;

	public SearchPathLocatorFactory() {
		super(new LookupLocatorFactory());
	}

	protected void addSearchPath(File parent, String path) {
		File child = FileTools.resolvePath(parent, path);
		FileLocator locator = new FileLocator(child);
		locator.setSynchSynchronous(true);
		((LookupLocatorFactory) getFactory())
				.addLocatorFactory(new RelativeLocatorFactory(locator));
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		searchpath = element.attributeValue(EA_SEARCHPATH, null);
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		if (context instanceof IFileEnvironment) {
			File parent = ((IFileEnvironment) context).getBaseDir();
			setSearchPath(parent, searchpath);
		}
	}

	public void setSearchPath(File parent, String paths) {
		((LookupLocatorFactory) getFactory()).clear();
		// set up search paths
		for (Enumeration e = new StringTokenizer(paths, PATH_SEPARATOR); e
				.hasMoreElements();) {
			String path = (String) e.nextElement();
			if ((path != null) && (path.trim().length() > 0)) {
				addSearchPath(parent, path);
			}
		}
	}

}
