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
package de.intarsys.tools.action;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;

/**
 * This is a proxy for an {@link IAction} that can be used to reference to
 * actions in an {@link IActionRegistry} by id. This is for example useful if
 * the actions are not yet fully known or accessible when parsing for example a
 * menu structure or when you need a stand-in for an existing action instead of
 * the {@link IAction} itself.
 */
public class ActionProxy extends ActionForwarder implements IElementConfigurable, IElementSerializable {

	/** A resolver for dynamic lookup of the action */
	private IActionRegistry resolver;

	/** The id of the action we are a proxy for */
	private String proxyId;

	public ActionProxy() {
	}

	public ActionProxy(IAction action) {
		super(action);
		this.proxyId = action.getId();
	}

	public ActionProxy(String proxyId, IActionRegistry resolver) {
		super(null);
		this.proxyId = proxyId;
		this.resolver = resolver;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setProxyId(element.attributeValue("id", null));
	}

	@Override
	public String getId() {
		return proxyId;
	}

	public String getProxyId() {
		return proxyId;
	}

	public IActionRegistry getResolver() {
		if (resolver == null) {
			return ActionRegistry.get();
		}
		return resolver;
	}

	@Override
	protected IAction resolve() {
		IAction resolved = getResolver().lookupAction(getId());
		if (resolved == null) {
			resolved = new ActionAdapter() {
				@Override
				public String getLabel() {
					return "<missing proxy action '" + getId() + "'>";
				}
			};
		}
		return resolved;
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		element.setAttributeValue("class", getClass().getName());
		element.setAttributeValue("id", getProxyId());
	}

	public void setProxyId(String proxyId) {
		this.proxyId = proxyId;
	}

	public void setResolver(IActionRegistry resolver) {
		this.resolver = resolver;
	}

}
