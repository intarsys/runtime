/*
 * Copyright (c) 2012, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.provider.Providers;
import de.intarsys.tools.string.StringTools;

/**
 * Tools for dealing with digests.
 * <p>
 * This class uses the interface {@link IDigestCodec} to decouple the runtime
 * from the dependency to ASN.1 libraries.
 * 
 */
public class DigestTools {

	static public interface IDigestCodec {

		public IDigest decode(byte[] bytes) throws IOException;

		public byte[] encode(IDigest digest) throws IOException;
	}

	static private IDigestCodec codec = createCodec();

	static protected IDigestCodec createCodec() {
		Iterator<IDigestCodec> it = Providers.get().lookupProviders(
				IDigestCodec.class);
		while (it.hasNext()) {
			try {
				return it.next();
			} catch (Throwable t) {
				Logger.getAnonymousLogger().log(Level.WARNING,
						"error creating codec", t);
			}
		}
		return null;
	}

	static public IDigest createDigest(byte[] bytes) throws IOException {
		return decode(bytes);
	}

	public static IDigest createDigest(Object value) throws IOException {
		if (value == null) {
			return null;
		}
		if (value instanceof IDigest) {
			return (IDigest) value;
		}
		if (value instanceof String) {
			if (StringTools.isEmpty((String) value)) {
				return null;
			}
			value = Base64.decode((String) value);
		}
		if (value instanceof ILocator) {
			value = LocatorTools.getBytes((ILocator) value);
		}
		if (value instanceof ILocatorSupport) {
			value = LocatorTools.getBytes(((ILocatorSupport) value)
					.getLocator());
		}
		if (value instanceof byte[]) {
			return DigestTools.decode((byte[]) value);
		}
		if (value instanceof IArgs) {
			IArgs tempArgs = (IArgs) value;
			byte[] bytes = ArgTools.getByteArray(tempArgs, "der", null);
			if (bytes == null) {
				bytes = ArgTools.getByteArray(tempArgs, "raw", null);
				String hashAlgorithm = ArgTools.getString(tempArgs,
						"algorithm", "SHA256");
				if (bytes != null) {
					return DigestTools.createDigest(hashAlgorithm, bytes);
				}
			} else {
				return DigestTools.decode(bytes);
			}
		}
		try {
			return ConverterRegistry.get().convert(value, IDigest.class);
		} catch (ConversionException e) {
			throw new IOException("can't convert " + value + " to digest");
		}
	}

	static public IDigest createDigest(String algorithmName, byte[] bytes) {
		return new Digest(algorithmName, bytes);
	}

	static public IDigester createDigester(String algorithmName)
			throws NoSuchAlgorithmException {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithmName,
					"BC"); //$NON-NLS-1$
			return new Digester(algorithmName, digest);
		} catch (NoSuchProviderException e) {
			MessageDigest digest = MessageDigest.getInstance(algorithmName);
			return new Digester(algorithmName, digest);
		}
	}

	public static IDigester createDigesterSHA1() {
		try {
			return DigestTools.createDigester("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA1 digest not supported");
		}
	}

	static public IDigest decode(byte[] bytes) throws IOException {
		if (getCodec() == null) {
			throw new IOException("no digest codec installed");
		}
		return getCodec().decode(bytes);
	}

	public static IDigest digest(IDigester digester, InputStream is)
			throws IOException {
		digester.digestUpdate(is);
		return digester.digestFinal();
	}

	static protected byte[] encode(IDigest digest) throws IOException {
		if (getCodec() == null) {
			throw new IOException("no digest codec installed");
		}
		return getCodec().encode(digest);
	}

	static protected IDigestCodec getCodec() {
		return codec;
	}

}
