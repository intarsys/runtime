package de.intarsys.tools.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "properties")
public class Properties {

	@XmlElement(name = "property")
	private Property[] properties;

	public Properties() {
	}

	public Property[] getProperties() {
		return properties;
	}

	public void setProperties(Property[] properties) {
		this.properties = properties;
	}

}
