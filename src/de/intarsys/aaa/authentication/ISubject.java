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

import java.util.List;

import de.intarsys.aaa.permission.IPermission;
import de.intarsys.tools.authenticate.ICredential;

/**
 * The authenticated subject.
 * 
 */
public interface ISubject {

	/**
	 * All {@link ICredential} instances associated with the subject.
	 * 
	 * @return All {@link ICredential} instances associated with the subject.
	 */
	public List<ICredential> getCredentials();

	/**
	 * The implementation object for this.
	 * <p>
	 * This is for example the persistent java object for a user.
	 * 
	 * @return The implementation object for this.
	 */
	public Object getImpl();

	/**
	 * The list of permissions for this subject.
	 * 
	 * @return The list of permissions for this subject.
	 */
	public List<IPermission> getPermissions();

	/**
	 * A flag if this subject has administrative rights.
	 * 
	 * @return A flag if this subject has administrative rights.
	 */
	public boolean isAdministrator();

}
