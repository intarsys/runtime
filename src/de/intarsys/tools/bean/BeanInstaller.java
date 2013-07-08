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
package de.intarsys.tools.bean;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.impl.ExtensionPointHandlerAdapter;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;

public class BeanInstaller extends ExtensionPointHandlerAdapter {

	private static final Logger Log = PACKAGE.Log;

	public static final String XP_BEANS = "de.intarsys.claptz.beans";

	public static final String XE_OBJECT = "object"; //$NON-NLS-1$

	public static final String XE_CLASS = "class"; //$NON-NLS-1$

	@Override
	protected Object basicInstallInsert(IExtension extension, IElement element) {
		if (XE_OBJECT.equals(element.getName())) {
			String onError = element.attributeValue("onerror", null);
			try {
				ClassLoader classLoader = extension.getProvider()
						.getClassLoader();
				Object bean;
				String id = element.attributeValue("bean-id", null);
				String create = element.attributeValue("bean-create", null);
				if ("lazy".equals(create)) {
					bean = new LazyBean(element, extension.getProvider());
				} else {
					bean = ElementTools.createObject(element, Object.class,
							extension.getProvider());
				}
				BeanContainer.get().registerBean(id, bean);
				String registryId = element.attributeValue("bean-registry",
						null);
				if (!StringTools.isEmpty(registryId)) {
					Object registry = BeanContainer.get().lookupBean(
							registryId, Object.class);
					if (registry == null) {
						throw new ObjectCreationException("registry '"
								+ registryId + "' not found");
					}
					if (bean instanceof IBeanProxy) {
						bean = ((IBeanProxy) bean).getObject();
					}
					ObjectTools.register(registry, bean);
				}
				return bean;
			} catch (Exception e) {
				if ("ignore".equals(onError)) {
					//
				} else if ("fail".equals(onError)) {
					throw new RuntimeException(e);
				} else {
					String msg = "error creating object";
					log(Log, Level.WARNING, extension, element, msg, e);
				}
			}
		} else if (XE_CLASS.equals(element.getName())) {
			String onError = element.attributeValue("onerror", null);
			try {
				Class bean = ElementTools.createClass(element, "name",
						Class.class, extension.getProvider());
				return bean;
			} catch (Exception e) {
				if ("ignore".equals(onError)) {
					//
				} else if ("fail".equals(onError)) {
					throw new RuntimeException(e);
				} else {
					String msg = "error creating object";
					log(Log, Level.WARNING, extension, element, msg, e);
				}
			}
		} else {
			return super.basicInstallInsert(extension, element);
		}
		return null;
	}
}
