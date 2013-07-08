/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.range;

import java.io.IOException;
import java.util.List;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.number.NumberWrapper;
import de.intarsys.tools.string.StringTools;

public class RangeTools {

	public static IRange getRange(IArgs args, String argName,
			IRange defaultValue) {
		Object optionValue = args.get(argName);
		if (optionValue == null) {
			return defaultValue;
		}
		return toRange(optionValue);
	}

	public static IRange toRange(Object object) {
		if (object instanceof IRange) {
			return (IRange) object;
		} else if (object instanceof NumberWrapper) {
			return new DefinedRange((NumberWrapper) object);
		} else if (object instanceof Integer) {
			return DefinedRange.create((Integer) object);
		} else if (object instanceof List) {
			return DefinedRange.create((List) object);
		} else if (object instanceof String) {
			String stringValue = (String) object;
			if (StringTools.isNumeric(stringValue)) {
				try {
					return DefinedRange.create(stringValue);
				} catch (IOException e) {
					throw new IllegalArgumentException("range");
				}
			} else {
				EnumRange rangeItem = (EnumRange) EnumRange.META
						.getItemOrDefault(stringValue);
				return new NamedRange(rangeItem);
			}
		}
		throw new IllegalArgumentException("range");
	}

}
