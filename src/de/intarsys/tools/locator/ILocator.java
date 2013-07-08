/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.component.ISynchronizable;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * An abstraction for the location of resource data.
 * 
 */
public interface ILocator extends ISynchronizable {
	/**
	 * Delete the artifact referenced by this.
	 * 
	 * @throws IOException
	 */
	public void delete() throws IOException;

	/**
	 * Answer <code>true</code> if the location designated by this exists.
	 * 
	 * @return Answer <code>true</code> if the location designated by this
	 *         exists.
	 */
	public boolean exists();

	/**
	 * The locator for the resource <code>name</code> within the context of
	 * this. This may for example be an {@link ILocator} to a file within a
	 * directory.
	 * 
	 * @param name
	 *            The name of the resource to be located.
	 * 
	 * @return The {@link ILocator} for the resource with the name "name" within
	 *         the context of this.
	 */
	public ILocator getChild(String name);

	/**
	 * The full physical name of this.
	 * 
	 * <p>
	 * This method returns a representation that is proprietary to the
	 * underlying physical representation, for example a file name, a SQL
	 * statement or so on.
	 * </p>
	 * 
	 * @return The full physical name of the receiver.
	 */
	public String getFullName();

	/**
	 * Return an {@link InputStream} on the data represented by the receiver.
	 * 
	 * @return An {@link InputStream} on the data represented by the receiver.
	 * 
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Returns the length of this data container or -1 if unknown
	 * 
	 * @return the length of this data container, measured in bytes.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public long getLength() throws IOException;

	/**
	 * The local name of the receiver within its parent.
	 * 
	 * @return The local name of the receiver within its parent.
	 */
	public String getLocalName();

	/**
	 * Return an {@link OutputStream} on the location represented by the
	 * receiver.
	 * 
	 * @return An {@link OutputStream} on the location represented by the
	 *         receiver.
	 * 
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * The {@link ILocator} that is one hierarchy level up or null. This may be
	 * for example the directory where the currently designated resource is
	 * found.
	 * 
	 * @return The {@link ILocator}that is one hierarchy level up or null.
	 */
	public ILocator getParent();

	/**
	 * The {@link IRandomAccess} for this.
	 * 
	 * @return The {@link IRandomAccess} for this.
	 * @throws IOException
	 */
	public IRandomAccess getRandomAccess() throws IOException;

	/**
	 * A {@link Reader} on the data represented by the receiver.
	 * 
	 * @return A {@link Reader} on the data represented by the receiver.
	 * 
	 * @throws IOException
	 */
	public Reader getReader() throws IOException;

	/**
	 * A {@link Reader} on the data represented by the receiver for the given
	 * encoding.
	 * 
	 * @param encoding
	 *            The encoding.
	 * 
	 * @return A {@link Reader} on the data represented by the receiver for the
	 *         given encoding.
	 * 
	 * @throws IOException
	 */
	public Reader getReader(String encoding) throws IOException;

	/**
	 * The type of the resource. This may be for example a mime type or the file
	 * extension of the underlying file.
	 * 
	 * @return The type of the resource
	 */
	public String getType();

	/**
	 * The qualified local name of the receiver within its parent that includes
	 * the type specification for the destination if appropriate. This is for
	 * example a filename with its correct suffix. Some locator may return the
	 * same name as "getLocalName".
	 * 
	 * @return The qualified local name of the receiver within its parent that
	 *         includes the type specification for the destination if
	 *         appropriate.
	 */
	public String getTypedName();

	/**
	 * A {@link Writer} on the location represented by the receiver.
	 * 
	 * @return A {@link Writer} on the location represented by the receiver.
	 * 
	 * @throws IOException
	 */
	public Writer getWriter() throws IOException;

	/**
	 * A {@link Writer} on the location represented by the receiver for the
	 * given encoding.
	 * 
	 * @param encoding
	 *            The encoding.
	 * 
	 * @return A {@link Writer} on the location represented by the receiver for
	 *         the given encoding.
	 * 
	 * @throws IOException
	 */
	public Writer getWriter(String encoding) throws IOException;

	/**
	 * Answer <code>true</code> if the location designated by this is a
	 * directory. A directory location serves as a container for other
	 * resources, you can never <code>getInputStream</code> on this.
	 * 
	 * @return Answer <code>true</code> if the location designated by this is a
	 *         directory.
	 */
	public boolean isDirectory();

	/**
	 * <code>true</code> if the specified resource is read only.
	 * 
	 * @return <code>true</code> if the specified resource is read only.
	 */
	public boolean isReadOnly();

	/**
	 * Return an array of {@link ILocator} that are children of the receiver
	 * that conform to <code>filter</code>. This method never returns null.
	 * 
	 * @param filter
	 *            The filter used to examine the child resources.
	 * 
	 * @return An array of {@link ILocator} objects that conform to the filter
	 *         argument.
	 * 
	 * @throws IOException
	 */
	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException;

	/**
	 * Rename the complete physical name to <code>newName</code>.
	 * 
	 * @param newName
	 *            The new name of the {@link ILocator}. The new name is expected
	 *            to contain both local and type part of the name.
	 * @throws IOException
	 */
	public void rename(String newName) throws IOException;

	/**
	 * Make the receiver read only. This is a one way switch only.
	 */
	public void setReadOnly();

	/**
	 * The location designated by this as an {@link URL}.
	 * 
	 * @return The location designated by this as an {@link URL}.
	 */
	public URL toURL();
}
