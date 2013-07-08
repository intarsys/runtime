package de.intarsys.tools.serialize;

import de.intarsys.tools.mime.IMimeTypeSupport;

/**
 * The {@link ISerializationFactory} for plain string serializing and
 * deserializing.
 * 
 */
public class StringSerializationFactory implements ISerializationFactory,
		IMimeTypeSupport {

	public static final String MIMETYPE = "text/plain";

	private String charset = "UTF-8";

	public StringSerializationFactory() {
	}

	public StringSerializationFactory(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public IDeserializer createDeserializer(SerializationContext context) {
		return new StringDeserializer(
				((StreamSerializationContext) context).getInputStream(),
				getCharset());
	}

	@Override
	public ISerializer createSerializer(SerializationContext context) {
		return new StringSerializer(
				((StreamSerializationContext) context).getOutputStream(),
				getCharset());
	}

	public String getCharset() {
		return charset;
	}

	@Override
	public String getContentType() {
		return MIMETYPE;
	}

	@Override
	public Class getSerializationType() {
		return Object.class;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
