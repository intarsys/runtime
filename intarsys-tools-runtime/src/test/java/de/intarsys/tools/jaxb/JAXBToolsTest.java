package de.intarsys.tools.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.xml.sax.SAXParseException;

public class JAXBToolsTest {
	@Test
	public void unmarshalOk() throws IOException {
		Person person = unmarshalPerson("""
				<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
				<person>
				    <givenName>Johann</givenName>
				    <surname>Strauss</surname>
				</person>
				""");
		System.out.println(person);
	}

	@Test
	public void unmarshalProhibitsExternalEntities() {
		IOException exception = assertThrows(IOException.class, () -> unmarshalPerson("""
				<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
				<!DOCTYPE person [
				    <!ENTITY externalEntity SYSTEM "file:///etc/passwd">
				]>
				<person>
				    <givenName>Johann</givenName>
				    <surname>&externalEntity;</surname>
				</person>
				"""));

		SAXParseException cause = unwrapException(exception, SAXParseException.class);
		assertNotNull("expected SAXParseException as cause", cause);
		assertEquals("expected error when external entity is dereferenced", 7, cause.getLineNumber());
	}

	@Test
	public void unmarshalProhibitsExternalParameterEntities() {
		IOException exception = assertThrows(IOException.class, () -> unmarshalPerson("""
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<!DOCTYPE example [
			    <!ENTITY % externalParameterEntity SYSTEM "file:///etc/passwd">
			    %externalParameterEntity;
			]>
			<person>
			    <givenName>Johann</givenName>
			    <surname>Strauss</surname>
			</person>
			"""));

		SAXParseException cause = unwrapException(exception, SAXParseException.class);
		assertNotNull("expected SAXParseException as cause", cause);
		assertEquals("expected error when external entity is dereferenced", 4, cause.getLineNumber());
	}

	@Test
	public void unmarshalProhibitsExternalDtd() {
		IOException exception = assertThrows(IOException.class, () -> unmarshalPerson("""
				<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
				<!DOCTYPE example SYSTEM "file:///etc/passwd">
				<person>
				    <givenName>Johann</givenName>
				    <surname>Strauss</surname>
				</person>
				"""));

		SAXParseException cause = unwrapException(exception, SAXParseException.class);
		assertNotNull("expected SAXParseException as cause", cause);
		assertEquals("expected error when external DTD is defined", 2, cause.getLineNumber());
	}

	private Person unmarshalPerson(String xml) throws IOException {
		try (InputStream input = newInputStream(xml)) {
			return JAXBTools.unmarshal(input, Person.class);
		}
	}

	private InputStream newInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	private <E extends Throwable> E unwrapException(Throwable throwable, Class<E> exceptionType) {
		while (throwable != null) {
			if (exceptionType.isInstance(throwable)) {
				return exceptionType.cast(throwable);
			}

			throwable = throwable.getCause();
		}

		return null;
	}
}
