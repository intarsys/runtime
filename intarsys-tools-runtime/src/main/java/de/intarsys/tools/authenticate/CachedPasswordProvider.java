package de.intarsys.tools.authenticate;

import de.intarsys.tools.activity.IPrompter;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.message.IMessage;

/**
 * Provide the password cached from another {@link IPasswordProvider}.
 * 
 */
public class CachedPasswordProvider implements IPasswordProvider, IPrompter {

	private Secret password;

	private final IPasswordProvider passwordProvider;

	public CachedPasswordProvider(IPasswordProvider passwordProvider) {
		super();
		this.passwordProvider = passwordProvider;
	}

	@Override
	public Secret getPassword() {
		if (password != null) {
			return password;
		}
		password = getPasswordProvider().getPassword();
		return password;
	}

	public IPasswordProvider getPasswordProvider() {
		return passwordProvider;
	}

	@Override
	public void setMessage(IMessage message) {
		if (getPasswordProvider() instanceof IPrompter) {
			((IPrompter) passwordProvider).setMessage(message);
		}
	}

	@Override
	public void setTitle(IMessage title) {
		if (getPasswordProvider() instanceof IPrompter) {
			((IPrompter) passwordProvider).setTitle(title);
		}
	}
}