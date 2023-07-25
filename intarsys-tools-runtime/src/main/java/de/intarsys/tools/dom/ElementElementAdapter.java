package de.intarsys.tools.dom;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.intarsys.tools.collection.ConversionIterator;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.infoset.AbstractElement;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IAttribute;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reader.DirectTagReader;
import de.intarsys.tools.xml.TransformerTools;

/**
 * Adapt a W3C DOM to {@link IElement}
 *
 */
public class ElementElementAdapter extends AbstractElement implements IStringEvaluatorAccess {

	private final Element element;

	private IStringEvaluator stringEvaluator;

	private IStringEvaluator templateEvaluator;

	public ElementElementAdapter(Element element) {
		super();
		this.element = element;
	}

	public ElementElementAdapter(Element element, IStringEvaluator evaluator) {
		super();
		this.element = element;
		setStringEvaluator(evaluator);
	}

	@Override
	public String asXML() {
		try (StringWriter output = new StringWriter()) {
			TransformerTools
					.createSecureTransformerFactory()
					.newTransformer()
					.transform(new DOMSource(element), new StreamResult(output));
			return output.toString();
		} catch (TransformerException | IOException e) {
			return "<error>";
		}
	}

	@Override
	public IAttribute attribute(String name) {
		Attr attr = element.getAttributeNode(name);
		if (attr == null) {
			return null;
		}
		return new AttributeAttributeAdapter(attr, stringEvaluator);
	}

	@Override
	public Object attributeData(String name, Object defaultValue) {
		Attr attr = element.getAttributeNode(name);
		Object value;
		if (attr == null) {
			value = defaultValue;
		} else {
			value = evaluate(attr.getValue());
		}
		return value == null ? defaultValue : value;
	}

	@Override
	public Iterator<String> attributeNames() {
		return new Iterator<>() {
			NamedNodeMap map = element.getAttributes();
			int index;

			@Override
			public boolean hasNext() {
				return index < map.getLength();
			}

			@Override
			public String next() {
				if (hasNext()) {
					return ((Attr) map.item(index++)).getName();
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String attributeTemplate(String name) {
		Attr attr = element.getAttributeNode(name);
		if (attr == null) {
			return null;
		} else {
			return attr.getValue();
		}
	}

	@Override
	public String attributeValue(String name, String defaultValue) {
		Attr attr = element.getAttributeNode(name);
		Object value;
		if (attr == null) {
			value = defaultValue;
		} else {
			value = evaluate(attr.getValue());
		}
		return value == null ? defaultValue : toString(value);
	}

	protected String condense(String pValue) {
		if (pValue == null) {
			return null;
		}
		return DirectTagReader.escape(pValue);
	}

	@Override
	public IElement element(String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}
		return new ElementElementAdapter((Element) list.item(0), stringEvaluator);
	}

	@Override
	public Iterator<IElement> elementIterator() {
		Iterator<Element> it = new Iterator<>() {
			NodeList list = element.getElementsByTagName("*");
			int index;

			@Override
			public boolean hasNext() {
				return index < list.getLength();
			}

			@Override
			public Element next() {
				if (hasNext()) {
					return (Element) list.item(index++);
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return new ConversionIterator<>(it) {
			@Override
			protected IElement createTargetObject(Element element) {
				return new ElementElementAdapter(element, stringEvaluator);
			}
		};
	}

	@Override
	public Iterator<IElement> elementIterator(final String name) {
		Iterator<Element> it = new Iterator<>() {
			NodeList list = element.getElementsByTagName(name);
			int index;

			@Override
			public boolean hasNext() {
				return index < list.getLength();
			}

			@Override
			public Element next() {
				if (hasNext()) {
					return (Element) list.item(index++);
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return new ConversionIterator<>(it) {
			@Override
			protected IElement createTargetObject(Element element) {
				return new ElementElementAdapter(element, stringEvaluator);
			}
		};
	}

	@Override
	public void elementRemove(IElement pElement) {
		element.removeChild((((ElementElementAdapter) pElement).getElement()));
	}

	@Override
	public void elementsClear() {
		NodeList list = element.getElementsByTagName("*");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			element.removeChild(node);
		}
	}

	@Override
	public String elementText(String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}
		Object value = evaluate(((Element) list.item(0)).getTextContent());
		return value == null ? null : toString(value);
	}

	protected Object evaluate(String value) {
		return StringEvaluatorTools.evaluateString(templateEvaluator, value);
	}

	public Element getElement() {
		return element;
	}

	@Override
	public String getName() {
		return element.getNodeName();
	}

	@Override
	public IStringEvaluator getStringEvaluator() {
		return stringEvaluator;
	}

	@Override
	public String getText() {
		Object value = evaluate(element.getTextContent());
		return value == null ? null : toString(value);
	}

	@Override
	public boolean hasAttribute(String name) {
		return element.hasAttribute(name);
	}

	@Override
	public boolean hasAttributes() {
		return element.hasAttributes();
	}

	@Override
	public boolean hasElements() {
		return element.getElementsByTagName("*").getLength() > 0;
	}

	@Override
	public boolean hasElements(String name) {
		return element.getElementsByTagName(name).getLength() > 0;
	}

	protected Element myElement() {
		return element;
	}

	@Override
	public IElement newElement(String name) {
		Element tempElement = element.getOwnerDocument().createElement(name);
		element.appendChild(tempElement);
		return new ElementElementAdapter(tempElement, stringEvaluator);
	}

	@Override
	public void setAttributeTemplate(String name, String template) {
		if (template == null) {
			element.removeAttribute(name);
		} else {
			element.setAttribute(name, template);
		}
	}

	@Override
	public void setAttributeValue(String name, String value) {
		if (value == null) {
			element.removeAttribute(name);
		} else {
			element.setAttribute(name, condense(value));
		}
	}

	@Override
	public void setName(String name) {
		element.getOwnerDocument().renameNode(element, null, name);
	}

	@Override
	public void setStringEvaluator(IStringEvaluator evaluator) {
		this.stringEvaluator = evaluator;
		if (stringEvaluator == null) {
			this.templateEvaluator = null;
		} else {
			this.templateEvaluator = TaggedStringEvaluator.decorate(evaluator);
		}
	}

	@Override
	public void setText(String value) {
		element.setTextContent(value);
	}

	@Override
	public String toString() {
		return asXML();
	}

	protected String toString(Object value) {
		return ElementTools.toString(value);
	}

}
