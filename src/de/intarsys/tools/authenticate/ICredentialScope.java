package de.intarsys.tools.authenticate;

/**
 * A scope where an {@link ICredential} is valid.
 * 
 */
public interface ICredentialScope {

	/**
	 * The {@link ICredentialSpec} for the {@link ICredential} required for this
	 * scope.
	 * 
	 * @return The {@link ICredentialSpec} for the {@link ICredential} required
	 *         for this scope.
	 */
	public ICredentialSpec getCredentialSpec();

	/**
	 * A prompt string that can be displayed to the user.
	 * 
	 * @return The prompt string
	 */
	public String getPrompt();

	/**
	 * An indicator of similarity for two scopes. The higher the value, the
	 * better the match. -1 indicates no match.
	 * 
	 * @param current
	 * @return
	 */
	public int match(ICredentialScope scopes);

}
