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

import java.text.MessageFormat;

import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;

public class NTCredential extends UserPasswordCredential {

	public static final String ATTR_DOMAIN = "domain"; //$NON-NLS-1$
	public static final MessageFormat FormatQualifiedUserName = new MessageFormat(
			"{0}\\{1}"); //$NON-NLS-1$

	private final String domain;

	public NTCredential(String userName, char[] password, String domain) {
		super(userName, password);
		this.domain = domain;
	}

	@Deprecated
	public NTCredential(String userName, char[] password, String workstation,
			String domain) {
		this(userName, password, domain);
	}

	public NTCredential(String userName, String password, String domain,
			boolean passwordEncrypted) {
		super(userName, password, passwordEncrypted);
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	@Override
	public String getQualifiedUserName() {
		return FormatQualifiedUserName.format(new Object[] { getDomain(),
				getUser() });
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		super.serialize(element);
		element.setAttributeValue(ATTR_DOMAIN, domain);
	}
}