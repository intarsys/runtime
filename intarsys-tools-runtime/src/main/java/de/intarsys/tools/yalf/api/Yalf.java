/*
 * Copyright (c) 2014, intarsys GmbH
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
package de.intarsys.tools.yalf.api;

import de.intarsys.tools.component.Singleton;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.common.LogTools;
import de.intarsys.tools.yalf.jul.JulProvider;

/**
 * VM singleton for accessing the active YALF.
 * 
 */
@Singleton
public final class Yalf {

	private static IYalfProvider<?> Active;

	static {
		ObjectCreationException providerException = null;
		try {
			Active = createSystemProvider();
		} catch (ObjectCreationException e) {
			providerException = e;
		}
		if (Active == null) {
			Active = createDefaultProvider();
		}

		ILogger log = LogTools.getLogger(Yalf.class);
		if (providerException != null) {
			log.warn("Installation of log provider failed.", providerException);
		}
		log.info("Yalf implementation is {}", Active.getClass());
	}

	protected static IYalfProvider<?> createDefaultProvider() {
		IYalfProvider<?> provider;
		try {
			provider = createObject("de.intarsys.tools.yalf.logback.LogbackProvider", IYalfProvider.class);
		} catch (ObjectCreationException e) {
			try {
				provider = createObject("de.intarsys.tools.yalf.slf4j.Slf4jProvider", IYalfProvider.class);
			} catch (ObjectCreationException nested) {
				provider = new JulProvider();
			}
		}
		return provider;
	}

	public static <T> T createObject(String className, Class<T> clazz) throws ObjectCreationException { // NOSONAR
		/*
		 * do not use tool class here, -> recursion
		 */
		try {
			return (T) Class.forName(className).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
	}

	protected static IYalfProvider<?> createSystemProvider() throws ObjectCreationException {
		String providerClass = System.getProperty("de.intarsys.yalf.provider");
		if (StringTools.isEmpty(providerClass)) {
			return null;
		}
		return createObject(providerClass, IYalfProvider.class);
	}

	public static IYalfProvider<?> get() {
		return Active;
	}

	public static void set(IYalfProvider<?> component) {
		Active = component;
	}

	private Yalf() {
	}
}
