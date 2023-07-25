package de.intarsys.tools.functor;

import java.security.PublicKey;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.encoding.Base64;

public class TokenPublicKey extends Object implements PublicKey {

	private final byte[] token;

	public TokenPublicKey() {
		this(ByteArrayTools.createRandomBytes(16));
	}

	public TokenPublicKey(byte[] token) {
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
