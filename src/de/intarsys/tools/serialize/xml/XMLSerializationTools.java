package de.intarsys.tools.serialize.xml;

import java.io.IOException;
import java.io.OutputStream;

import de.intarsys.tools.locator.ByteArrayLocator;
import de.intarsys.tools.serialize.ISerializationFactory;
import de.intarsys.tools.serialize.ISerializer;
import de.intarsys.tools.stream.StreamTools;

public class XMLSerializationTools {

	static public byte[] serialize(ISerializationFactory factory,
			Object object, boolean createDocument) throws IOException {
		ByteArrayLocator locator = new ByteArrayLocator(null, "object", "xml");
		OutputStream os = null;
		try {
			os = locator.getOutputStream();
			XMLSerializationContext context = new XMLSerializationContext(os,
					createDocument);
			ISerializer serializer = factory.createSerializer(context);
			serializer.serialize(object);
			return locator.getBytes();
		} finally {
			StreamTools.close(os);
		}
	}
}
