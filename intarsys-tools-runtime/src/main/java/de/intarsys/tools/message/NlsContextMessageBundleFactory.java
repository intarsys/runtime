package de.intarsys.tools.message;

import java.util.Locale;

import de.intarsys.tools.nls.INlsContext;

/**
 * An {@link IMessageBundleFactory} that creates {@link IMessageBundle} instances that are sensitive to an
 * {@link INlsContext} (which may provide a different {@link Locale} for each call).
 * 
 */
public class NlsContextMessageBundleFactory extends CommonMessageBundleFactory {

	private ClassLoader classloader;

	@Override
	protected CommonMessageBundle createMessageBundle(String name, ClassLoader pClassloader) {
		ClassLoader activeClassLoader = (getClassloader() == null) ? pClassloader : getClassloader();
		return new NlsContextMessageBundle(this, name, activeClassLoader);
	}

	public ClassLoader getClassloader() {
		return classloader;
	}

	public void setClassloader(ClassLoader classloader) {
		this.classloader = classloader;
	}

}
