/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.category;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.lang.Aliases;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.presentation.PresentationMixin;
import de.intarsys.tools.reflect.IClassLoaderSupport;

public class GenericCategory
		implements ICategory, IPresentationSupport, IElementConfigurable, IClassLoaderSupport, IContextSupport {

	private IElement element;

	private PresentationMixin presentation = new PresentationMixin(this);

	private String id;

	private String parentId;

	private ClassLoader classLoader;

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		element = pElement;
		String tmpId = element.attributeValue("id", getId());
		tmpId = Aliases.get().resolve(tmpId);
		if (tmpId == null) {
			throw new ConfigurationException("category needs an id");
		}
		setId(tmpId);
		parentId = element.attributeValue("parent", null);
		parentId = Aliases.get().resolve(parentId);
		//
		presentation.configure(pElement);
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public String getDescription() {
		return presentation.getDescription();
	}

	public IElement getElement() {
		return element;
	}

	@Override
	public String getIconName() {
		return presentation.getIconName();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return presentation.getLabel();
	}

	public IMessageBundle getMessageBundle() {
		return presentation.getMessageBundle();
	}

	@Override
	public ICategory getParent() {
		return CategoryRegistry.get().lookupCategory(getParentId());
	}

	public String getParentId() {
		return parentId;
	}

	@Override
	public String getTip() {
		return presentation.getTip();
	}

	@PostConstruct
	public void register() {
		CategoryRegistry.get().registerCategory(this);
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		if (context instanceof IMessageBundleSupport) {
			setMessageBundle(((IMessageBundleSupport) context).getMessageBundle());
		}
		if (context instanceof IClassLoaderSupport) {
			setClassLoader(((IClassLoaderSupport) context).getClassLoader());
		}
	}

	public void setDescription(Object description) {
		presentation.setDescription(description);
	}

	public void setElement(IElement element) {
		this.element = element;
	}

	public void setIconName(Object iconName) {
		presentation.setIconName(iconName);
	}

	public void setId(String id) {
		this.id = id;
		this.presentation.setCodePrefix(id);
	}

	public void setLabel(Object label) {
		presentation.setLabel(label);
	}

	public void setMessageBundle(IMessageBundle messageBundle) {
		presentation.setMessageBundle(messageBundle);
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setTip(Object tip) {
		presentation.setTip(tip);
	}

}
