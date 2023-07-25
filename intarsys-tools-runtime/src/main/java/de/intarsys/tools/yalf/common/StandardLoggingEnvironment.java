package de.intarsys.tools.yalf.common;

import javax.annotation.PostConstruct;

import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.preferences.IPreferencesSupport;
import de.intarsys.tools.preferences.IPreferencesSyncher;
import de.intarsys.tools.preferences.PreferencesFactory;
import de.intarsys.tools.preferences.PreferencesTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

public class StandardLoggingEnvironment implements IPreferencesSupport, IPreferencesSyncher, ILoggingEnvironment {

	public static final String PREF_LOGLEVEL = "logLevel"; //$NON-NLS-1$

	private IPreferences preferences;

	public StandardLoggingEnvironment() {
		super();
	}

	protected IPreferences createPreferences() {
		IPreferences result = PreferencesFactory.get().getMain().node(getPreferencesName());
		return result;
	}

	public EnumLogLevel getLogLevel() {
		return PreferencesTools.getEnumItem(getPreferences(), EnumLogLevel.META, PREF_LOGLEVEL);
	}

	public EnumMeta<EnumLogLevel> getLogLevelDomain() {
		return EnumLogLevel.META;
	}

	@Override
	public IPreferences getPreferences() {
		if (preferences == null) {
			preferences = createPreferences();
		}
		return preferences;
	}

	public String getPreferencesName() {
		return "logging";
	}

	@PostConstruct
	public void install() {
		preferencesRestore();
	}

	@Override
	public void preferencesRestore() {
		preferencesRestore(getPreferences());
	}

	protected void preferencesRestore(IPreferences preferences) {
		Level logLevel = Level.parse(preferences.get(PREF_LOGLEVEL, "OFF")); //$NON-NLS-1$
		if (logLevel != Level.OFF) {
			ILogger rootLogger = LogTools.getLogger(""); //$NON-NLS-1$
			if (rootLogger.getLevel() != logLevel) {
				rootLogger.setLevel(logLevel);
			}
		}
	}

	@Override
	public void preferencesStore() {
		preferencesStore(getPreferences());
	}

	/**
	 * @param preferences
	 */
	protected void preferencesStore(IPreferences preferences) {
		//
	}

	public void setLogLevel(EnumLogLevel logLevel) {
		getPreferences().put(PREF_LOGLEVEL, logLevel.getId());
		preferencesRestore();
	}

}
