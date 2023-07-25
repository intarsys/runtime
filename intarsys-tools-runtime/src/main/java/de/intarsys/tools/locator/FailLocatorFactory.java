package de.intarsys.tools.locator;

import java.io.FileNotFoundException;
import java.io.IOException;

public class FailLocatorFactory extends CommonLocatorFactory {

	public static final FailLocatorFactory FACTORY = new FailLocatorFactory();

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		throw new FileNotFoundException("'" + location + "' not found");
	}

}
