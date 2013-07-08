package de.intarsys.tools.logging;

import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract public class CommonHandlerFactory implements IHandlerFactory {

	private boolean singleton = false;

	private Level level = Level.OFF;

	private Handler singletonHandler;

	private Formatter formatter;

	abstract protected Handler basicCreateHandler() throws IOException;

	@Override
	synchronized final public Handler createLogHandler() throws IOException {
		if (isSingleton() && singletonHandler != null) {
			return singletonHandler;
		}
		Handler tempHandler = basicCreateHandler();
		if (getLevel() != Level.OFF) {
			tempHandler.setLevel(getLevel());
		} else {
			tempHandler.setLevel(Logger.getLogger("").getLevel());
		}
		if (getFormatter() != null) {
			tempHandler.setFormatter(getFormatter());
		}
		if (isSingleton()) {
			singletonHandler = tempHandler;
		}
		return tempHandler;
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public Level getLevel() {
		return level;
	}

	protected Handler getSingletonHandler() {
		return singletonHandler;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
		if (singletonHandler != null) {
			singletonHandler.setFormatter(formatter);
		}
	}

	public void setLevel(Level level) {
		this.level = level;
		if (getLevel() != Level.OFF && singletonHandler != null) {
			singletonHandler.setLevel(level);
		}
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	protected void setSingletonHandler(Handler singletonHandler) {
		this.singletonHandler = singletonHandler;
	}

}
