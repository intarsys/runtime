package de.intarsys.tools.authenticate;

import java.text.ParseException;

import de.intarsys.tools.factory.CommonFactory;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

public class CredentialFactory extends CommonFactory<ICredential> {

	@Override
	protected ICredential basicCreateInstance(IArgs args)
			throws ObjectCreationException {
		IElement configuration = getConfiguration(args);
		if (configuration == null) {
			String qualifiedUserName = (String) args
					.get(UserPasswordCredential.ATTR_QUALIFIED_USER_NAME);
			String userName;
			String domain = null;
			if (qualifiedUserName != null) {
				try {
					Object[] tokens = NTCredential.FormatQualifiedUserName
							.parse(qualifiedUserName);
					domain = (String) tokens[0];
					userName = (String) tokens[1];
				} catch (ParseException ex) {
					userName = qualifiedUserName;
				}
			} else {
				userName = (String) args.get(UserPasswordCredential.ATTR_USER);
			}
			char[] password = (char[]) args
					.get(UserPasswordCredential.ATTR_PASSWORD);
			if (domain == null) {
				return new UserPasswordCredential(userName, password);
			}
			return new NTCredential(userName, password, domain);
		} else {
			String userName = configuration.attributeValue(
					UserPasswordCredential.ATTR_USER, null);
			String passwordEncrypted = configuration.attributeValue(
					UserPasswordCredential.ATTR_PASSWORD, null);
			String domain = configuration.attributeValue(
					NTCredential.ATTR_DOMAIN, null);
			if (domain == null) {
				return new UserPasswordCredential(userName, passwordEncrypted,
						true);
			}
			return new NTCredential(userName, passwordEncrypted, domain, true);
		}
	}

	@Override
	public Class<ICredential> getResultType() {
		return ICredential.class;
	}
}
