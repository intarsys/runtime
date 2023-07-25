package de.intarsys.tools.url;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.intarsys.tools.exception.InternalError;
import de.intarsys.tools.string.StringTools;

/**
 * Tool for dealing with URL encoding.
 * 
 */
public class URLEncodingTools {

	public static final String CHARSET_DEFAULT = "UTF-8";

	public static String decode(String value, final String encoding) throws UnsupportedEncodingException {
		return URLDecoder.decode(value, encoding != null ? encoding : CHARSET_DEFAULT);
	}

	public static String decodeCharset(String value, Charset encoding) {
		try {
			return URLDecoder.decode(value, encoding.name());
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("required encoding");
		}
	}

	/**
	 * Decode a complete http entity content and return it as a {@link Map}.
	 * 
	 * @param content
	 * @param encoding
	 * @return A map of the key / value pairs in the entity.
	 * @throws IOException
	 */
	public static Map<String, String> decodeEntity(String content, String encoding) throws IOException {
		Map<String, String> parameters = new HashMap<>();
		if (StringTools.isEmpty(content)) {
			return parameters;
		}
		Scanner scanner = new Scanner(content);
		try {
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
				String parameter = scanner.next();
				int pos = parameter.indexOf('=');
				if (pos == -1) {
					throw new IllegalArgumentException("bad parameter");
				}

				final String name = decode(parameter.substring(0, pos), encoding);
				String value = "";
				if (parameter.length() > pos) {
					value = decode(parameter.substring(pos + 1), encoding);
				}
				parameters.put(name, value);
			}
			return parameters;
		} finally {
			scanner.close();
		}
	}

	public static String encode(String value, String encoding) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, encoding != null ? encoding : CHARSET_DEFAULT);
	}

	public static String encodeCharset(String value, Charset encoding) {
		try {
			return URLEncoder.encode(value, encoding != null ? encoding.name() : CHARSET_DEFAULT);
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("required encoding");
		}
	}

	/**
	 * Encode a {@link Map} of key / value pairs to a http entity.
	 * 
	 * @param parameters
	 * @param encoding
	 * @return The encoded entity
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeEntity(final Map<String, String> parameters, final String encoding)
			throws UnsupportedEncodingException {
		final StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> parameter : parameters.entrySet()) {
			final String encodedName = encode(parameter.getKey(), encoding);
			final String value = parameter.getValue();
			final String encodedValue = value != null ? encode(value, encoding) : "";
			if (result.length() > 0)
				result.append("&");
			result.append(encodedName);
			result.append("=");
			result.append(encodedValue);
		}
		return result.toString();
	}

	private URLEncodingTools() {
	}
}
