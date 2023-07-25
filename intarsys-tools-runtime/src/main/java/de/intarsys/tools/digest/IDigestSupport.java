package de.intarsys.tools.digest;

/**
 * An object that can return its own {@link IDigest}.
 */
public interface IDigestSupport {

	IDigest getDigest();

}
