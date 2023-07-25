package de.intarsys.tools.nls;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

import javax.swing.JComponent;

/**
 * This {@link INlsContext} simply falls back to the Java VM implementation.
 * 
 * While implementing the "push" and "pop" style messages, this implementation should typically be used for "single
 * threaded" applications only, as the results are not bound to the thread context.
 * 
 * Changes in the {@link Locale} are not reflected in {@link Locale#getDefault()}.
 */
public class StandardNlsContext implements INlsContext {

	private Deque<Locale> stack = new ArrayDeque<>();

	private Locale current;

	protected void activateLocale(Locale locale) {
		stack.push(locale);
		current = locale;
		JComponent.setDefaultLocale(locale);
	}

	@Override
	public synchronized Locale getLocale() {
		return current == null ? Locale.getDefault() : current;
	}

	@Override
	public synchronized boolean isLocalePushed() {
		return !stack.isEmpty();
	}

	@Override
	public synchronized void popLocale() {
		current = stack.pop();
		setLocale(getLocale());
	}

	@Override
	public synchronized void pushLocale(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		activateLocale(locale);
	}

	@Override
	public synchronized void setLocale(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		stack.pollFirst();
		activateLocale(locale);
	}

}
