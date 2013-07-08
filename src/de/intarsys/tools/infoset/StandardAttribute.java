package de.intarsys.tools.infoset;


public class StandardAttribute implements IAttribute {

	final private StandardElement element;

	private String name;

	private String value;

	private String template;

	public StandardAttribute(StandardElement element) {
		super();
		this.element = element;
	}

	protected String condense(String pValue) {
		if (pValue == null) {
			return null;
		}
		return element.condense(pValue);
	}

	protected Object evaluate(String value) {
		if (value == null) {
			return value;
		}
		return element.evaluate(value);
	}

	@Override
	public Object getData() {
		if (value == null) {
			return evaluate(template);
		}
		return value;
	}

	public StandardElement getElement() {
		return element;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTemplate() {
		if (template == null) {
			template = condense(value);
		}
		return template;
	}

	@Override
	public String getValue() {
		if (value == null) {
			value = String.valueOf(evaluate(template));
		}
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTemplate(String template) {
		this.value = null;
		this.template = template;
	}

	public void setValue(String value) {
		this.value = value;
		this.template = null;
	}

	@Override
	public String toString() {
		return getName() + "=\"" + getTemplate() + "\"";
	}

}
