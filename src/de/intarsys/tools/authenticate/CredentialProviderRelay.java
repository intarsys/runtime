package de.intarsys.tools.authenticate;

import java.util.ArrayList;
import java.util.List;

public class CredentialProviderRelay implements ICredentialStore {

	private List<ICredentialProvider> providers;

	public CredentialProviderRelay() {
		providers = new ArrayList<>();
		CredentialProvider.set(this);
	}

	@Override
	public ICredential getCredential(ICredentialScope scope) {
		for (ICredentialProvider provider : providers) {
			ICredential credential = provider.getCredential(scope);
			if (credential != null) {
				return credential;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		for (ICredentialProvider provider : providers) {
			if (provider instanceof ICredentialStore) {
				((ICredentialStore) provider).clear();
			}
		}
	}

	public void registerCredentialProvider(ICredentialProvider provider) {
		providers.add(provider);
	}

	@Override
	public void removeCredential(ICredentialScope scope) {
		for (ICredentialProvider provider : providers) {
			if (provider instanceof ICredentialStore) {
				((ICredentialStore) provider).removeCredential(scope);
			}
		}
	}

	@Override
	public void setCredential(ICredentialScope scope, ICredential credential) {
		for (ICredentialProvider provider : providers) {
			if (provider instanceof ICredentialStore) {
				((ICredentialStore) provider).setCredential(scope, credential);
			}
		}
	}
}
