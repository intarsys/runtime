package de.intarsys.tools.infoset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JavaConverter {

	public static Map<String, Object> convert(IElement element) {
		JavaConverter converter = new JavaConverter();
		return converter.convertAll(element);
	}

	protected Map<String, Object> convertAll(IElement element) {
		if (element == null) {
			return null; // NOSONAR
		}
		return convertObject(element);
	}

	protected Map<String, Object> convertObject(IElement element) {
		Map<String, Object> result = new HashMap<>();
		convertObjectDefaults(element, result);
		convertObjectAttributes(element, result);
		convertObjectElements(element, result);
		return result;
	}

	protected void convertObjectAttributes(IElement element, Map<String, Object> result) {
		Iterator<String> itAttr = element.attributeNames();
		while (itAttr.hasNext()) {
			String attrName = itAttr.next();
			result.put(attrName, element.attributeValue(attrName, null));
		}
	}

	protected void convertObjectDefaults(IElement element, Map<String, Object> result) {
		for (PropertyDeclaration pDecl : element.getDeclarations()) {
			if (pDecl instanceof ArrayDeclaration) {
				result.put(((ArrayDeclaration) pDecl).getPropertyName(), new ArrayList<>());
			}
		}
	}

	protected void convertObjectElements(IElement element, Map<String, Object> result) {
		Iterator<IElement> itEl = element.elementIterator();
		while (itEl.hasNext()) {
			IElement childElement = itEl.next();
			String childName = childElement.getName();
			String mapName = childName;
			ArrayDeclaration decl = lookupArrayDeclaration(element, childName);
			Object childObject = null;
			if (decl == null) {
				childObject = convert(childElement);
			} else {
				mapName = decl.getPropertyName();
				childObject = result.get(mapName);
				if (!(childObject instanceof List)) {
					childObject = new ArrayList<>();
				}
				((List<Object>) childObject).add(convert(childElement));
			}
			result.put(mapName, childObject);
		}
	}

	protected ArrayDeclaration lookupArrayDeclaration(IElement element, String elementName) {
		return element.getDeclarations() //
				.stream() //
				.filter((d) -> d instanceof ArrayDeclaration) //
				.map((d) -> (ArrayDeclaration) d)
				.filter((d) -> d.getElementName().equals(elementName))
				.findFirst() //
				.orElse(null);
	}

}
