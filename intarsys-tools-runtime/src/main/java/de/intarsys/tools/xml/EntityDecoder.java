package de.intarsys.tools.xml;

import java.io.IOException;
import java.io.Reader;

import de.intarsys.tools.reader.DirectTagReader;
import de.intarsys.tools.reader.IDirectTagHandler;
import de.intarsys.tools.reader.ILocationProvider;

public class EntityDecoder extends DirectTagReader {

	private static final IDirectTagHandler ENTITY_HANDLER = new IDirectTagHandler() {
		@Override
		public Object endTag(String tagContent, Object context) throws IOException {
			try {
				if (tagContent.startsWith("#")) {
					char c = (char) Integer.parseInt(tagContent.substring(1), 10);
					return new String(new char[] { c });
				}
				if ("amp".equals(tagContent)) {
					return "&";
				}
				if ("lt".equals(tagContent)) {
					return "<";
				}
				if ("gt".equals(tagContent)) {
					return ">";
				}
				if ("apos".equals(tagContent)) {
					return "'";
				}
				if ("auml".equals(tagContent)) {
					return "\u00E4";
				}
				if ("Auml".equals(tagContent)) {
					return "\u00C4";
				}
				if ("ouml".equals(tagContent)) {
					return "\u00F6";
				}
				if ("Ouml".equals(tagContent)) {
					return "\u00D6";
				}
				if ("uuml".equals(tagContent)) {
					return "\u00FC";
				}
				if ("Uuml".equals(tagContent)) {
					return "\u00DC";
				}
			} catch (Exception e) {
				//
			}
			return tagContent;
		}

		@Override
		public void setLocationProvider(ILocationProvider locationProvider) {
			// not required
		}

		@Override
		public void startTag() {
			// not required
		}
	};
	private static final String ENTITY_END = ";"; //$NON-NLS-1$
	private static final String ENTITY_START = "&"; //$NON-NLS-1$

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
