package de.intarsys.tools.message;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * An {@link IMessageBundle} that delegates to a list of {@link IMessageBundle} instances.
 * 
 */
public class CompositeMessageBundle extends CommonMessageBundle {

	private static final ILogger Log = LogTools.getLogger("NLS");

	private final List<IMessageBundle> bundles;

	public CompositeMessageBundle(CommonMessageBundleFactory factory, String name, List<IMessageBundle> bundles) {
		super(factory, name, null);
		this.bundles = bundles;
	}

	@Override
	public Set<String> getCodes() {
		HashSet<String> codes = new HashSet<>();
		bundles.forEach(bundle -> codes.addAll(bundle.getCodes()));
		return codes;
	}

	@Override
	public String getPattern(String code) {
		String result = bundles
				.stream()
				.map(bundle -> bundle.getPattern(code))
				.filter(Objects::nonNull)
				.findFirst().orElse(null);
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
			result = bundles
					.stream()
					.map(bundle -> {
						if (bundle.getPattern(code) != null) {
							return bundle.getString(code, args);
						}
						return null;
					})
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
			if (result == null) {
				result = getFallbackString(code, args);
			}
		}
		if (isLogMode()) {
			Log.info("ExtendedMessageBundle.getString,{},{},{}", getName(), code, result);
		}
		return result;
	}

}
