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
package de.intarsys.aaa.authentication;

import java.io.Serializable;

import de.intarsys.tools.authenticate.ICredential;
import de.intarsys.tools.authenticate.ICredentialSpec;

/**
 * The authentication strategy.
 * <p>
 * The {@link IAuthenticationHandler} manages authentication of a subject. After
 * successful authentication with a given {@link ICredential}, getSubject
 * returns the currently active authenticated object.
 * 
 */
public interface IAuthenticationHandler extends Serializable {

	/**
	 * Perform the necessary authentication procedure for the given credential.
	 * If successful, establish the subject for the active authentication
	 * context.
	 * <p>
	 * "credential" may be null to cancel authentication.
	 * 
	 * @param credential
	 * @throws AuthenticationException
	 */
	public void authenticate(ICredential credential)
			throws AuthenticationException;

	/**
	 * A description of the {@link ICredential} expected by this
	 * {@link IAuthenticationHandler} upon authenticate.
	 * 
	 * @return
	 */
	public ICredentialSpec getCredentialSpec();

	/**
	 * The authenticated subject for the active authentication context.
	 * 
	 * @return The authenticated subject for the active authentication context.
	 */
	public ISubject getSubject();

	/**
	 * <code>true</code> if the {@link IAuthenticationHandler} needs to be
	 * authenticated.
	 * 
	 * @return
	 */
	public boolean isAuthenticationRequired();

}
