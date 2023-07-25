package de.intarsys.tools.functor;

import java.security.PrivateKey;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.encoding.Base64;

public class TokenPrivateKey extends Object implements PrivateKey {

	private final byte[] token;

	public TokenPrivateKey() {
		this(ByteArrayTools.createRandomBytes(16));
	}

	public TokenPrivateKey(byte[] token) {
		super();
		this.token = token;
	}

	@Override
	public String getAlgorithm() {
		return "Token";
	}

	@Override
	public byte[] getEncoded() {
		return token;
	}

	@Override
	public String getFormat() {
		return "Token";
	}

	public String getToken() {
		return new String(Base64.encode(token));
	}

}
