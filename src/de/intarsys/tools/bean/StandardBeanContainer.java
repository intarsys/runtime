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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The {@link IBeanContainer} standard implementation
 * 
 */
public class StandardBeanContainer implements IBeanContainer {

	final private Map<String, Object> beanMap = new HashMap<String, Object>();

	final private List<Object> beanList = new ArrayList<Object>();

	private static final Logger Log = PACKAGE.Log;

	@Override
	synchronized public <T> T lookupBean(String id, Class<T> expectedClass) {
		Object bean = beanMap.get(id);
		if (bean instanceof IBeanProxy) {
			IBeanProxy lazy = (IBeanProxy) bean;
			return (T) lazy.getObject();
		}
		return (T) bean;
	}

	@Override
	synchronized public <T> List<T> lookupBeans(Class<T> expectedClass) {
		List<T> beans = new ArrayList<T>(5);
		for (Object bean : beanList) {
			if (bean instanceof IBeanProxy) {
				IBeanProxy lazy = (IBeanProxy) bean;
				Object object = lazy.getObject();
				if (expectedClass.isInstance(object)) {
					beans.add((T) object);
				}
			} else if (expectedClass.isInstance(bean)) {
				beans.add((T) bean);
			}
		}
		return beans;
	}

	@Override
	synchronized public void registerBean(String id, Object object) {
		beanList.add(object);
		if (id != null) {
			beanMap.put(id, object);
		}
	}

	@Override
	synchronized public void unregisterBean(String id) {
		Object bean = beanMap.remove(id);
		beanList.remove(bean);
	}

}
