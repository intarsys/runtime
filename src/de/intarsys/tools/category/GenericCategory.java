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
package de.intarsys.tools.category;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IAttribute;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.message.Message;
import de.intarsys.tools.presentation.IPresentationSupport;

public class GenericCategory implements ICategory, IPresentationSupport,
		IElementConfigurable {

	private Object description = null;

	private IElement element;

	private String iconName = null;

	private String id = null;

	private Object label = null;

	private String parentId;

	private Object tip = null;

	public void configure(IElement pElement)
			throws ConfigurationException {
		element = pElement;
		id = element.attributeValue("id", null);
		if (id == null) {
			throw new ConfigurationException("category needs an id");
		}
		parentId = element.attributeValue("parent", null);
		//
		IAttribute attribute;
		attribute = element.attribute("label");
		if (attribute != null) {
			setLabel(attribute.getData());
		}
		attribute = element.attribute("tip");
		if (attribute != null) {
			setTip(attribute.getData());
		}
		attribute = element.attribute("description");
		if (attribute != null) {
			setDescription(attribute.getData());
		}
		setIconName(element.attributeValue("icon", null));
	}

	public String getDescription() {
		if (description == null) {
			// support tip change, do not cache
			return getTip();
		}
		if (description instanceof Message) {
			description = ((Message) description).get();
		}
		return (String) description;
	}

	public IElement getElement() {
		return element;
	}

	public String getIconName() {
		return iconName;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		if (label == null) {
			// support change, do not cache
			return getId();
		}
		if (label instanceof Message) {
			label = ((Message) label).get();
		}
		return (String) label;
	}

	public ICategory getParent() {
		return CategoryRegistry.get().lookupCategory(getParentId());
	}

	public String getParentId() {
		return parentId;
	}

	public String getTip() {
		if (tip == null) {
			// support change, do not cache
			return getLabel();
		}
		if (tip instanceof Message) {
			tip = ((Message) tip).get();
		}
		return (String) tip;
	}

	public void setDescription(Object description) {
		this.description = description;
	}

	public void setElement(IElement element) {
		this.element = element;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(Object label) {
		this.label = label;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setTip(Object tip) {
		this.tip = tip;
	}
}
