package de.intarsys.tools.functor;

import java.security.cert.Certificate;
import java.util.List;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class TestArgsValidator extends TestCase {

	private void assertArgsValidationException(IArgs args) {
		try {
			new ArgsValidator().validate(args, (IArgs) args.get("signature"));
			fail("expected ArgsValidationException");
		} catch (Exception e) {
			assertTrue(e instanceof ArgsValidationException);
		}
	}

	private IArgs getDefaultSignature() {
		IArgs args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "signature.type", "ArgDSig");
		ArgTools.putPath(args, "signature.version", "1.0");
		ArgTools.putPath(args, "signature.content.value", //
				"p/NqV6dCWRKtcZCHmZMvyzyZh3bGvQ+DwKo/yRRM0UorYXU6AJ6IjafYtfYp8gmNgHgk60MWdW31esVakez3IV/+1rQV99jLuM0yLRYbRqs3+78M3DHNsr57g9UhqrMTJTLeNL2/r6DVQrUyc95TI4FJXBVbGswZn9HtLMWzEG0vKybK0um+Kngmyr4IFxotMGsAZ5XSh4io+xQWTpU/pdL4ihFOdZvNfWdNgEVIJrRtLFN4dtH2MrDv+WB/r4EGk3FWEi5kMEHoI7zl2EURmo9gBjKRtUHz7kUq6mgnu07WR2sDxnhvKssstGNROODPkPlTWmK9Rg5pl4h8H/Ud6A==");
		ArgTools.putPath(args, "signature.content.signed.select", "*;!signature");
		ArgTools.putPath(args, "signature.content.signed.hash.value", "d+fOd8cHqBR7tlpxCsGvP8oCyN0r42di7JYR2Q+1wEE=");
		ArgTools.putPath(args, "signature.content.signer.certificates.0.value", //
				"MIIC6jCCAdKgAwIBAgIEUYPDyTANBgkqhkiG9w0BAQsFADA3MRMwEQYDVQQLDApKVW5pdC1UZXN0MSAwHgYDVQQDDBdUcnVzdGVkIEFwcGxldCBEZXBsb3llcjAeFw0xMzA1MDMxNDA0NDVaFw0yMzA1MDMxNDA0NDVaMDcxEzARBgNVBAsMCkpVbml0LVRlc3QxIDAeBgNVBAMMF1RydXN0ZWQgQXBwbGV0IERlcGxveWVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsC1mcWH7fm9RP9bhk4U+zrwZE8vVMT9saepDlxJOq3zjzmECrI5zbLNrFj5+jzSrIEa9chF0jtH2lnTAH9aV3GmSsQuD2Wv6vCgYHT0a5pk0H+eIozkNukeLlHAsPAoW9Dzd0ccVpfU4u3HeHDNcLjQxm1PqAzgD+UKdq3509SjLgduXU+jFr577SVN/U4IBpeLQ3qc8TOJgopYC/Ci/9ILHmO1x8CJtyeJ9jLpoiqMwulX4ShxEcGuBm/75P6ujoKHA+EyebZC0kb79c90obbaE/WygxmiwSljYX4HY6EZvm1G4PRdn9FeNG9laX8hqV7/+mNwZO/rszHi9BjZFXwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQCvFfkAq5QaeIfeFqWI0LYYS38NgWiS4aLSns2e1l7G2kK7FEuerKFb1XjFUd5Rw21zHAdFyvRGASB2aE/7FdXD4bTeDM18WPZkRTJWxk11TUTqwm1AdY432xEi2uP9LevdVv81b7VKvt4qfUAx80I4mGnfGPUep2EFgvDdCDMfbPclxoZ9Tp4RVOrMi7r3wqGIrl1j0VATlF6LauQjWtD6vKWoo5jo2/QgR9VtRgxx0GSYBNYSmxRqSsCkF2pt8VABdLnWbHvk+Uinpiz7yJNloG9ebQQW7AAAlxSJhJs9SxYrRyioEB5t4B02VJ5DvqSWRJXUNtz7ufvNg2+G+D3J");
		return args;
	}

	private IArgs getNonDefaultAlgorithmsSignature() {
		IArgs args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "c", "d");
		ArgTools.putPath(args, "signature.type", "ArgDSig");
		ArgTools.putPath(args, "signature.version", "1.0");
		ArgTools.putPath(args, "signature.content.signed.algorithm", "MD5withRSA");
		ArgTools.putPath(args, "signature.content.value", //
				"eyF7C7YcjC/OI4ig2SGAmjFkA40mvqanyeXbwVoeR3sGTZ/UTdWaoiP8As1gv+X/GiAlsolVS0Yh9l+gPZoZeMs0N4qOOVdqAqK2tSDD/aX0y6OkLVVaq2ZuPoXxmDdTC//GaFSetXwk4ME90ctOVoeJbj1iDmTMAc+x2eGTDRu+3+M8YoOPNx3AfCmexSHadfUCkaOVtSSAvXCpq4FLfKUbsyPeJ+ptumGgV4BlkeCS95YpSN7hWCFeoA7YwW0ajFmG7Z6pnl64kZb5aRHf5/exhV6xmfJCDEvwXhpx5nt87gF9Mz0qGF/0iUHNn7xlzowAidqnKSQ+nasVkuq+Ew==");
		ArgTools.putPath(args, "signature.content.signed.select", "!signature;a;!c");
		ArgTools.putPath(args, "signature.content.signed.hash.algorithm", "SHA1");
		ArgTools.putPath(args, "signature.content.signed.hash.value", "SIBo/h6UaM2VpfSBLtmLJNJfqZc=");
		ArgTools.putPath(args, "signature.content.signer.certificates.0.value", //
				"MIIC6jCCAdKgAwIBAgIEUYPDyTANBgkqhkiG9w0BAQsFADA3MRMwEQYDVQQLDApKVW5pdC1UZXN0MSAwHgYDVQQDDBdUcnVzdGVkIEFwcGxldCBEZXBsb3llcjAeFw0xMzA1MDMxNDA0NDVaFw0yMzA1MDMxNDA0NDVaMDcxEzARBgNVBAsMCkpVbml0LVRlc3QxIDAeBgNVBAMMF1RydXN0ZWQgQXBwbGV0IERlcGxveWVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsC1mcWH7fm9RP9bhk4U+zrwZE8vVMT9saepDlxJOq3zjzmECrI5zbLNrFj5+jzSrIEa9chF0jtH2lnTAH9aV3GmSsQuD2Wv6vCgYHT0a5pk0H+eIozkNukeLlHAsPAoW9Dzd0ccVpfU4u3HeHDNcLjQxm1PqAzgD+UKdq3509SjLgduXU+jFr577SVN/U4IBpeLQ3qc8TOJgopYC/Ci/9ILHmO1x8CJtyeJ9jLpoiqMwulX4ShxEcGuBm/75P6ujoKHA+EyebZC0kb79c90obbaE/WygxmiwSljYX4HY6EZvm1G4PRdn9FeNG9laX8hqV7/+mNwZO/rszHi9BjZFXwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQCvFfkAq5QaeIfeFqWI0LYYS38NgWiS4aLSns2e1l7G2kK7FEuerKFb1XjFUd5Rw21zHAdFyvRGASB2aE/7FdXD4bTeDM18WPZkRTJWxk11TUTqwm1AdY432xEi2uP9LevdVv81b7VKvt4qfUAx80I4mGnfGPUep2EFgvDdCDMfbPclxoZ9Tp4RVOrMi7r3wqGIrl1j0VATlF6LauQjWtD6vKWoo5jo2/QgR9VtRgxx0GSYBNYSmxRqSsCkF2pt8VABdLnWbHvk+Uinpiz7yJNloG9ebQQW7AAAlxSJhJs9SxYrRyioEB5t4B02VJ5DvqSWRJXUNtz7ufvNg2+G+D3J");
		return args;
	}

	public void testAddedArgs() throws ArgsValidationException {
		IArgs args = getDefaultSignature();
		ArgTools.putPath(args, "f", "g");
		assertArgsValidationException(args);

		args = getNonDefaultAlgorithmsSignature();
		ArgTools.putPath(args, "f", "g");
		List<Certificate> certificates = new ArgsValidator().validate(args, (IArgs) args.get("signature"));
		assertFalse(certificates.isEmpty());
	}

	public void testModifiedArgs() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.putPath(args, "a", "c");
		assertArgsValidationException(args);
	}

	public void testModifiedButNotSignedArgs() throws ArgsValidationException {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.putPath(args, "c", "new value");
		List<Certificate> certificates = new ArgsValidator().validate(args, (IArgs) args.get("signature"));
		assertFalse(certificates.isEmpty());
	}

	public void testModifiedHashValue() {
		IArgs args = getDefaultSignature();
		ArgTools.putPath(args, "signature.content.signed.hash.value", "d+fOd8cHqBR7tlpxCsGvP8oCyN0r42di7JYR2Q+AAE=");
		assertArgsValidationException(args);
	}

	public void testModifiedSelected() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.putPath(args, "signature.content.signed.select", "!signature;!a;!c");
		assertArgsValidationException(args);
	}

	public void testModifiedSignatureAlgorithm() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.putPath(args, "signature.content.signed.algorithm", "SHA1withRSA");
		assertArgsValidationException(args);
	}

	public void testModifiedType() {
		IArgs args = getDefaultSignature();
		ArgTools.putPath(args, "signature.type", "NewType");
		assertArgsValidationException(args);
	}

	public void testRemovedCertificateValue() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.undefinePath(args, "signature.content.signer.certificates.0.value");
		assertArgsValidationException(args);
	}

	public void testRemovedHashValue() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.undefinePath(args, "signature.content.signed.hash.value");
		assertArgsValidationException(args);
	}

	public void testRemovedSelected() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.undefinePath(args, "signature.content.signed.select");
		assertArgsValidationException(args);
	}

	public void testRemovedSignatureValue() {
		IArgs args = getNonDefaultAlgorithmsSignature();
		ArgTools.undefinePath(args, "signature.content.value");
		assertArgsValidationException(args);
	}

	public void testValidateDefaultSignature() throws ArgsValidationException {
		IArgs args = getDefaultSignature();
		List<Certificate> certificates = new ArgsValidator().validate(args, (IArgs) args.get("signature"));
		assertFalse(certificates.isEmpty());
	}

	public void testValidateNonDefaultSignature() throws ArgsValidationException {
		IArgs args = getNonDefaultAlgorithmsSignature();
		List<Certificate> certificates = new ArgsValidator().validate(args, (IArgs) args.get("signature"));
		assertFalse(certificates.isEmpty());
	}
}
