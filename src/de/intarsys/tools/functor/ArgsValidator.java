package de.intarsys.tools.functor;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("nls")
public class ArgsValidator extends ArgsCryptoBase {

	private List<Certificate> loadCertificates(IArgs contentArgs)
			throws ArgsValidationException {
		List<Certificate> certificates = new ArrayList<Certificate>();
		IArgs certificatesArgs = ArgTools.getArgs(contentArgs, ARG_SIGNER + "."
				+ ARG_CERTIFICATES, Args.create());
		for (int i = 0; i < certificatesArgs.size(); i++) {
			IArgs certArgs = ArgTools.getArgs(certificatesArgs,
					String.valueOf(i), Args.create());
			byte[] certValue = ArgTools.getByteArray(certArgs, ARG_VALUE, null);
			if (certValue == null) {
				throw new ArgsValidationException("certificates." + i
						+ ".value is missing");
			}
			String certType = ArgTools.getString(certArgs, ARG_TYPE,
					DEFAULT_CERTIFICATE_TYPE);
			try {
				Certificate cert = CertificateFactory.getInstance(certType)
						.generateCertificate(
								new ByteArrayInputStream(certValue));
				certificates.add(cert);
			} catch (CertificateException e) {
				throw new ArgsValidationException(e.getLocalizedMessage(), e);
			}
		}
		if (certificates.isEmpty()) {
			throw new ArgsValidationException(
					"signature.content.signer.certificates is missing");
		}
		return certificates;
	}

	/**
	 * Validate the {@link IArgs} structure, with the parameters contained in
	 * signatureArgs.
	 * 
	 * @param args
	 *            to be verified
	 * @param signatureArgs
	 *            containing the signature data. It doesn't hurt, if it is a
	 *            subtree of args.
	 * @return
	 * @throws ArgsValidationException
	 */
	public List<Certificate> validate(IArgs args, IArgs signatureArgs)
			throws ArgsValidationException {
		String type = ArgTools.getString(signatureArgs, ARG_TYPE, TYPE_ARGDSIG);
		String version = ArgTools.getString(signatureArgs, ARG_VERSION,
				VERSION_1_0);
		if (TYPE_ARGDSIG.equals(type) && VERSION_1_0.equals(version)) {
			List<Certificate> certificates = validateSignature(args,
					signatureArgs);
			validateSignedArgsHash(args, signatureArgs);
			return certificates;
		} else {
			throw new ArgsValidationException(
					"Unsupported signature type or version");
		}
	}

	private List<Certificate> validateSignature(IArgs args, IArgs signatureArgs)
			throws ArgsValidationException {
		IArgs contentArgs = ArgTools.getArgs(signatureArgs, ARG_CONTENT,
				Args.create());
		byte[] signatureValue = ArgTools.getByteArray(contentArgs, ARG_VALUE,
				null);
		if (signatureValue == null) {
			throw new ArgsValidationException(
					"signature.content.value is missing");
		}
		IArgs signedArgs = ArgTools.getArgs(contentArgs, ARG_SIGNED,
				Args.create());
		String signatureAlgorithm = ArgTools.getString(signedArgs,
				ARG_ALGORITHM, DEFAULT_SIGNATURE_ALGORITHM);

		List<Certificate> certificates = loadCertificates(contentArgs);
		try {
			Signature jcaSignature = Signature.getInstance(signatureAlgorithm);
			jcaSignature.initVerify(certificates.get(0));
			updateSignature(jcaSignature, signedArgs);
			if (!jcaSignature.verify(signatureValue)) {
				throw new ArgsValidationException(
						"signature could not be verified");
			}
		} catch (GeneralSecurityException e) {
			throw new ArgsValidationException(e.getLocalizedMessage(), e);
		}
		return certificates;
	}

	private void validateSignedArgsHash(IArgs args, IArgs signatureArgs)
			throws ArgsValidationException {
		IArgs signedArgs = ArgTools.getArgs(signatureArgs, ARG_CONTENT + "."
				+ ARG_SIGNED, Args.create());
		byte[] hash = ArgTools.getByteArray(signedArgs, ARG_HASH + "."
				+ ARG_VALUE, null);
		if (hash == null) {
			throw new ArgsValidationException(
					"signature.content.signed.hash.value is missing");
		}

		List<String> fields = ArgTools.getList(signedArgs, ARG_SELECT,
				DEFAULT_SELECT);
		String digestAlgorithm = ArgTools.getString(signedArgs, ARG_HASH + "."
				+ ARG_ALGORITHM, DEFAULT_DIGEST_ALGORITHM);
		IArgs signedContent = createSignedContentArgs(args, fields);
		try {
			byte[] contentDigest = hash(digestAlgorithm, signedContent);
			if (!Arrays.equals(hash, contentDigest)) {
				throw new ArgsValidationException(
						"args hash doesn't match the signed hash");
			}
		} catch (GeneralSecurityException e) {
			throw new ArgsValidationException(e.getLocalizedMessage(), e);
		}
	}

}
