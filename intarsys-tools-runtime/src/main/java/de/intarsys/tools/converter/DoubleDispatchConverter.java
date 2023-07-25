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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

	private final Map<Class, List<IConverter>> registeredConverters = new HashMap<>();

	private final Map<Class, List<IConverter>> cachedConverters = new HashMap<>();

	private final Class targetType;

	public DoubleDispatchConverter(Class targetType) {
		super();
		this.targetType = targetType;
	}

	protected List<IConverter> computeConverters(Class clazz, ArrayList list) {
		List<IConverter> result = registeredConverters.get(clazz);
		if (result != null) {
			list.addAll(result);
		}
		java.lang.Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			list.addAll(lookupConverters(interfaces[i]));
		}
		java.lang.Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			list.addAll(lookupConverters(superClass));
		}
		list.sort(new Comparator<IConverter>() {
			@Override
			public int compare(IConverter o1, IConverter o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
		return list;
	}

	@Override
	public Object convert(Object source) throws ConversionException {
		List<IConverter> converters = lookupConverters(source);
		for (IConverter converter : converters) {
			Object result = converter.convert(source);
			if (result != null) {
				return result;
			}
		}
		if (converters.isEmpty()) {
			if (targetType == Canonical.class) {
				// break recursion
				throw new ConversionException(
						"can't convert " + source.getClass().getName() + " to " + getTargetType());
			}
			Object canonical = ConverterRegistry.get().convert(source, Canonical.class);
			if (canonical == source) {
				// break recursion
				throw new ConversionException(
						"can't convert " + source.getClass().getName() + " to " + getTargetType());
			}
			return ConverterRegistry.get().convert(canonical, getTargetType());
		}
		return null;
	}

	@Override
	public Class<?> getSourceType() {
		return Object.class;
	}

	@Override
	public Class<?> getTargetType() {
		return targetType;
	}

	private List<IConverter> lookupConverters(Class<?> clazz) {
		List<IConverter> result = cachedConverters.get(clazz); // NOSONAR recursive!
		if (result == null) {
			result = computeConverters(clazz, new ArrayList<>());
			cachedConverters.put(clazz, result);
		}
		return result;
	}

	protected List<IConverter> lookupConverters(Object source) {
		Class<?> clazz = (source == null) ? Undefined.class : source.getClass();
		return lookupConverters(clazz);
	}

	public void registerConverter(IConverter converter) {
		registeredConverters.computeIfAbsent(converter.getSourceType(), c -> new ArrayList<>()).add(converter);
	}

	public void unregisterConverter(IConverter converter) {
		registeredConverters.computeIfAbsent(converter.getSourceType(), c -> new ArrayList<>()).remove(converter);
	}
}
