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
import java.util.Map;

/**
 * Double dispatch converter.
 * <p>
 * This is the result of the first stage in the conversion process. A
 * {@link IConverter} that bundles all {@link IConverter} instances that can
 * create a dedicated target type. Amongst the sub-converters the one is
 * selected that most closely supports the source object type.
 */
public class DoubleDispatchConverter implements IConverter<Object, Object> {

	final private Map<Class, IConverter> converters = new HashMap<Class, IConverter>();

	final private Class targetType;

	public DoubleDispatchConverter(Class targetType) {
		super();
		this.targetType = targetType;
	}

	public Object convert(Object source) throws ConversionException {
		IConverter converter = lookupConverter(source);
		if (converter == null) {
			if (targetType == Canonical.class) {
				// break recursion
				throw new ConversionException("can't convert "
						+ source.getClass().getName() + " to "
						+ getTargetType());
			}
			Object canonical = ConverterRegistry.get().convert(source,
					Canonical.class);
			if (canonical == source) {
				// break recursion
				throw new ConversionException("can't convert "
						+ source.getClass().getName() + " to "
						+ getTargetType());
			}
			return ConverterRegistry.get().convert(canonical, getTargetType());
		}
		return converter.convert(source);
	}

	public Class<?> getSourceType() {
		return Object.class;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

	private IConverter<?, ?> lookupConverter(Class<?> clazz) {
		IConverter<?, ?> result = converters.get(clazz);
		if (result != null) {
			return result;
		}
		java.lang.Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			result = lookupConverter(interfaces[i]);
			if (result != null) {
				return result;
			}
		}
		java.lang.Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			result = lookupConverter(superClass);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	protected IConverter<?, ?> lookupConverter(Object source) {
		Class<?> clazz = (source == null) ? Undefined.class : source.getClass();
		IConverter<?, ?> converter = lookupConverter(clazz);
		return converter;
	}

	public void registerConverter(IConverter converter) {
		converters.put(converter.getSourceType(), converter);
	}

	public void unregisterConverter(IConverter converter) {
		converters.remove(converter.getSourceType());
	}
}
