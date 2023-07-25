package de.intarsys.tools.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.intarsys.tools.locator.ILocator;

public class JAXBTools {
	private static final Object lock = new Object();
	private static final Map<Object, JAXBContext> contextsByPackageNames = new ConcurrentHashMap<>();

	public static JAXBContext getContext(Class<?> clazz) throws JAXBException {
		String packageName = clazz.getPackage().getName();
		synchronized (lock) {
			//NOSONAR We cannot throw a checked exception from the mapping function for computeIfAbsent.
			JAXBContext context = contextsByPackageNames.get(packageName);
			if (context == null) {
				context = JAXBContext.newInstance(packageName, clazz.getClassLoader());
				contextsByPackageNames.put(packageName, context);
			}

			return context;
		}
	}

	public static Unmarshaller getUnmarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext context = getContext(clazz);
		return context.createUnmarshaller();
	}

	public static <T> T unmarshal(ILocator locator, Class<T> type) throws IOException {
		try (InputStream input = locator.getInputStream()) {
			return unmarshal(input, type);
		}
	}

	public static <T> T unmarshal(InputStream input, Class<T> type) throws IOException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller(type);
			Object object = unmarshaller.unmarshal(input);
			return type.cast(object);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static <T> T unmarshalElement(ILocator locator, Class<T> declaredType) throws IOException {
		try (InputStream is = locator.getInputStream()) {
			return unmarshalElement(is, declaredType);
		}
	}

	public static <T> T unmarshalElement(InputStream input, Class<T> declaredType) throws IOException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller(declaredType);
			JAXBElement<?> element = (JAXBElement<?>) unmarshaller.unmarshal(input);
			Object value = element.getValue();
			return declaredType.cast(value);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private JAXBTools() {
		super();
	}
}
