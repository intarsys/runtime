package de.intarsys.tools.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * A common superclass for implementing {@link IMessageBundleFactory}.
 * 
 */
public abstract class CommonMessageBundleFactory implements IMessageBundleFactory, IElementConfigurable {

	private static final ILogger Log = LogTools.getLogger("NLS");

	private final Map<String, CommonMessageBundle> bundles = new ConcurrentHashMap<>();

	private boolean logMode;

	private boolean rawMode;

	protected CommonMessageBundleFactory() {
		super();
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setLogMode(ElementTools.getBoolean(element, "logMode", isLogMode()));
		setRawMode(ElementTools.getBoolean(element, "rawMode", isRawMode()));
	}

	protected abstract CommonMessageBundle createMessageBundle(String name, ClassLoader classloader);

	@Override
	public IMessageBundle getMessageBundle(String name, ClassLoader classloader) {
		return bundles.computeIfAbsent(name, (n) -> {
			if (isLogMode()) {
				Log.info("MessageBundleFactory createMessageBundle {}", name);
			}
			return createMessageBundle(name, classloader);
		});
	}

	public boolean isLogMode() {
		return logMode;
	}

	public boolean isRawMode() {
		return rawMode;
	}

	public void setLogMode(boolean developMode) {
		this.logMode = developMode;
	}

	public void setRawMode(boolean rawMode) {
		this.rawMode = rawMode;
	}

}