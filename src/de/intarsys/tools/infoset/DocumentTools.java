package de.intarsys.tools.infoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.stream.StreamTools;

/**
 * Some tools for handling infoset abstractions.
 * 
 */
public class DocumentTools {

	public static boolean equals(IDocument a, IDocument b) {
		if (a == null) {
			return b == null;
		}
		if (b == null) {
			return false;
		}
		IElement aRoot = a.getRootElement();
		IElement bRoot = b.getRootElement();
		return equals(aRoot, bRoot);
	}

	public static boolean equals(IElement a, IElement b) {
		if (a == null) {
			return b == null;
		}
		if (b == null) {
			return false;
		}
		Iterator<String> itNames;
		itNames = a.attributeNames();
		Set<String> visited = new HashSet<>();
		while (itNames.hasNext()) {
			String name = itNames.next();
			if (!a.attributeValue(name, null).equals(
					b.attributeValue(name, null))) {
				return false;
			}
			visited.add(name);
		}
		itNames = b.attributeNames();
		while (itNames.hasNext()) {
			String name = itNames.next();
			if (!visited.contains(name)) {
				if (!a.attributeValue(name, null).equals(
						b.attributeValue(name, null))) {
					return false;
				}
			}
		}
		Iterator<IElement> itElementA;
		Iterator<IElement> itElementB;
		itElementA = a.elementIterator();
		itElementB = b.elementIterator();
		while (itElementA.hasNext() && itElementB.hasNext()) {
			IElement childA = itElementA.next();
			IElement childB = itElementB.next();
			if (!equals(childA, childB)) {
				return false;
			}
		}
		if (itElementA.hasNext()) {
			return false;
		}
		if (itElementB.hasNext()) {
			return false;
		}
		return true;
	}

	public static IDocument parse(ILocator locator) throws IOException {
		InputStream is = null;
		try {
			is = locator.getInputStream();
			return ElementFactory.get().parse(is);
		} finally {
			StreamTools.close(is);
		}
	}

	public static void save(IDocument doc, ILocator locator) throws IOException {
		Writer w = null;
		try {
			w = locator.getWriter();
			save(doc, w);
		} finally {
			StreamTools.close(w);
		}
	}

	public static void save(IDocument doc, Writer w) throws IOException {
		XMLWriter xmlw = new XMLWriter(w, OutputFormat.createPrettyPrint());
		xmlw.write(doc);
	}
}