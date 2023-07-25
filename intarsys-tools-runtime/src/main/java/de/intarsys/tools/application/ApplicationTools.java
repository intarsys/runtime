/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.application;

import java.util.Set;

import de.intarsys.tools.component.ComponentException;
import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.component.IVersionSupport;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.preferences.IPreferencesSupport;
import de.intarsys.tools.preferences.PreferencesFactory;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.presentation.PresentationAdapter;

public final class ApplicationTools {

	public static class DefaultStartStop implements IStartStop {

		@Override
		public boolean isStarted() {
			return true;
		}

		@Override
		public void start() throws ComponentException {
			// do nothing by default
		}

		@Override
		public void stop() throws ComponentException {
			System.exit(0);
		}

		@Override
		public boolean stopRequested(Set visited) {
			return true;
		}

	}

	private static IPreferencesSupport PreferencesSupport;

	private static IPreferences Preferences;

	private static IStartStop StartStop = new DefaultStartStop();

	private static IStartStop Loop = new DefaultStartStop();

	public static String getApplicationLabel() {
		Object app = Application.get();
		return getApplicationLabel(app);
	}

	public static String getApplicationLabel(Object app) {
		if (app instanceof IPresentationSupport) {
			return ((IPresentationSupport) app).getLabel();
		}
		return "Application";
	}

	public static IStartStop getApplicationLoop() {
		Object app = Application.get();
		return getApplicationLoop(app);
	}

	public static IStartStop getApplicationLoop(Object app) {
		if (Loop != null) {
			return Loop;
		}
		if (app instanceof IStartStop) {
			return (IStartStop) app;
		}
		return null;
	}

	public static IPreferences getApplicationPreferences() {
		Object app = Application.get();
		return getApplicationPreferences(app);
	}

	public static IPreferences getApplicationPreferences(Object app) {
		if (Preferences != null) {
			return Preferences;
		}
		if (PreferencesSupport != null) {
			return PreferencesSupport.getPreferences();
		}
		if (app instanceof IPreferencesSupport) {
			return ((IPreferencesSupport) app).getPreferences();
		}
		return PreferencesFactory.get().getMain();
	}

	public static IPreferencesSupport getApplicationPreferencesSupport() {
		Object app = Application.get();
		return getApplicationPreferencesSupport(app);
	}

	public static IPreferencesSupport getApplicationPreferencesSupport(Object app) {
		if (PreferencesSupport != null) {
			return PreferencesSupport;
		}
		if (Preferences != null) {
			return new IPreferencesSupport() {
				@Override
				public IPreferences getPreferences() {
					return Preferences;
				}
			};
		}
		if (app instanceof IPreferencesSupport) {
			return ((IPreferencesSupport) app);
		}
		return new IPreferencesSupport() {
			@Override
			public IPreferences getPreferences() {
				return getApplicationPreferences(app);
			}
		};
	}

	public static IPresentationSupport getApplicationPresentation() {
		Object app = Application.get();
		return getApplicationPresentation(app);
	}

	public static IPresentationSupport getApplicationPresentation(Object app) {
		return new PresentationAdapter(app, "Application");
	}

	public static IStartStop getApplicationStartStop() {
		Object app = Application.get();
		return getApplicationStartStop(app);
	}

	public static IStartStop getApplicationStartStop(Object app) {
		if (StartStop != null) {
			return StartStop;
		}
		if (app instanceof IStartStop) {
			return (IStartStop) app;
		}
		return null;
	}

	public static String getApplicationVersion() {
		Object app = Application.get();
		return getApplicationVersion(app);
	}

	public static String getApplicationVersion(Object app) {
		if (app instanceof IVersionSupport) {
			return ((IVersionSupport) app).getVersion();
		}
		return "1.0";
	}

	public static void setApplicationLoop(IStartStop value) {
		Loop = value;
	}

	public static void setApplicationPreferences(IPreferences value) {
		Preferences = value;
	}

	public static void setApplicationPreferencesSupport(IPreferencesSupport value) {
		PreferencesSupport = value;
	}

	public static void setApplicationStartStop(IStartStop value) {
		StartStop = value;
	}

	private ApplicationTools() {
	}

}
