package de.intarsys.tools.nls;

import java.util.Locale;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * Allow interaction with an NLS context.
 * 
 */
@ServiceImplementation(StandardNlsContext.class)
public interface INlsContext {

	/**
	 * @return The currently active {@link Locale}
	 */
	Locale getLocale();

	/**
	 * @return {@code true} if the {@link Locale} stack is not empty.
	 */
	boolean isLocalePushed();

	/**
	 * Pop the currently active {@link Locale} from the stack.
	 * 
	 */
	void popLocale();

	/**
	 * Push the currently active {@link Locale} on the stack.
	 * 
	 * You need to pair this with {@link #popLocale()}.
	 * 
	 * @param locale
	 */
	void pushLocale(Locale locale);

	/**
	 * Assign the currently active {@link Locale}.
	 * 
	 * @param locale
	 */
	void setLocale(Locale locale);

}
