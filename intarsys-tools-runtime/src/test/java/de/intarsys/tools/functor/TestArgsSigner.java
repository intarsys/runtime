package de.intarsys.tools.functor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;

import de.intarsys.tools.collection.ListTools;
import junit.framework.TestCase;

@SuppressWarnings({ "nls", "MagicNumber", "MultipleStringLiterals" })
public class TestArgsSigner extends TestCase {

	private KeyStore jks;
	private String alias;

	protected KeyPair createKeyPair(String algorithm) throws NoSuchAlgorithmException {
		if ("Token".equals(algorithm)) {
			PublicKey publicKey = new TokenPublicKey();
			PrivateKey privateKey = new TokenPrivateKey(publicKey.getEncoded());
			return new KeyPair(publicKey, privateKey);
		} else {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
			kpg.initialize(1024);
			return kpg.genKeyPair();
		}
	}

	protected Signature createSignature(String algorithm) throws NoSuchAlgorithmException {
		if ("Token".equals(algorithm)) {
			return new TokenSignature();
		} else {
			return Signature.getInstance(algorithm);
		}
	}

	private List<Certificate> getCertificates() throws GeneralSecurityException {
		return Collections.singletonList(jks.getCertificate(alias));
	}

	private PrivateKey getPrivateKey() throws GeneralSecurityException {
		return (PrivateKey) jks.getKey(alias, new char[0]);
	}

	private void loadKeyStore() throws GeneralSecurityException, IOException {
		jks = KeyStore.getInstance("JKS");
		jks.load(getClass().getResourceAsStream("test.keystore"), new char[0]);
		alias = jks.aliases().nextElement();
	}

	@Override
	protected void setUp() throws Exception {
		loadKeyStore();
	}

	public void testDefaults() throws GeneralSecurityException {
		IArgs args = Args.create();
		ArgTools.putPath(args, "a", "b");

		IArgs signedArgs = new ArgsSigner().sign(args, getPrivateKey(), getCertificates());

		assertEquals("b", ArgTools.getString(args, "a", null));

		assertEquals("ArgDSig", ArgTools.getString(signedArgs, "type", null));
		assertEquals("1.0", ArgTools.getString(signedArgs, "version", null));

		// content.*
		assertEquals(
				"GqvDRBpF2ZDx3BeUoOqAxHUQ9/snKRzJ3lcwGEm+VxrdcCLm4saeDZVCRCGzSP7PvK7LH0OnxzVANVH/hQjHc44+pEkhKvbDyEXqm71ctGA62JD6pFzsERRvQedMl2J4Ta+2gQI2SmS2GpEbwQ2UOGcnO9gohojHrQkGUsh7FeviA9OXxRaDWKWrYBUAn2+4ox4uBCZ5KCFuIttVSn/6tOmEsrGTvQIKC7AFzTU5lJPfOTar75llH3HaxugFMKKzVhlK9jcy3fdxeBXsAiCRx7fjh4+OuNAL+M0+EkwcNTvn2ZpP+HmoHBu2XbJ7BdhqB5xgMFBUuMAHsQZAz77o2w==",
				ArgTools.getString(signedArgs, "content.value", null));

		// content.signed.*
		assertEquals("*;!signature", ArgTools.getString(signedArgs, "content.signed.select", null));
		assertNull(ArgTools.getString(signedArgs, "content.signed.algorithm", null));

		// content.signed.hash.*
		assertEquals("d+fOd8cHqBR7tlpxCsGvP8oCyN0r42di7JYR2Q+1wEE=",
				ArgTools.getString(signedArgs, "content.signed.hash.raw", null));
		assertNull(ArgTools.getString(signedArgs, "content.signed.hash.algorithm", null));

		// content.signer.certificates.0.*
		assertEquals(
				"MIIC6jCCAdKgAwIBAgIEUYPDyTANBgkqhkiG9w0BAQsFADA3MRMwEQYDVQQLDApKVW5pdC1UZXN0MSAwHgYDVQQDDBdUcnVzdGVkIEFwcGxldCBEZXBsb3llcjAeFw0xMzA1MDMxNDA0NDVaFw0yMzA1MDMxNDA0NDVaMDcxEzARBgNVBAsMCkpVbml0LVRlc3QxIDAeBgNVBAMMF1RydXN0ZWQgQXBwbGV0IERlcGxveWVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsC1mcWH7fm9RP9bhk4U+zrwZE8vVMT9saepDlxJOq3zjzmECrI5zbLNrFj5+jzSrIEa9chF0jtH2lnTAH9aV3GmSsQuD2Wv6vCgYHT0a5pk0H+eIozkNukeLlHAsPAoW9Dzd0ccVpfU4u3HeHDNcLjQxm1PqAzgD+UKdq3509SjLgduXU+jFr577SVN/U4IBpeLQ3qc8TOJgopYC/Ci/9ILHmO1x8CJtyeJ9jLpoiqMwulX4ShxEcGuBm/75P6ujoKHA+EyebZC0kb79c90obbaE/WygxmiwSljYX4HY6EZvm1G4PRdn9FeNG9laX8hqV7/+mNwZO/rszHi9BjZFXwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQCvFfkAq5QaeIfeFqWI0LYYS38NgWiS4aLSns2e1l7G2kK7FEuerKFb1XjFUd5Rw21zHAdFyvRGASB2aE/7FdXD4bTeDM18WPZkRTJWxk11TUTqwm1AdY432xEi2uP9LevdVv81b7VKvt4qfUAx80I4mGnfGPUep2EFgvDdCDMfbPclxoZ9Tp4RVOrMi7r3wqGIrl1j0VATlF6LauQjWtD6vKWoo5jo2/QgR9VtRgxx0GSYBNYSmxRqSsCkF2pt8VABdLnWbHvk+Uinpiz7yJNloG9ebQQW7AAAlxSJhJs9SxYrRyioEB5t4B02VJ5DvqSWRJXUNtz7ufvNg2+G+D3J",
				ArgTools.getString(signedArgs, "content.signer.certificates.0.value", null));
		assertNull(ArgTools.getString(signedArgs, "content.signer.certificates.0.type", null));
	}

	public void testFields() throws GeneralSecurityException {
		IArgs args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "c", "d");

		IArgs signedArgs = new ArgsSigner().sign(args, ListTools.with("!signature", "a", "!c"),
				ArgsCryptoBase.DEFAULT_DIGEST_ALGORITHM, ArgsCryptoBase.DEFAULT_SIGNATURE_ALGORITHM, getPrivateKey(),
				getCertificates());

		// content.signed.*
		assertEquals("!signature;a;!c", ArgTools.getString(signedArgs, "content.signed.select", null));

		// content.signed.hash.* || should be the same as in testDefaults
		assertEquals("d+fOd8cHqBR7tlpxCsGvP8oCyN0r42di7JYR2Q+1wEE=",
				ArgTools.getString(signedArgs, "content.signed.hash.raw", null));
	}

	public void testNonDefaultAlgorithms() throws GeneralSecurityException {
		IArgs args = Args.create();
		ArgTools.putPath(args, "a", "b");

		IArgs signedArgs = new ArgsSigner().sign(args, null, "SHA1", "MD5withRSA", getPrivateKey(), getCertificates());

		// content.*
		assertEquals(
				"a87U4255GwSZFAGlZZPR7SlwoXfzXp3sVg8epvz27OvjTScEKDJXkRMgnk5nDsMZa/qru/R+FocUT6jp7dkYK2AX/3R6696NCY43H7WQUHYAoVAO+DdH7hcVNwHARF6U3SifZ+bpCc8GmaBbUmxZU7SAtLdaQK1nRMPkVLJcRx4GhNOVwIsvXxX+sFePlEBXIehk6W+xMQ+qlOXDYIAYJdxS5zCVCtyK636eRNHbDcHXmFgzaKAL69PB9TfiQ/pYfTk9fh+1P20dBbSYC6t7FV0N/HZ9NXhrDeOi4khibHrDlSDA6qpY8X3IT0GOSNoWc9q42tai4aZ8bMMvBgENig==",
				ArgTools.getString(signedArgs, "content.value", null));
		assertEquals("MD5withRSA", ArgTools.getString(signedArgs, "content.signed.algorithm", null));

		// content.signed.hash.*
		assertEquals("SIBo/h6UaM2VpfSBLtmLJNJfqZc=", ArgTools.getString(signedArgs, "content.signed.hash.raw", null));
		assertEquals("SHA1", ArgTools.getString(signedArgs, "content.signed.hash.algorithm", null));
	}

	public void unsupported_testTokenSignature() throws GeneralSecurityException {
		// TODO TokenSignature is not supported anymore
		/*
		 * ArgsSigner signer; IArgs args; IArgs signatureArgs; List<String>
		 * fields; MessageDigest digest; Signature signature; KeyPair keyPair;
		 * PrivateKey privateKey; PublicKey publicKey; List<Certificate>
		 * certificates; // args = Args.create(); ArgTools.putPath(args, "a",
		 * "b"); ArgTools.putPath(args, "x", "y"); fields = ListTools.with("*");
		 * digest = createDigest("SHA-256"); signature =
		 * createSignature("Token"); keyPair = createKeyPair("Token");
		 * privateKey = keyPair.getPrivate(); publicKey = keyPair.getPublic();
		 * certificates = null; signer = new ArgsSigner(); signatureArgs =
		 * signer.sign(args, fields, digest, signature, privateKey, publicKey,
		 * certificates); System.out.println(ArgTools.toString(signatureArgs,
		 * "")); assertTrue(signatureArgs != null);
		 */
	}
}
