/*
 * Copyright (c) 2012, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.authenticate;

import java.io.IOException;

import de.intarsys.tools.crypto.CryptoEnvironment;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementSerializable;

/**
 * An {@link ICredential} container for user name and password.
 * 
 */
public class UserPasswordCredential implements ICredential,
		IElementSerializable {

	public static final String ATTR_PASSWORD = "password"; //$NON-NLS-1$
	public static final String ATTR_QUALIFIED_USER_NAME = "qualifiedUserName"; //$NON-NLS-1$
	public static final String ATTR_USER = "user"; //$NON-NLS-1$

	// when storing the password in a variable it can as well be a String
	private String password;
	private boolean passwordEncrypted;
	private String user;

	public UserPasswordCredential(String userName, char[] password) {
		this.user = userName;
		this.password = new String(password);
		passwordEncrypted = false;
		CryptoEnvironment cryptoEnvironment = CryptoEnvironment.get();
		if (cryptoEnvironment.getDefaultCryptdecFactoryEncrypt() != null) {
			try {
				this.password = cryptoEnvironment
						.encryptStringEncoded(this.password);
				passwordEncrypted = true;
			} catch (IOException ex) {
				// no remedy for this; go on
			}
		}
	}

	public UserPasswordCredential(String userName, String password,
			boolean passwordEncrypted) {
		this.user = userName;
		this.password = password;
		this.passwordEncrypted = passwordEncrypted;
	}

	public char[] getPassword() {
		String passwordPlain = null;
		if (passwordEncrypted) {
			try {
				passwordPlain = CryptoEnvironment.get().decryptStringEncoded(
						password);
			} catch (IOException ex) {
				// no remedy for this; but hope it will never happen
				throw new RuntimeException(ex);
			}
		} else {
			passwordPlain = password;
		}
		return passwordPlain.toCharArray();
	}

	public String getQualifiedUserName() {
		return getUser();
	}

	public String getUser() {
		return user;
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		if (!passwordEncrypted) {
			// we don't serialize unencrypted passwords
			throw new ElementSerializationException();
		}
		element.setAttributeValue(ElementTools.ATTR_FACTORY,
				CredentialFactory.class.getName());
		element.setAttributeValue(ATTR_USER, user);
		element.setAttributeValue(ATTR_PASSWORD, password);
	}
}
