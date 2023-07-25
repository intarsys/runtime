package de.intarsys.tools.digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.locator.BasicLocatorFactory;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

public class DigestFunctor implements IFunctor<Object> {

	public static final String ARG_ENCODING = "encoding"; //$NON-NLS-1$

	public static final String ARG_FORMAT = "format"; //$NON-NLS-1$

	public static final String ARG_ALGORITHM = "algorithm"; //$NON-NLS-1$

	public static final String ARG_LOCATOR = "locator"; //$NON-NLS-1$

	public static final String VALUE_ENCODING_NONE = "NONE"; //$NON-NLS-1$
	public static final String VALUE_ENCODING_BASE64 = "BASE64"; //$NON-NLS-1$
	public static final String VALUE_ENCODING_HEX = "HEX"; //$NON-NLS-1$
	public static final String VALUE_FORMAT_RAW = "RAW"; //$NON-NLS-1$
	public static final String VALUE_FORMAT_DER = "DER"; //$NON-NLS-1$

	private static final ILogger Log = PACKAGE.Log;

	public static String getAlgorithmName(IArgs args) {
		return ArgTools.getString(args, ARG_ALGORITHM, "SHA256"); //$NON-NLS-1$
	}

	public static IDigester getDigester(IArgs args) {
		String algName = getAlgorithmName(args);
		try {
			return DigestTools.createDigester(algName);
		} catch (NoSuchAlgorithmException e) {
			Log.log(Level.SEVERE, "Digest algorithm '" + algName //$NON-NLS-1$
					+ "' not supported. Using SHA1.", e); //$NON-NLS-1$
			return DigestTools.createDigesterSHA1();
		}
	}

	public static String getEncoding(IArgs args) {
		return ArgTools.getString(args, ARG_ENCODING, VALUE_ENCODING_BASE64);
	}

	public static String getFormat(IArgs args) {
		return ArgTools.getString(args, ARG_FORMAT, VALUE_FORMAT_DER).toUpperCase();
	}

	public static ILocator getLocator(IArgs args) {
		return ArgTools.getLocator(args, ARG_LOCATOR, null, BasicLocatorFactory.get());
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		IArgs args = call.getArgs();
		ILocator locator = getLocator(args);
		if (locator == null) {
			throw new IllegalArgumentException("'locator' missing"); //$NON-NLS-1$
		}
		IDigester digester = getDigester(args);
		if (digester == null) {
			throw new IllegalArgumentException("'digester' missing"); //$NON-NLS-1$
		}
		InputStream is = null;
		try {
			is = locator.getInputStream();
			IDigest digest = DigestTools.digest(digester, is);
			Object result;
			byte[] bytes;
			String format = getFormat(args);
			if (VALUE_FORMAT_RAW.equals(format)) {
				bytes = digest.getBytes();
			} else if (VALUE_FORMAT_DER.equals(format)) {
				bytes = digest.getEncoded();
			} else {
				throw new IllegalArgumentException("'format' " + format //$NON-NLS-1$
						+ " not supported"); //$NON-NLS-1$
			}
			String encoding = getEncoding(args);
			if (VALUE_ENCODING_NONE.equals(encoding)) {
				result = bytes;
			} else if (VALUE_ENCODING_BASE64.equals(encoding)) {
				result = new String(Base64.encode(bytes));
			} else if (VALUE_ENCODING_HEX.equals(encoding)) {
				result = HexTools.bytesToHexString(bytes, 0, bytes.length, false);
			} else {
				throw new IllegalArgumentException("'encoding' " + encoding //$NON-NLS-1$
						+ " not supported"); //$NON-NLS-1$
			}
			return result;
		} catch (IOException e) {
			throw new FunctorExecutionException(e);
		} finally {
			StreamTools.close(is);
		}
	}
}
