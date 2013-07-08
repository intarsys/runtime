package de.intarsys.tools.logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * An {@link IHandlerFactory} that creates a {@link CompositeHandler} by
 * delegating to a collection of other {@link IHandlerFactory} instances.
 * 
 */
public class CompositeHandlerFactory extends CommonHandlerFactory {

	private List<IHandlerFactory> handlerFactories = new ArrayList<IHandlerFactory>();

	public void addLogHandlerFactory(IHandlerFactory factory) {
		handlerFactories.add(factory);
	}

	@Override
	protected Handler basicCreateHandler() throws IOException {
		CompositeHandler handler = new CompositeHandler();
		for (IHandlerFactory factory : handlerFactories) {
			handler.addHandler(factory.createLogHandler());
		}
		return handler;
	}

	public void removeLogHandlerFactory(IHandlerFactory factory) {
		handlerFactories.remove(factory);
	}

}
