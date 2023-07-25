package de.intarsys.tools.infoset;

/**
 * A simple fluent API for building IElement structures.
 *
 */
public class DocumentBuilder {

	private final DocumentBuilder parent;

	private final IDocument document;

	private final IElement current;

	public DocumentBuilder() {
		this.parent = null;
		this.document = ElementFactory.get().createDocument();
		this.current = ElementFactory.get().createElement("root");
		this.document.setRootElement(current);
	}

	protected DocumentBuilder(DocumentBuilder parent, IElement current) {
		super();
		this.parent = parent;
		this.document = parent.getDocument();
		this.current = current;
	}

	public DocumentBuilder(IDocument document) {
		this.parent = null;
		this.document = document;
		this.current = document.getRootElement();
	}

	public DocumentBuilder(IDocument document, IElement element) {
		this.parent = null;
		this.document = document;
		this.current = element;
	}

	public DocumentBuilder element(String name) {
		IElement child = current.element(name);
		if (child == null) {
			child = current.newElement(name);
		}
		return new DocumentBuilder(this, child);
	}

	public DocumentBuilder end() {
		return parent;
	}

	public IElement getCurrentElement() {
		return current;
	}

	public IDocument getDocument() {
		return document;
	}

	public IElement getRootElement() {
		return document.getRootElement();
	}

	public DocumentBuilder setAttributeTemplate(String name, String template) {
		current.setAttributeTemplate(name, template);
		return this;
	}

	public DocumentBuilder setAttributeValue(String name, String value) {
		current.setAttributeValue(name, value);
		return this;
	}

	public DocumentBuilder setName(String name) {
		current.setName(name);
		return this;
	}

	public DocumentBuilder setText(String value) {
		current.setText(value);
		return this;
	}

}
