package de.intarsys.tools.logging;

import java.io.IOException;
import java.util.logging.Handler;

public interface IHandlerFactory {

	public Handler createLogHandler() throws IOException;

}
