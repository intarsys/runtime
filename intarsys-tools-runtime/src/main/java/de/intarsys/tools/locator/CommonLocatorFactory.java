package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.string.IPrettyPrintable;
import de.intarsys.tools.string.PrettyPrinter;
import de.intarsys.tools.string.StringTools;

/**
 * A common superclass for implementing an {@link ILocatorFactory}.
 * 
 */
public abstract class CommonLocatorFactory implements ILocatorFactory, IPrettyPrintable {

	protected abstract ILocator basicCreateLocator(String location) throws IOException;

	@Override
	public final ILocator createLocator(String location) throws IOException {
		if (StringTools.isEmpty(location)) {
			return null;
		}
		return basicCreateLocator(location);
	}

	@Override
	public String toString() {
		return new PrettyPrinter().toString(this);
	}

	@Override
	public void toString(PrettyPrinter printer) {
		printer.appendValue(getClass().getName());
	}

}
