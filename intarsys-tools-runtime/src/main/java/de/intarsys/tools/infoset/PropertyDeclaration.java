package de.intarsys.tools.infoset;

public abstract class PropertyDeclaration {

	private String propertyName;

	protected PropertyDeclaration() {
	}

	protected PropertyDeclaration(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
