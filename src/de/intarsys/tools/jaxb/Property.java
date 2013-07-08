package de.intarsys.tools.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "property")
public class Property {

	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "value", required = true)
	private String value;

	public Property() {
	}

	public Property(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
