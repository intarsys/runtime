package de.intarsys.tools.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	public byte[] decrypt(byte[] bytes) throws IOException {
		return bytes;
	}

	@Override
	public void decryptFinal(OutputStream os) throws IOException {
		// nothing to do
	}

	@Override
	public void decryptUpdate(InputStream is, OutputStream os)
			throws IOException {
		StreamTools.copyStream(is, false, os, false);
	}

	@Override
	public byte[] encrypt(byte[] bytes) throws IOException {
		return bytes;
	}

	@Override
	public void encryptFinal(OutputStream os) throws IOException {
		// nothing to do
	}

	@Override
	public void encryptUpdate(InputStream is, OutputStream os)
			throws IOException {
		StreamTools.copyStream(is, false, os, false);
	}

	@Override
	public String getId() {
		return id;
	}

}
