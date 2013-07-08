package de.intarsys.tools.functor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.intarsys.tools.collection.ListTools;

/**
 * Shared methods between ArgsSigner and ArgsValidator.
 * 
 */
@SuppressWarnings("nls")
public abstract class ArgsCryptoBase {

	public static final String ARG_VERSION = "version";
	public static final String ARG_TYPE = "type";
	public static final String ARG_VALUE = "value";
	public static final String ARG_SIGNER = "signer";
	public static final String ARG_SIGNED = "signed";
	public static final String ARG_CONTENT = "content";
	public static final String ARG_CERTIFICATES = "certificates";
	public static final String ARG_HASH = "hash";
	public static final String ARG_SELECT = "select";
	public static final String ARG_ALGORITHM = "algorithm";

	public static final String VERSION_1_0 = "1.0";
	public static final String TYPE_ARGDSIG = "ArgDSig";

	public static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";
	public static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA";
	public static final String DEFAULT_CERTIFICATE_TYPE = "X.509";
	public static final List<String> DEFAULT_SELECT = ListTools.with("*",
			"!signature");

	public ArgsCryptoBase() {
		super();
	}

	/**
	 * Create the {@link IArgs} structure containing the signed part of the
	 * overall args, filtered using the path tokens from fields.
	 * 
	 * @param args
	 * @param fields
	 * @return
	 */
	protected IArgs createSignedContentArgs(IArgs args, List<String> fields) {
		IArgs signedContent = Args.create();
		for (String field : fields) {
			updateSubtree(signedContent, args, field);
		}
		return signedContent;
	}

	/**
	 * Create a hash with the specified algorithm for all args.
	 * 
	 * @param hashAlgorithm
	 * @param args
	 * @return
	 * @throws GeneralSecurityException
	 */
	protected byte[] hash(String hashAlgorithm, IArgs args)
			throws GeneralSecurityException {
		final MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
		OutputStream os = new OutputStream() {
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				digest.update(b, off, len);
			}

			@Override
			public void write(int b) throws IOException {
				digest.update((byte) b);
			}
		};
		try {
			writeArgs(os, args);
		} catch (IOException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			if (cause instanceof GeneralSecurityException) {
				throw (GeneralSecurityException) cause;
			}
			throw new GeneralSecurityException(cause);
		}
		return digest.digest();
	}

	/**
	 * Update the {@link Signature} with the args content.
	 * 
	 * @param signature
	 * @param args
	 * @throws GeneralSecurityException
	 */
	protected void updateSignature(final Signature signature, IArgs args)
			throws GeneralSecurityException {
		OutputStream os = new OutputStream() {
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				try {
					signature.update(b, off, len);
				} catch (SignatureException e) {
					throw new IOException(e);
				}
			}

			@Override
			public void write(int b) throws IOException {
				try {
					signature.update((byte) b);
				} catch (SignatureException e) {
					throw new IOException(e);
				}
			}
		};
		try {
			writeArgs(os, args);
		} catch (Exception e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			if (cause instanceof GeneralSecurityException) {
				throw (GeneralSecurityException) cause;
			}
			throw new GeneralSecurityException(cause);
		}
	}

	/**
	 * Update the {@link IArgs} subtree of args, according to the field
	 * specification.
	 * 
	 * @param subtree
	 * @param args
	 * @param field
	 */
	protected void updateSubtree(IArgs subtree, IArgs args, String field) {
		if ("*".equals(field)) {
			ArgTools.putAll(subtree, args);
		} else if (field.startsWith("!")) {
			field = field.substring(1);
			ArgTools.undefinePath(subtree, field);
		} else {
			Object value = ArgTools.getObject(args, field, null);
			ArgTools.putPath(subtree, field, value);
		}
	}

	protected void writeArg(OutputStream os, String key, Object value)
			throws IOException, UnsupportedEncodingException {
		os.write(key.getBytes("UTF-8"));
		os.write("=".getBytes("UTF-8"));
		if (value instanceof IArgs) {
			os.write("{\n".getBytes("UTF-8"));
			writeArgs(os, (IArgs) value);
			os.write("\n}".getBytes("UTF-8"));
		} else {
			String string = String.valueOf(value);
			os.write(string.getBytes("UTF-8"));
		}
		os.write("\n".getBytes("UTF-8"));
	}

	/**
	 * Write a canonical form of args to the output stream.
	 * 
	 * @param os
	 * @param args
	 * @throws IOException
	 */
	protected void writeArgs(OutputStream os, IArgs args) throws IOException {
		List<String> names = new ArrayList<String>(args.names());
		if (names.isEmpty()) {
			for (int i = 0; i < args.size(); i++) {
				String key = "" + i;
				Object value = args.get(i);
				writeArg(os, key, value);
			}
		} else {
			Collections.sort(names);
			for (String name : names) {
				if (args.isDefined(name)) {
					String key = name;
					Object value = args.get(name);
					writeArg(os, key, value);
				}
			}
		}
	}

}