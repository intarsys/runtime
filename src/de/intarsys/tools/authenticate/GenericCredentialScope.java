package de.intarsys.tools.authenticate;

public class GenericCredentialScope implements ICredentialScope {

	private final ICredentialSpec credentialSpec;

	public GenericCredentialScope(ICredentialSpec credentialSpec) {
		super();
		this.credentialSpec = credentialSpec;
	}

	@Override
	public ICredentialSpec getCredentialSpec() {
		return credentialSpec;
	}

	@Override
	public String getPrompt() {
		// nothing specific
		return null;
	}

	@Override
	public int match(ICredentialScope scope) {
		if (scope == this) {
			return 1;
		}
		return -1;
	}

}
