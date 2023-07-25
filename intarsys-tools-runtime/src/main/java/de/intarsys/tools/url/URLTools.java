package de.intarsys.tools.url;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.string.StringTools;

public class URLTools {

	public static Map<String, List<String>> getQueryParameters(URL url) {
		if (StringTools.isEmpty(url.getQuery())) {
			return Collections.emptyMap();
		}
		return Arrays
				.stream(url.getQuery().split("&"))
				.map(URLTools::splitQueryParameter)
				.collect(
						Collectors.groupingBy(
								SimpleImmutableEntry::getKey,
								LinkedHashMap::new,
								Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
	}

	public static SimpleImmutableEntry<String, String> splitQueryParameter(String param) {
		try {
			final int idx = param.indexOf("=");
			final String key = idx > 0 ? param.substring(0, idx) : param;
			final String value = idx > 0 && param.length() > idx + 1 ? param.substring(idx + 1) : null;
			return new SimpleImmutableEntry<>(
					URLDecoder.decode(key, "UTF-8"),
					value == null ? null : URLDecoder.decode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("required encoding");
		}
	}

	public static String withLeadingSeparator(String path) {
		return PathTools.withLeadingSeparator(path);
	}

	public static String withoutLeadingSeparator(String path) {
		return PathTools.withoutLeadingSeparator(path);
	}

	public static String withoutTrailingSeparator(String path) {
		return PathTools.withoutTrailingSeparator(path);
	}

	public static String withTrailingSeparator(String path) {
		return PathTools.withTrailingSeparator(path);
	}

	private URLTools() {
	}
}
