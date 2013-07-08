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
package de.intarsys.claptz.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.IExtensionPoint;
import de.intarsys.claptz.IInstrument;
import de.intarsys.claptz.State;
import de.intarsys.claptz.StateNew;
import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.string.StringTools;

/**
 * a concrete implementation for an {@link IExtension}.
 */
public class StandardExtension {

	public class Facade implements IExtension {

		@Override
		public void attachDelete(Object target) {
			StandardExtension.this.attachObjectDelete(target);
		}

		@Override
		public void attachInsert(Object target) {
			StandardExtension.this.attachObjectInsert(target);
		}

		@Override
		public void attachUpdate(Object target) {
			StandardExtension.this.attachObjectUpdate(target);
		}

		public String getAbsentAction() {
			return StandardExtension.this.getAbsentAction();
		}

		@Override
		public List<ExtensionOperation> getOperations() {
			return StandardExtension.this.getOperations();
		}

		public Object getAttribute(Object key) {
			return StandardExtension.this.getAttribute(key);
		}

		public IElement getElement() {
			return StandardExtension.this.getElement();
		}

		public IExtensionPoint getExtensionPoint() {
			return StandardExtension.this.getExtensionPointFacade();
		}

		public String getExtensionPointId() {
			return StandardExtension.this.getExtensionPointId();
		}

		public String getId() {
			return StandardExtension.this.getId();
		}

		public String getIfdef() {
			return StandardExtension.this.getIfdef();
		}

		public String getIfnotdef() {
			return StandardExtension.this.getIfnotdef();
		}

		protected StandardExtension getOwner() {
			return StandardExtension.this;
		}

		public IInstrument getProvider() {
			return StandardExtension.this.getProvider().getFacade();
		}

		public Object removeAttribute(Object key) {
			return StandardExtension.this.removeAttribute(key);
		}

		public Object setAttribute(Object key, Object value) {
			return StandardExtension.this.setAttribute(key, value);
		}

	}

	final private Facade facade = new Facade();

	private String extensionPointId;

	private IElement element;

	final private StandardInstrument provider;

	private StandardExtensionPoint extensionPoint;

	private String id;

	private State state = new StateNew("");

	final private AttributeMap attributes = new AttributeMap();

	private String ifdef;

	private String ifnotdef;

	private String absentAction = null;

	private List<ExtensionOperation> operations = new ArrayList<>();

	public StandardExtension(StandardInstrument provider,
			String extensionPointId, String id) {
		if (extensionPointId == null) {
			throw new NullPointerException("extensionPointId can't be null");
		}
		this.provider = provider;
		this.extensionPointId = extensionPointId;
		this.id = id;
	}

	/**
	 * Attach a delete operation regarding <code>target</code> to
	 * <code>targetExtension</code>.
	 * <p>
	 * The "attach*" flavor methods record along with the {@link IExtension}
	 * which objects are manipulated in its declarations. The registered
	 * {@link ExtensionOperation} instances can be looked up using
	 * "getAttachments".
	 * 
	 * @param targetExtension
	 * @param target
	 */
	public void attachObjectDelete(Object target) {
		ExtensionOperation tempOperation = findOperation(target);
		if (tempOperation == null) {
			operations.add(new ExtensionObjectDelete(target));
		} else if (tempOperation.isDelete()) {
			// nothing to do, leave delete in place
		} else if (tempOperation.isInsert()) {
			operations.remove(tempOperation);
		} else if (tempOperation.isUpdate()) {
			operations.remove(tempOperation);
			operations.add(new ExtensionObjectDelete(target));
		}
	}

	public void attachObjectInsert(Object target) {
		ExtensionOperation tempOperation = findOperation(target);
		if (tempOperation == null) {
			operations.add(new ExtensionObjectInsert(target));
		} else if (tempOperation.isDelete()) {
			operations.remove(tempOperation);
			operations.add(new ExtensionObjectInsert(target));
		} else if (tempOperation.isInsert()) {
			// nothing to do, leave insert in place
		} else if (tempOperation.isUpdate()) {
			// strange
			operations.remove(tempOperation);
			operations.add(new ExtensionObjectInsert(target));
		}
	}

	public void attachObjectUpdate(Object target) {
		ExtensionOperation tempOperation = findOperation(target);
		if (tempOperation == null) {
			operations.add(new ExtensionObjectUpdate(target));
		} else if (tempOperation.isDelete()) {
			// strange
			operations.remove(tempOperation);
			operations.add(new ExtensionObjectUpdate(target));
		} else if (tempOperation.isInsert()) {
			// nothing to do, leave insert in place
		} else if (tempOperation.isUpdate()) {
			// nothing to do, leave update in place
		}
	}

	protected ExtensionOperation findOperation(Object target) {
		for (Iterator<ExtensionOperation> it = getOperations().iterator(); it
				.hasNext();) {
			ExtensionOperation extensionOperation = it.next();
			if (extensionOperation.getObject() == target) {
				return extensionOperation;
			}
		}
		return null;
	}

	public String getAbsentAction() {
		return absentAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.component.IAttributeSupport#getAttribute(java.lang.
	 * Object)
	 */
	final public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	public IElement getElement() {
		return element;
	}

	protected StandardExtensionPoint getExtensionPoint() {
		return extensionPoint;
	}

	protected IExtensionPoint getExtensionPointFacade() {
		return extensionPoint == null ? null : extensionPoint.getFacade();
	}

	public String getExtensionPointId() {
		return extensionPointId;
	}

	public Facade getFacade() {
		return facade;
	}

	public String getId() {
		return id;
	}

	public String getIfdef() {
		return ifdef;
	}

	public String getIfnotdef() {
		return ifnotdef;
	}

	public List<ExtensionOperation> getOperations() {
		return operations;
	}

	public StandardInstrument getProvider() {
		return provider;
	}

	public State getState() {
		return state;
	}

	final public Object removeAttribute(Object key) {
		return attributes.remove(key);
	}

	public void setAbsentAction(String absentAction) {
		this.absentAction = absentAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.component.IAttributeSupport#setAttribute(java.lang.
	 * Object, java.lang.Object)
	 */
	final public Object setAttribute(Object key, Object value) {
		return attributes.put(key, value);
	}

	public void setElement(IElement element) {
		this.element = element;
	}

	protected void setExtensionPoint(StandardExtensionPoint extensionPoint) {
		this.extensionPoint = extensionPoint;
	}

	public void setIfdef(String ifdef) {
		this.ifdef = ifdef;
	}

	public void setIfnotdef(String ifnotdef) {
		this.ifnotdef = ifnotdef;
	}

	public void setState(State state) {
		this.state = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return "IExtension to '" + getExtensionPointId() + "' by "
					+ StringTools.safeString(getProvider());
		} catch (RuntimeException e) {
			return "<unprintable IExtension>";
		}
	}
}
