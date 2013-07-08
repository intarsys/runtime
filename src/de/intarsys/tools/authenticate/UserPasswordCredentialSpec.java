package de.intarsys.tools.authenticate;

public class UserPasswordCredentialSpec implements ICredentialSpec {

	private String user;

	public UserPasswordCredentialSpec() {
	}

	public UserPasswordCredentialSpec(String user) {
		super();
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
