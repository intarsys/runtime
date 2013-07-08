package de.intarsys.tools.authenticate;

import java.util.HashMap;
import java.util.Map;

public class BasicCredentialProvider implements ICredentialStore {

	final private Map<ICredentialScope, ICredential> credentials = new HashMap<ICredentialScope, ICredential>();

	@Override
	public void clear() {
		credentials.clear();
	}

	@Override
	public ICredential getCredential(ICredentialScope scope) {
		ICredential creds = credentials.get(scope);
		if (creds == null) {
			int bestMatchFactor = -1;
			ICredentialScope bestMatch = null;
			for (ICredentialScope current : credentials.keySet()) {
				int factor = scope.match(current);
				if (factor > bestMatchFactor) {
					bestMatchFactor = factor;
					bestMatch = current;
				}
			}
			if (bestMatch != null) {
				creds = credentials.get(bestMatch);
			}
		}
		return creds;
	}

	@Override
	public void removeCredential(ICredentialScope scope) {
		ICredential creds = credentials.remove(scope);
		if (creds == null) {
			int bestMatchFactor = -1;
			ICredentialScope bestMatch = null;
			for (ICredentialScope current : credentials.keySet()) {
				int factor = scope.match(current);
				if (factor > bestMatchFactor) {
					bestMatchFactor = factor;
					bestMatch = current;
				}
			}
			if (bestMatch != null) {
				creds = credentials.remove(bestMatch);
			}
		}
	}

	@Override
	public void setCredential(ICredentialScope scope, ICredential credential) {
		credentials.put(scope, credential);
	}
}
