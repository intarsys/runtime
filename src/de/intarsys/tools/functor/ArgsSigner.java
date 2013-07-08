package de.intarsys.tools.functor;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.util.List;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.StringTools;

public class ArgsSigner extends ArgsCryptoBase {

	/**
	 * Create an {@link IArgs} structure defining the hash over args, using
	 * digest algorithm.
	 * 
	 * @param digestAlgorithm
	 * @param args
	 * @return
	 * @throws GeneralSecurityException
	 */
	protected IArgs createHashArgs(String digestAlgorithm, IArgs args)
			throws GeneralSecurityException {
		IArgs hashArgs = Args.create();
		if (!DEFAULT_DIGEST_ALGORITHM.equals(digestAlgorithm)) {
			hashArgs.put(ARG_ALGORITHM, digestAlgorithm);
		}
		byte[] hashValue = hash(digestAlgorithm, args);
		hashArgs.put(ARG_VALUE, new String(Base64.encode(hashValue)));
		return hashArgs;
	}

	/**
	 * Compute the signature value on args. The result is the Base64 encoded
	 * signature.
	 * 
	 * @param signature
	 * @param privateKey
	 * @param args
	 * @return
	 * @throws GeneralSecurityException
	 */
	protected String createSignatureValue(String signatureAlgorithm,
			PrivateKey privateKey, IArgs args) throws GeneralSecurityException {
		Signature signature = Signature.getInstance(signatureAlgorithm);
		signature.initSign(privateKey);
		updateSignature(signature, args);
		byte[] signatureValue = signature.sign();
		return new String(Base64.encode(signatureValue));
	}

	/**
	 * Create the {@link IArgs} structure containing the signed properties of
	 * the signature.
	 * 
	 * @param args
	 * @param signature
	 * @param fields
	 * @param digest
	 * @return
	 * @throws GeneralSecurityException
	 */
	protected IArgs createSignedArgs(IArgs args, String signatureAlgorithm,
			List<String> fields, String digestAlgorithm)
			throws GeneralSecurityException {
		IArgs signedArgs = Args.create();
		if (!DEFAULT_SIGNATURE_ALGORITHM.equals(signatureAlgorithm)) {
			signedArgs.put(ARG_ALGORITHM, signatureAlgorithm);
		}
		if (fields == null || fields.isEmpty()) {
			fields = DEFAULT_SELECT;
		}
		String selectString = StringTools.join(fields, ";"); //$NON-NLS-1$
		signedArgs.put(ARG_SELECT, selectString);
		IArgs signedContent = createSignedContentArgs(args, fields);
		IArgs hash = createHashArgs(digestAlgorithm, signedContent);
		signedArgs.put(ARG_HASH, hash);
		return signedArgs;
	}

	/**
	 * Create an {@link IArgs} structure defining the signer principal.
	 * 
	 * @return
	 * @throws CertificateEncodingException
	 */
	protected IArgs createSignerArgs(List<Certificate> certificates)
			throws CertificateEncodingException {
		IArgs signerArgs = Args.create();
		IArgs certificatesArgs = Args.create();
		signerArgs.put(ARG_CERTIFICATES, certificatesArgs);
		for (Certificate certificate : certificates) {
			IArgs certArgs = Args.create();
			certificatesArgs.add(certArgs);

			if (!DEFAULT_CERTIFICATE_TYPE.equals(certificate.getType())) {
				certArgs.put(ARG_TYPE, certificate.getType());
			}
			certArgs.put(ARG_VALUE,
					new String(Base64.encode(certificate.getEncoded())));
		}
		return signerArgs;
	}

	/**
	 * Sign the {@link IArgs} structure, restricted to the subtree defined by
	 * fields, using the provided algorithms.
	 * 
	 * @param args
	 *            to be signed
	 * @param fields
	 *            to filter the signed args. If empty, a default will be used.
	 * @param digestAlgorithm
	 *            If null, default SHA-256 will be used
	 * @param signatureAlgorithm
	 *            If null, default SHA256withRSA will be used
	 * @param privateKey
	 * @param certificate
	 * @return The {@link IArgs} structure containing the signature on args,
	 *         restricted to the subtree defined by fields.
	 * @throws GeneralSecurityException
	 */
	public IArgs sign(IArgs args, List<String> fields, String digestAlgorithm,
			String signatureAlgorithm, PrivateKey privateKey,
			List<Certificate> certificates) throws GeneralSecurityException {
		IArgs signatureArgs = Args.create();
		signatureArgs.put(ARG_TYPE, TYPE_ARGDSIG);
		signatureArgs.put(ARG_VERSION, VERSION_1_0);
		IArgs signatureContentArgs = Args.create();
		signatureArgs.put(ARG_CONTENT, signatureContentArgs);
		IArgs signedArgs = createSignedArgs(args, signatureAlgorithm, fields,
				digestAlgorithm);
		signatureContentArgs.put(ARG_SIGNED, signedArgs);
		IArgs signerArgs = createSignerArgs(certificates);
		signatureContentArgs.put(ARG_SIGNER, signerArgs);
		String signatureValue = createSignatureValue(signatureAlgorithm,
				privateKey, signedArgs);
		signatureContentArgs.put(ARG_VALUE, signatureValue);
		return signatureArgs;
	}

	/**
	 * Sign the {@link IArgs} structure with the provided private key.
	 * 
	 * @param args
	 * @param privateKey
	 *            a RSA private key
	 * @param certificate
	 * @return The {@link IArgs} structure containing the signature on args,
	 *         restricted to the subtree defined by selected.
	 * @throws GeneralSecurityException
	 */
	public IArgs sign(IArgs args, PrivateKey privateKey,
			List<Certificate> certificates) throws GeneralSecurityException {
		return sign(args, Collections.<String> emptyList(),
				DEFAULT_DIGEST_ALGORITHM, DEFAULT_SIGNATURE_ALGORITHM,
				privateKey, certificates);
	}

}
