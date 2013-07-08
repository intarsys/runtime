package de.intarsys.tools.infoset;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.collection.EmptyIterator;
import de.intarsys.tools.collection.FilterIterator;
import de.intarsys.tools.reader.DirectTagReader;
import de.intarsys.tools.stream.StreamTools;

/**
 * A standard implementation for {@link IElement}.
 * 
 */
public class StandardElement implements IElement {

	private StandardElement parent;

	private StandardDocument document;

	private String text = "";

	private List<IElement> elements;

	private Map<String, String> attributes;

	private String name;

	public StandardElement(StandardDocument document, StandardElement parent,
			String name) {
		super();
		this.document = document;
		this.parent = parent;
		this.name = name;
	}

	@Override
	public String asXML() {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(writer,
					OutputFormat.createPrettyPrint());
			xmlWriter.write(this);
			return writer.toString();
		} catch (Exception e) {
			return "<error>";
		} finally {
			StreamTools.close(writer);
		}
	}

	@Override
	public IAttribute attribute(String name) {
		if (attributes == null) {
			return null;
		}
		String template = attributes.get(name);
		if (template == null) {
			return null;
		}
		StandardAttribute attr = new StandardAttribute(this);
		attr.setName(name);
		attr.setTemplate(template);
		return attr;
	}

	@Override
	public Iterator<String> attributeNames() {
		if (attributes == null) {
			return EmptyIterator.UNIQUE;
		}
		return attributes.keySet().iterator();
	}

	@Override
	public String attributeTemplate(String name) {
		IAttribute attr = attribute(name);
		if (attr == null) {
			return null;
		}
		return attr.getTemplate();
	}

	@Override
	public String attributeValue(String name, String defaultValue) {
		IAttribute attr = attribute(name);
		if (attr == null) {
			return defaultValue;
		}
		return attr.getValue();
	}

	protected String condense(String pValue) {
		if (pValue == null) {
			return null;
		}
		return DirectTagReader.escape(pValue);
	}

	@Override
	public IElement element(String name) {
		if (elements == null) {
			return null;
		}
		for (IElement element : elements) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	@Override
	public Iterator<IElement> elementIterator() {
		if (elements == null) {
			return EmptyIterator.UNIQUE;
		}
		return elements.iterator();
	}

	@Override
	public Iterator<IElement> elementIterator(final String name) {
		if (elements == null) {
			return EmptyIterator.UNIQUE;
		}
		return new FilterIterator<IElement>(elements.iterator()) {
			@Override
			protected boolean accept(IElement object) {
				return object.getName().equals(name);
			}
		};
	}

	@Override
	public void elementRemove(IElement pElement) {
		if (elements == null) {
			return;
		}
		elements.remove(pElement);
	}

	@Override
	public void elementsClear() {
		if (elements == null) {
			return;
		}
		elements.clear();
	}

	@Override
	public String elementText(String name) {
		IElement element = element(name);
		if (element == null) {
			return null;
		}
		return element.getText();
	}

	protected Object evaluate(String value) {
		if (value == null) {
			return value;
		}
		if (value.indexOf('$') >= 0) {
			if (parent != null) {
				return parent.evaluate(value);
			}
			if (document != null) {
				return document.evaluate(value);
			}
		}
		return value;
	}

	public StandardDocument getDocument() {
		return document;
	}

	@Override
	public String getName() {
		return name;
	}

	public StandardElement getParent() {
		return parent;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean hasAttribute(String name) {
		return attribute(name) != null;
	}

	@Override
	public boolean hasAttributes() {
		return attributes != null && attributes.size() > 0;
	}

	@Override
	public boolean hasElements() {
		return elements != null && elements.size() > 0;
	}

	@Override
	public boolean hasElements(String name) {
		return element(name) != null;
	}

	@Override
	public IElement newElement(String name) {
		StandardElement result = new StandardElement(document, this, name);
		if (elements == null) {
			elements = new ArrayList<>();
		}
		elements.add(result);
		return result;
	}

	public void setAttributeTemplate(String name, String template) {
		if (template == null) {
			if (attributes == null) {
				return;
			}
			attributes.remove(name);
		} else {
			if (attributes == null) {
				attributes = new HashMap<String, String>();
			}
			attributes.put(name, template);
		}
	}

	@Override
	public void setAttributeValue(String name, String value) {
		if (value == null) {
			if (attributes == null) {
				return;
			}
			attributes.remove(name);
		} else {
			if (attributes == null) {
				attributes = new HashMap<String, String>();
			}
			attributes.put(name, condense(value));
		}
	}

	public void setDocument(StandardDocument document) {
		this.document = document;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setParent(StandardElement parent) {
		this.parent = parent;
	}

	@Override
	public void setText(String value) {
		this.text = value;
	}

	@Override
	public String toString() {
		return asXML();
	}

}
