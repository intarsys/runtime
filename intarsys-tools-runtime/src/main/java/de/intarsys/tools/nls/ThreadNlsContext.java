package de.intarsys.tools.nls;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

/**
 * This {@link INlsContext} allows to associate a dedicated state with each {@link Thread}.
 * 
 * Changes in the {@link Locale} are not reflected in {@link Locale#getDefault()}.
 */
public class ThreadNlsContext implements INlsContext {

	private INlsContext baseContext = new StandardNlsContext();

	private ThreadLocal<Deque<Locale>> localeHolder = ThreadLocal.withInitial(() -> new ArrayDeque<Locale>()); // NOSONAR

	protected void activateLocale(Locale locale) {
		// hook method
	}

	public INlsContext getBaseContext() {
		return baseContext;
	}

	@Override
	public Locale getLocale() {
		Deque<Locale> stack = localeHolder.get();
		Locale locale = stack.peek();
		if (locale == null) {
			locale = baseContext.getLocale();
		}
		return locale;
	}

	@Override
	public boolean isLocalePushed() {
		Deque<Locale> stack = localeHolder.get();
		return !stack.isEmpty();
	}

	@Override
	public void popLocale() {
		Deque<Locale> stack = localeHolder.get();
		stack.pop();
		Locale locale = stack.peek();
		activateLocale(locale);
	}

	@Override
	public void pushLocale(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		Deque<Locale> stack = localeHolder.get();
		stack.push(locale);
		activateLocale(locale);
	}

	public void setBaseContext(INlsContext baseContext) {
		this.baseContext = baseContext;
	}

	@Override
	public void setLocale(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		Deque<Locale> stack = localeHolder.get();
		stack.pollFirst();
		stack.push(locale);
		activateLocale(locale);
	}

}
