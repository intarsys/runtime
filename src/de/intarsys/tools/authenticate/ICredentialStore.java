package de.intarsys.tools.authenticate;

/**
 * An {@link ICredentialProvider} that supports writing.
 * 
 */
public interface ICredentialStore extends ICredentialProvider {

	/**
	 * Clear all credentials
	 */
	public void clear();

	/**
	 * Remove the credential for a given scope.
	 * 
	 * @param scope
	 */
	public void removeCredential(ICredentialScope scope);

	/**
	 * Associate a credential with a given scope.
	 * 
	 * @param scope
	 * @param credential
	 */
	public void setCredential(ICredentialScope scope, ICredential credential);

}
