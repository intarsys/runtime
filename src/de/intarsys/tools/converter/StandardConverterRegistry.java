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
package de.intarsys.tools.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.provider.Providers;

/**
 * Standard {@link IConverterRegistry} implementation.
 */
public class StandardConverterRegistry implements IConverterRegistry {

	private static final Logger Log = PACKAGE.Log;

	private final Map<Class, IConverter> converters = new HashMap<Class, IConverter>();

	private boolean initialized = false;

	private boolean lookupProviders = true;

	public Object convert(Object source, Class targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}
		if (targetType == Object.class) {
			// fast path
			return source;
		}
		if (targetType.isInstance(source)) {
			return source;
		}
		IConverter converter = lookupConverter(targetType);
		if (converter == null) {
			Object canonical = ConverterRegistry.get().convert(source,
					Canonical.class);
			if (canonical == source) {
				// break recursion
				throw new ConversionException("can't convert "
						+ source.getClass().getName() + " to " + targetType);
			}
			try {
				return ConverterRegistry.get().convert(canonical, targetType);
			} catch (ConversionException e) {
				throw new ConversionException("can't convert "
						+ source.getClass().getName() + " to " + targetType, e);
			}
		}
		return converter.convert(source);
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
			} catch (Throwable e) {
				Log.log(Level.WARNING, "error creating converter", e);
			}
		}
	}

	public boolean isLookupProviders() {
		return lookupProviders;
	}

	synchronized public IConverter lookupConverter(Class targetType) {
		init();
		return converters.get(targetType);
	}

	synchronized public void registerConverter(IConverter converter) {
		IConverter tempConverter = converters.get(converter.getTargetType());
		if (tempConverter == null) {
			tempConverter = new DoubleDispatchConverter(
					converter.getTargetType());
			((DoubleDispatchConverter) tempConverter)
					.registerConverter(converter);
			converters.put(tempConverter.getTargetType(), tempConverter);
		} else {
			((DoubleDispatchConverter) tempConverter)
					.registerConverter(converter);
		}
	}

	public void setLookupProviders(boolean lookupProviders) {
		this.lookupProviders = lookupProviders;
	}

	synchronized public void unregisterConverter(IConverter converter) {
		IConverter tempConverter = converters.get(converter.getTargetType());
		if (tempConverter != null) {
			((DoubleDispatchConverter) tempConverter)
					.unregisterConverter(converter);
		}
	}
}
