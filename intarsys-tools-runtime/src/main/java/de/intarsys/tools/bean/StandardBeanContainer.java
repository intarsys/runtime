/*
 * Copyright (c) 2012, intarsys GmbH
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import de.intarsys.tools.collection.ReverseListIterator;
import de.intarsys.tools.proxy.IProxy;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * The {@link IBeanContainer} standard implementation
 * 
 */
public class StandardBeanContainer implements IBeanContainer {

	private static final ILogger Log = PACKAGE.Log;

	private final Map<String, Object> beansById = new HashMap<>(500);

	private final List<Object> beanList = new ArrayList<>(1000);

	private final Map<String, List> beansByRole = new HashMap<>(100);

	protected void addBeanToRole(String role, Object bean) {
		List beans = beansByRole.computeIfAbsent(role, (key) -> new ArrayList<>());
		beans.add(bean);
	}

	@Override
	public synchronized <T> T lookupBean(Class<T> expectedClass) {
		for (Object object : beanList) {
			if (expectedClass.isInstance(object)) {
				return (T) realize(object);
			}
		}
		return null;
	}

	@Override
	public synchronized <T> T lookupBean(String id, Class<T> expectedClass) {
		Object bean = beansById.get(id);
		return (T) realize(bean);
	}

	@Override
	public synchronized <T> List<T> lookupBeans(Class<T> expectedClass) {
		List<T> result = new ArrayList<>();
		for (Object object : beanList) {
			if (expectedClass.isInstance(object)) {
				result.add((T) realize(object));
			}
		}
		return result;
	}

	@Override
	public synchronized <T> List<T> lookupBeans(String role, Class<T> expectedClass) {
		List beans = beansByRole.get(role);
		if (beans == null) {
			return new ArrayList<>();
		}
		int i = 0;
		while (i < beans.size()) {
			beans.set(i, realize(beans.get(i)));
			i++;
		}
		return beans;
	}

	protected Object realize(Object bean) {
		if (bean instanceof IProxy) {
			return ((IProxy) bean).getRealized();
		}
		return bean;
	}

	@Override
	public synchronized void registerBean(String id, String role, Object object) {
		beanList.add(object);
		if (id != null) {
			beansById.put(id, object);
		}
		if (role == null) {
			Class clazz = object.getClass();
			BeanComponent beanRole = (BeanComponent) clazz.getAnnotation(BeanComponent.class);
			if (beanRole != null) {
				role = beanRole.role();
			}
		}
		if (role != null) {
			addBeanToRole(role, object);
		}
	}

	@Override
	public synchronized void shutdown() {
		List beans = new ArrayList<>(beanList);
		Iterator<Object> it = new ReverseListIterator<>(beans);
		while (it.hasNext()) {
			shutdown(it.next());
		}
	}

	protected void shutdown(Object object) {
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(PreDestroy.class) != null) {
				try {
					method.invoke(object);
				} catch (Exception e) {
					Log.log(Level.WARN, "exception when shutting down", e);
				}
			}
		}
	}

	@Override
	public synchronized void unregisterBean(String id) {
		Object bean = beansById.remove(id);
		beanList.remove(bean);
		for (List roleBeanList : beansByRole.values()) {
			roleBeanList.remove(bean);
		}
	}

}
