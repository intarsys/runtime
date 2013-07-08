package de.intarsys.tools.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A {@link Handler} that applies some filtering before delegating to the
 * wrapped handler. This way you can reuse a {@link Handler} with different
 * filter characteristics on different {@link Logger} instances.
 * 
 */
public class FilterHandler extends DelegatingHandler {

	public FilterHandler(Handler handler) {
		super(handler);
	}

	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		super.publish(record);
	}

}
