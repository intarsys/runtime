package de.intarsys.tools.message;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.intarsys.tools.nls.INlsContext;
import de.intarsys.tools.nls.NlsContext;

/**
 * An {@link IMessageBundle} that resolves a message code with regard to an {@link INlsContext}. This means that the
 * lookup conditions (like {@link Locale}) may change at any time.
 * 
 * So, this is a container for more basic {@link IMessageBundle} instances that each have a dedicated {@link Locale}.
 * 
 */
public class NlsContextMessageBundle extends CommonMessageBundle {

	private final Map<Locale, IMessageBundle> messageBundles = new ConcurrentHashMap<>();

	public NlsContextMessageBundle(CommonMessageBundleFactory factory, String name,
			ClassLoader classLoader) {
		super(factory, name, classLoader);
	}

	@Override
	public Set<String> getCodes() {
		return getMessageBundleForLocale().getCodes();
	}

	protected IMessageBundle getMessageBundleForLocale() {
		Locale locale = NlsContext.get().getLocale();
		return messageBundles.computeIfAbsent(locale, (l) -> new BasicMessageBundle(getFactory(), getName(), l,
				getClassLoader()));
	}

	@Override
	public String getPattern(String code) {
		return getMessageBundleForLocale().getPattern(code);
	}

	@Override
	public String getString(String code, Object... args) {
		return getMessageBundleForLocale().getString(code, args);
	}

}
