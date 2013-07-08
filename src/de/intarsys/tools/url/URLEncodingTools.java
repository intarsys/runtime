package de.intarsys.tools.url;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Tool for dealing with URL encoding.
 * 
 */
public class URLEncodingTools {

	public static final String CHARSET_DEFAULT = "ISO_8859_1";

	protected static String decode(String value, final String encoding)
			throws UnsupportedEncodingException {
		return URLDecoder.decode(value, encoding != null ? encoding
				: CHARSET_DEFAULT);
	}

	/**
	 * Decode a complete http entity content and return it as a {@link Map}.
	 * 
	 * @param content
	 * @param encoding
	 * @return A map of the key / value pairs in the entity.
	 * @throws IOException
	 */
	public static Map<String, String> decodeEntity(String content,
			String encoding) throws IOException {
		Map<String, String> parameters = new HashMap<String, String>();
		Scanner scanner = new Scanner(content);
		scanner.useDelimiter("&");
		while (scanner.hasNext()) {
			final String[] nameValue = scanner.next().split("=");
			if (nameValue.length == 0 || nameValue.length > 2)
				throw new IllegalArgumentException("bad parameter");

			final String name = decode(nameValue[0], encoding);
			String value = "";
			if (nameValue.length == 2) {
				value = decode(nameValue[1], encoding);
			}
			parameters.put(name, value);
		}
		return parameters;
	}

	protected static String encode(String value, String encoding)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(value, encoding != null ? encoding
				: CHARSET_DEFAULT);
	}

	/**
	 * Encode a {@link Map} of key / value pairs to a http entity.
	 * 
	 * @param parameters
	 * @param encoding
	 * @return The encoded entity
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeEntity(final Map<String, String> parameters,
			final String encoding) throws UnsupportedEncodingException {
		final StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> parameter : parameters.entrySet()) {
			final String encodedName = encode(parameter.getKey(), encoding);
			final String value = parameter.getValue();
			final String encodedValue = value != null ? encode(value, encoding)
					: "";
			if (result.length() > 0)
				result.append("&");
			result.append(encodedName);
			result.append("=");
			result.append(encodedValue);
		}
		return result.toString();
	}

}
