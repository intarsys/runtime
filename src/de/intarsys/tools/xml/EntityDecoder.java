package de.intarsys.tools.xml;

import java.io.IOException;
import java.io.Reader;

import de.intarsys.tools.reader.DirectTagReader;
import de.intarsys.tools.reader.IDirectTagHandler;
import de.intarsys.tools.reader.ILocationProvider;

public class EntityDecoder extends DirectTagReader {

	private static IDirectTagHandler ENTITY_HANDLER = new IDirectTagHandler() {
		public Object endTag(String tagContent, Object context)
				throws IOException {
			try {
				if (tagContent.startsWith("#")) {
					char c = (char) Integer.parseInt(tagContent.substring(1),
							10);
					return new String(new char[] { c });
				}
				if (tagContent.equals("amp")) {
					return "&";
				}
				if (tagContent.equals("lt")) {
					return "<";
				}
				if (tagContent.equals("gt")) {
					return ">";
				}
				if (tagContent.equals("apos")) {
					return "'";
				}
				if (tagContent.equals("auml")) {
					return "ä";
				}
				if (tagContent.equals("Auml")) {
					return "Ä";
				}
				if (tagContent.equals("ouml")) {
					return "ö";
				}
				if (tagContent.equals("Ouml")) {
					return "Ö";
				}
				if (tagContent.equals("uuml")) {
					return "ü";
				}
				if (tagContent.equals("Uuml")) {
					return "Ü";
				}
			} catch (Exception e) {
			}
			return tagContent;
		}

		public void setLocationProvider(ILocationProvider locationProvider) {
		}

		public void startTag() {
		}
	};
	private static String ENTITY_END = ";"; //$NON-NLS-1$
	private static String ENTITY_START = "&"; //$NON-NLS-1$

	public EntityDecoder(Reader reader) {
		super(reader, ENTITY_HANDLER, null);
		setStartTag(ENTITY_START);
		setEndTag(ENTITY_END);
	}

	public EntityDecoder(Reader reader, boolean escape) {
		super(reader, ENTITY_HANDLER, null, escape);
		setStartTag(ENTITY_START);
		setEndTag(ENTITY_END);
	}

}
