package de.intarsys.tools.system;

import javax.annotation.PostConstruct;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * A functor object that can inject a system property
 * 
 */
public class SystemProperty {

	private static final ILogger Log = LogTools.getLogger(SystemProperty.class);

	private String key;

	private String value;

	private String usage;

	public String getKey() {
		return key;
	}

	public String getUsage() {
		return usage;
	}

	public String getValue() {
		return value;
	}

	@PostConstruct
	public void perform() {
		String oldValue = System.getProperty(key, null);
		if ("always".equals(usage)) {
			System.setProperty(key, value);
			if (oldValue == null) {
				Log.log(Level.INFO, "set system property '" + key + "' to '" + value + "'");
			} else {
				Log.log(Level.INFO, "change system property '" + key + "' from '" + oldValue + "' to '" + value + "'");
			}
		} else if ("optional".equals(usage)) {
			if (oldValue == null) {
				System.setProperty(key, value);
				Log.log(Level.INFO, "set system property '" + key + "' to '" + value + "'");
			} else {
				Log.log(Level.INFO, "keep system property '" + key + "' as '" + oldValue + "'");
			}
		} else if ("never".equals(usage)) {
			//
		} else {
			throw new IllegalArgumentException("SystemProperty unknown usage " + usage);
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
