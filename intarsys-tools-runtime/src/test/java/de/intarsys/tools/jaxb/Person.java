package de.intarsys.tools.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement()
@XmlType(name = "")
public class Person {
	private String givenName;
	private String surname;

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Override
	public String toString() {
		return String.format("%s(givenName=%s, surname=%s)", getClass().getSimpleName(), givenName, surname);
	}
}