package de.intarsys.tools.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * An {@link IMessageBundleFactory} wrapper that allows overriding messages via API.
 * 
 * This implementation is a fragment of a legacy component - it is currently unused and may be obsolete.
 */
public class OverrideMessageBundleFactory extends CommonMessageBundleFactory {

	private static final ILogger Log = LogTools.getLogger("NLS");

	private final IMessageBundleFactory factory = new BasicMessageBundleFactory();

	private String overrideSuffix;

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		setOverrideSuffix(ElementTools.getString(element, "overrideSuffix", getOverrideSuffix()));
	}

	@Override
	protected CommonMessageBundle createMessageBundle(String name, ClassLoader classloader) {
		OverrideMessageBundle result = new OverrideMessageBundle(this, name);
		if (!StringTools.isEmpty(getOverrideSuffix())) {
			IMessageBundle overrideBundle = getFactory().getMessageBundle(name + getOverrideSuffix(), classloader);
			result.setOverrideBundle(overrideBundle);
		}
		return result;
	}

	public IMessageBundleFactory getFactory() {
		return factory;
	}

	public String getOverrideSuffix() {
		return overrideSuffix;
	}

	public void load(ILocator locator) throws IOException {
		Log.info("ExtendedMessageBundleFactory.load {}", locator.getPath());
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = locator.getInputStream();
			properties.load(is);
		} catch (Exception e) {
			StreamTools.close(is);
			throw e;
		}
		for (Map.Entry entry : properties.entrySet()) {
			String extKey = (String) entry.getKey();
			register(extKey, (String) entry.getValue());
		}
	}

	public void register(String extKey, String value) {
		String[] ref = extKey.split("#");
		if (ref.length < 2) {
			Log.warn("ExtendedMessageBundleFactory.register '{}' malformed", extKey);
			return;
		}
		String bundle = ref[0];
		String key = ref[1];
		OverrideMessageBundle mb = (OverrideMessageBundle) getMessageBundle(bundle, getClass().getClassLoader());
		mb.register(key, value);
	}

	public void setOverrideSuffix(String overridePrefix) {
		this.overrideSuffix = overridePrefix;
	}

	public void unregister(String extKey) {
		String[] ref = extKey.split("#");
		if (ref.length < 2) {
			Log.warn("ExtendedMessageBundleFactory.register '{}' malformed", extKey);
		}
		String bundle = ref[0];
		String key = ref[1];
		OverrideMessageBundle mb = (OverrideMessageBundle) getMessageBundle(bundle, getClass().getClassLoader());
		mb.unregister(key);
	}
}
