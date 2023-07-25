package de.intarsys.tools.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import de.intarsys.tools.stream.StreamTools;

/**
 * An {@link ICryptdec} doing nothing on its input.
 * 
 */
public class NullCryptdec implements ICryptdec {

	final String id;

	public NullCryptdec() {
		this("null");
	}

	public NullCryptdec(String id) {
		super();
		this.id = id;
	}

	@Override
	public byte[] decrypt(byte[] bytes) throws GeneralSecurityException {
		return bytes;
	}

	@Override
	public void decryptFinal(OutputStream os) throws GeneralSecurityException {
		// nothing to do
	}

	@Override
	public void decryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException {
		try {
			StreamTools.copy(is, os);
		} catch (IOException e) {
			throw new GeneralSecurityException(e);
		}
	}

	@Override
	public byte[] encrypt(byte[] bytes) throws GeneralSecurityException {
		return bytes;
	}

	@Override
	public void encryptFinal(OutputStream os) throws GeneralSecurityException {
		// nothing to do
	}

	@Override
	public void encryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException {
		try {
			StreamTools.copy(is, os);
		} catch (IOException e) {
			throw new GeneralSecurityException(e);
		}
	}

	@Override
	public String getId() {
		return id;
	}

}
