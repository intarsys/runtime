package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgsSupport;

/**
 * An {@link ILocator} that can bundle another {@link ILocator} with a set of {@link IArgs}.
 *
 * <p>
 * This may be useful to complement the {@link ILocator} with higher level properties, for a
 * {@code de.intarsys.document.model.IDocument} for example.
 */
public class LocatorWithArgs extends DelegatingLocator implements IArgsSupport {

	private final ILocator locator;

	private final IArgs args;

	public LocatorWithArgs(ILocator locator, IArgs args) {
		this.locator = locator;
		this.args = args;
	}

	@Override
	public IArgs getArgs() {
		return args;
	}

	@Override
	protected ILocator getLocator() throws IOException {
		return locator;
	}

}
