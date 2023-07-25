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

import java.util.List;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A container for bean POJO's
 */
@ServiceImplementation(StandardBeanContainer.class)
public interface IBeanContainer {

	String BEAN_ROLE_LIFECYCLE = "container.lifecycle";

	/**
	 * Lookup a bean implementing the expected class.
	 * 
	 * @param <T>
	 * @param expectedClass
	 * @return
	 */
	public <T> T lookupBean(Class<T> expectedClass);

	/**
	 * Lookup a bean with the specified id.
	 * 
	 * @param <T>
	 * @param id
	 * @param expectedClass
	 * @return
	 */
	public <T> T lookupBean(String id, Class<T> expectedClass);

	/**
	 * Lookup all beans implementing the expected class.
	 * 
	 * @param <T>
	 * @param expectedClass
	 * @return
	 */
	public <T> List<T> lookupBeans(Class<T> expectedClass);

	/**
	 * Lookup all beans with the specified role.
	 * 
	 * @param <T>
	 * @param role
	 * @param expectedClass
	 * @return
	 */
	public <T> List<T> lookupBeans(String role, Class<T> expectedClass);

	/**
	 * Register a new bean.
	 * 
	 * @param id
	 * @param role
	 * @param object
	 */
	public void registerBean(String id, String role, Object object);

	/**
	 * Shutdown the container.
	 * 
	 */
	public void shutdown();

	/**
	 * Remove the bean with the specified id.
	 * 
	 * @param id
	 */
	public void unregisterBean(String id);

}
