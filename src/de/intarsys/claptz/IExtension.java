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
package de.intarsys.claptz;

import java.util.List;

import de.intarsys.claptz.impl.ExtensionOperation;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.infoset.IElement;

/**
 * An object defining the dynamic extension to an {@link IExtensionPoint}.
 * <p>
 * "getElement" provides the definition data using an {@link IElement} object,
 * where the {@link IElement} returned is the defining XML element "extension"
 * itself.
 * <p>
 * The provider object gives access to the {@link IInstrument} providing the
 * extension.
 * <p>
 * 'id' is an optional attribute for identifying the {@link IExtension} itself.
 * <p>
 * An {@link IExtension} is defined using an XML structure:
 * 
 * <pre>
 *   &lt;extension point=&quot;an.extension.point&quot; id=&quot;optional.id&quot;&gt;
 *   	&lt;anyelement/&gt;
 *   &lt;/extension&gt;
 * </pre>
 * 
 * <p>
 * There is currently no generic means of declaring / checking the extension
 * element content / structure. In particular there is currently no means to add
 * a DTD or schema declaration for the extension element content.
 * 
 */
public interface IExtension extends IAttributeSupport {

	public void attachDelete(Object target);

	public void attachInsert(Object target);

	public void attachUpdate(Object target);

	public List<ExtensionOperation> getOperations();

	/**
	 * The defining root {@link Element} for the {@link IExtension}.
	 * 
	 * @return The defining root {@link Element} for the {@link IExtension}.
	 */
	public IElement getElement();

	/**
	 * The {@link IExtensionPoint} extended by this {@link IExtension}
	 * 
	 * @return
	 */
	public IExtensionPoint getExtensionPoint();

	/**
	 * An optional id for the {@link IExtension}.
	 * 
	 * @return An optional id for the {@link IExtension}.
	 */
	public String getId();

	/**
	 * The object where the {@link IExtension} was provided.
	 * 
	 * @return The object where the {@link IExtension} was provided.
	 */
	public IInstrument getProvider();

}
