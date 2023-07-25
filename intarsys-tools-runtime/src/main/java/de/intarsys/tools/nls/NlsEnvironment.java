package de.intarsys.tools.nls;

import java.io.IOException;
import java.util.Locale;

import javax.swing.JComponent;

import de.intarsys.tools.component.SingletonClass;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.locator.ClassLoaderResourceLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.message.IMessageBundleFactory;
import de.intarsys.tools.message.MessageBundleFactory;
import de.intarsys.tools.message.OverrideMessageBundleFactory;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * This is an old style implementation that was usde in applet/bridge scenarios.
 * 
 * It allowed to dynamically switch locales and overwrite NLS messages.
 * 
 * @deprecated
 */
@Deprecated
@SingletonClass
public class NlsEnvironment {

	private static final ILogger Log = LogTools.getLogger(NlsEnvironment.class);

	private static final String ARG_LANGUAGE = "language"; //$NON-NLS-1$
	private static final String ARG_LOGMODE = "logMode"; //$NON-NLS-1$
	private static final String ARG_OVERRIDELOCATOR = "overrideLocator"; //$NON-NLS-1$
	private static final String ARG_OVERRIDESUFFIX = "overrideSuffix"; //$NON-NLS-1$
	private static final String ARG_RAWMODE = "rawMode"; //$NON-NLS-1$

	private static final NlsEnvironment ACTIVE = new NlsEnvironment();

	public static NlsEnvironment get() {
		return ACTIVE;
	}

	private NlsEnvironment() {
	}

	public void configure(IArgs args) {
		OverrideMessageBundleFactory bundleFactory = getOrCreateExtendedMessageBundleFactory();

		String languageTag = ArgTools.getString(args, ARG_LANGUAGE, null);
		if (!StringTools.isEmpty(languageTag)) {
			setLocale(languageTag);
		}

		// either:
		bundleFactory.setLogMode(ArgTools.getBoolStrict(args, ARG_LOGMODE, bundleFactory.isLogMode()));
		bundleFactory.setRawMode(ArgTools.getBoolStrict(args, ARG_RAWMODE, bundleFactory.isRawMode()));
		bundleFactory.setOverrideSuffix(ArgTools.getString(args, ARG_OVERRIDESUFFIX, bundleFactory
				.getOverrideSuffix()));
		ILocator overrideLocator = ArgTools.getLocator(args, ARG_OVERRIDELOCATOR, null, (
				location) -> new ClassLoaderResourceLocator(Thread.currentThread().getContextClassLoader(), location));
		if (overrideLocator != null) {
			try {
				bundleFactory.load(overrideLocator);
			} catch (IOException ex) {
				Log.log(Level.WARN, "NLS override file '{}' failed to load", overrideLocator.getName(), ex);
			}
		}
	}

	public Locale getLocale() {
		return Locale.getDefault();
	}

	protected OverrideMessageBundleFactory getOrCreateExtendedMessageBundleFactory() {
		IMessageBundleFactory bundleFactory = MessageBundleFactory.get();
		if (bundleFactory instanceof OverrideMessageBundleFactory) {
			return (OverrideMessageBundleFactory) bundleFactory;
		}
		// will have no effect on already loaded bundles
		OverrideMessageBundleFactory extendedFactory = new OverrideMessageBundleFactory();
		return extendedFactory;
	}

	protected void setLocale(Locale locale) {
		Locale.setDefault(locale);
		JComponent.setDefaultLocale(locale);
	}

	protected void setLocale(String languageTag) {
		Locale locale = Locale.forLanguageTag(languageTag);
		setLocale(locale);
	}
}
