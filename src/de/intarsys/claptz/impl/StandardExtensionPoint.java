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
import java.util.List;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.IExtensionPoint;
import de.intarsys.claptz.IExtensionPointHandler;
import de.intarsys.claptz.IInstrument;
import de.intarsys.claptz.InstrumentRegistryException;
import de.intarsys.tools.attribute.AttributeMap;

/**
 * A concrete implementation for an {@link IExtensionPoint}.
 */
public class StandardExtensionPoint {

	public class Facade implements IExtensionPoint {

		public Object getAttribute(Object key) {
			return StandardExtensionPoint.this.getAttribute(key);
		}

		public IExtension[] getExtensions() {
			return StandardExtensionPoint.this.getExtensionFacades();
		}

		public String getId() {
			return StandardExtensionPoint.this.getId();
		}

		public IExtensionPointHandler getInstaller() {
			return StandardExtensionPoint.this.getInstaller();
		}

		protected StandardExtensionPoint getOwner() {
			return StandardExtensionPoint.this;
		}

		public IInstrument getProvider() {
			return StandardExtensionPoint.this.getProvider();
		}

		public Object removeAttribute(Object key) {
			return StandardExtensionPoint.this.removeAttribute(key);
		}

		public Object setAttribute(Object key, Object value) {
			return StandardExtensionPoint.this.setAttribute(key, value);
		}
	}

	public static final Object ATTR_EXTENSIONS = new Object();

	final private Facade facade = new Facade();

	/**
	 * Generic attributes
	 */
	final private AttributeMap attributes = new AttributeMap();

	private List<StandardExtension> extensions = new ArrayList<>();

	final private String id;

	private IExtensionPointHandler installer;

	final private IInstrument provider;

	/**
	 * 
	 */
	public StandardExtensionPoint(IInstrument provider, String id) {
		this.provider = provider;
		this.id = id;
	}

	protected Object getAttribute(Object key) {
		return attributes.get(key);
	}

	protected IExtension[] getExtensionFacades() {
		IExtension[] result = new IExtension[extensions.size()];
		int i = 0;
		for (StandardExtension current : extensions) {
			result[i] = current.getFacade();
			i++;
		}
		return result;
	}

	public Facade getFacade() {
		return facade;
	}

	public String getId() {
		return id;
	}

	public IExtensionPointHandler getInstaller() {
		return installer;
	}

	public IInstrument getProvider() {
		return provider;
	}

	/**
	 * Add a new {@link IExtension} to the receiver.
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 */
	protected void registerExtension(StandardExtension extension)
			throws InstrumentRegistryException {
		synchronized (this) {
			extensions.add(extension);
			extension.setExtensionPoint(this);
		}
		if (getInstaller() != null && getInstaller().isActive()) {
			getInstaller().install(extension.getFacade());
		}
	}

	protected Object removeAttribute(Object key) {
		return attributes.remove(key);
	}

	protected Object setAttribute(Object key, Object value) {
		return attributes.put(key, value);
	}

	public void setInstaller(IExtensionPointHandler installer) {
		this.installer = installer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return "IExtensionPoint '" + getId() + "' by " + getProvider();
		} catch (RuntimeException e) {
			return "<unprintable extension point>";
		}
	}

	/**
	 * Remove an already related {@link StandardExtension} from the receiver.
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 */
	protected void unregisterExtension(StandardExtension extension)
			throws InstrumentRegistryException {
		synchronized (this) {
			extensions.remove(extension);
		}
		if (getInstaller() != null && getInstaller().isActive()) {
			getInstaller().uninstall(extension.getFacade());
		}
	}

}
