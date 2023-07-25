package de.intarsys.tools.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * An {@link IMessageBundle} that supports overrides via API.
 * 
 * This implementation is a fragment of a legacy component - it is untested (and unused).
 */
public class OverrideMessageBundle extends CommonMessageBundle {

	private static final ILogger Log = LogTools.getLogger("NLS");

	private IMessageBundle overrideBundle;

	private final Map<String, String> overrides = new HashMap<>();

	public OverrideMessageBundle(OverrideMessageBundleFactory factory, String name) {
		super(factory, name, null);
	}

	protected void clear() {
		overrides.clear();
	}

	@Override
	public Set<String> getCodes() {
		HashSet<String> codes = new HashSet<>();
		codes.addAll(overrides.keySet());
		if (getOverrideBundle() != null) {
			codes.addAll(getOverrideBundle().getCodes());
		}
		return codes;
	}

	protected IMessageBundle getOverrideBundle() {
		return overrideBundle;
	}

	@Override
	public String getPattern(String code) {
		String result = overrides.get(code);
		if (result == null) {
			if (getOverrideBundle() != null) {
				result = getOverrideBundle().getPattern(code);
			}
		}
		if (isLogMode()) {
			Log.info("ExtendedMessageBundle.getPattern,{},{},{}", getName(), code, result);
		}
		return result;
	}

	@Override
	public String getString(String code, Object... args) {
		String result = null;
		if (isRawMode()) {
			result = "{" + code + "}";
		} else {
			String pattern = overrides.get(code);
			if (pattern != null) {
				result = format(pattern, args);
			}
			if (result == null) {
				if (getOverrideBundle() != null) {
					if (getOverrideBundle().getPattern(code) != null) {
						result = getOverrideBundle().getString(code, args);
					}
				}
			}
		}
		if (isLogMode()) {
			Log.info("ExtendedMessageBundle.getString,{},{},{}", getName(), code, result);
		}
		return result;
	}

	@Override
	protected boolean isLogMode() {
		return getFactory().isLogMode();
	}

	@Override
	protected boolean isRawMode() {
		return getFactory().isRawMode();
	}

	protected void load(ILocator locator) throws IOException {
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = locator.getInputStream();
			properties.load(is);
		} catch (Exception e) {
			StreamTools.close(is);
			throw e;
		}
		overrides.putAll(new HashMap(properties));
	}

	protected void register(String code, String pattern) {
		overrides.put(code, pattern);
	}

	protected void setOverrideBundle(IMessageBundle overrideBundle) {
		this.overrideBundle = overrideBundle;
	}

	protected void unregister(String code) {
		overrides.remove(code);
	}
}
