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
package de.intarsys.tools.converter;

import static de.intarsys.tools.converter.PACKAGE.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.provider.Providers;

/**
 * Standard {@link IConverterRegistry} implementation.
 */
public class StandardConverterRegistry implements IConverterRegistry {

	private final Map<Class, IConverter> converters = new HashMap<>();

	private boolean initialized;

	private boolean lookupProviders = true;

	@Override
	public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
		if (source == null) {
			return null;
		}
		if (targetType == Object.class) {
			// fast path
			return (T) source;
		}
		if (targetType.isInstance(source)) {
			return (T) source;
		}
		IConverter converter = lookupConverter(targetType);
		if (converter == null) {
			Object canonical = ConverterRegistry.get().convert(source, Canonical.class);
			if (canonical == source) {
				// break recursion
				throw new ConversionException("can't convert " + source.getClass().getName() + " to " + targetType);
			}
			try {
				return ConverterRegistry.get().convert(canonical, targetType);
			} catch (ConversionException e) {
				throw new ConversionException("can't convert " + source.getClass().getName() + " to " + targetType, e);
			}
		}
		return (T) converter.convert(source);
	}

	protected Iterator<IConverter> findProviders() {
		return Providers.get().lookupProviders(IConverter.class);
	}

	protected void init() {
		if (!isLookupProviders() || initialized) {
			return;
		}
		initialized = true;
		Iterator<IConverter> ps = findProviders();
		while (ps.hasNext()) {
			try {
				registerConverter(ps.next());
			} catch (Throwable t) {
				/*
				 * findProviders() filters classes that are not there, but
				 * register might still fail because of dependencies
				 */
				Log.warn("error creating converter ({})", ExceptionTools.getMessage(t));
			}
		}
	}

	public boolean isLookupProviders() {
		return lookupProviders;
	}

	@Override
	public synchronized IConverter lookupConverter(Class targetType) {
		init();
		return converters.get(targetType);
	}

	@Override
	public synchronized void registerConverter(IConverter converter) {
		IConverter dd = converters.computeIfAbsent(converter.getTargetType(), c -> new DoubleDispatchConverter(c));
		((DoubleDispatchConverter) dd).registerConverter(converter);
	}

	public void setLookupProviders(boolean lookupProviders) {
		this.lookupProviders = lookupProviders;
	}

	@Override
	public synchronized void unregisterConverter(IConverter converter) {
		IConverter tempConverter = converters.get(converter.getTargetType());
		if (tempConverter != null) {
			((DoubleDispatchConverter) tempConverter).unregisterConverter(converter);
		}
	}
}
