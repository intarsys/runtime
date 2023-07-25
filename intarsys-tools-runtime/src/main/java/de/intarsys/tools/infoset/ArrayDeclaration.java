package de.intarsys.tools.infoset;

public class ArrayDeclaration extends PropertyDeclaration {

	private String elementName;

	public ArrayDeclaration() {
	}

	public ArrayDeclaration(String propertyName, String elementName) {
		super(propertyName);
		this.elementName = elementName;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

}
