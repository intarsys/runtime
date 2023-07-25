package de.intarsys.tools.functor;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import de.intarsys.tools.collection.ByteArrayTools;

/**
 * This is a kind of "degenerated" signature as a trusted party is required to
 * sign and verify.
 * <p>
 * 
 */
public class TokenSignature extends Signature {

	private TokenPrivateKey privateKey;

	private TokenPublicKey publicKey;

	public TokenSignature() {
		super("Token");
	}

	@Override
	protected Object engineGetParameter(String param) throws InvalidParameterException {
		return null;
	}

	@Override
	protected AlgorithmParameters engineGetParameters() {
		return null;
	}

	@Override
	protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
		if (!(privateKey instanceof TokenPrivateKey)) {
			throw new InvalidKeyException();
		}
		this.privateKey = (TokenPrivateKey) privateKey;
	}

	@Override
	protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void engineSetParameter(String param, Object value) throws InvalidParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected byte[] engineSign() throws SignatureException {
		// this engine does not care about the content
		byte[] signature = ByteArrayTools.createRandomBytes(16);
		return signature;
	}

	@Override
	protected void engineUpdate(byte b) throws SignatureException {
	}

	@Override
	protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
	}

	@Override
	protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
		return false;
	}

	protected TokenPrivateKey getPrivateKey() {
		return privateKey;
	}

	protected TokenPublicKey getPublicKey() {
		return publicKey;
	}

	protected void setPrivateKey(TokenPrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	protected void setPublicKey(TokenPublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
