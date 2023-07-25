package de.intarsys.tools.xml;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

public class TransformerTools {
	// Xalan-specific, will not work with other implementations ({http://xml.apache.org/xalan}indent-amount
	// still works for backward compatibility).
	private static final String INDENT_AMOUNT = "{http://xml.apache.org/xlst}indent-amount";

	/**
	 * Creates a transformer factory that does not resolve external DTDs and stylesheets to prevent XML external entity
	 * injections (XXE).
	 *
	 * @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#transformerfactory">
	 *      OWASP XML External Entity Prevention Cheat Sheet</a>
	 */
	public static TransformerFactory createSecureTransformerFactory() {
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

		return factory;
	}

	public static void setIndentAmount(Transformer transformer, int amount) {
		try {
			transformer.setOutputProperty(INDENT_AMOUNT, Integer.toString(amount));
		} catch (IllegalArgumentException exception) {
			// ignore and continue, we tried our best
		}
	}

	private TransformerTools() {
		super();
	}
}
