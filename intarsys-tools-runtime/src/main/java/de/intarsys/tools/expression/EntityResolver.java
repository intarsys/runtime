/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.expression;

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IStringEvaluator} that allows "entity coded" literals.
 * 
 * This is for example useful when encoding a string in a browser environment
 * where lots of characters are reserved or simply not accessible.
 * 
 * The following syntax is supported:
 * <ul>
 * <li>x+Digits &ndash; A hexadecimal encoded character</li>
 * <li>o+Digits &ndash; A octal encoded character</li>
 * <li>Digits &ndash; A decimal encoded character</li>
 * <li>All entries in entities map &ndash; The mapped String</li>
 * </ul>
 * 
 */
public class EntityResolver extends ContainerResolver {

	private static final Map<String, String> ENTITIES = new HashMap<>();

	static {
		ENTITIES.put("amp", "&");
		ENTITIES.put("gt", ">");
		ENTITIES.put("lt", "<");
		ENTITIES.put("copy", "\u00A9");
		ENTITIES.put("trade", "\u2122");
		ENTITIES.put("cr", "\r");
		ENTITIES.put("lf", "\n");
		ENTITIES.put("quot", "\"");
		ENTITIES.put("squot", "'");
		ENTITIES.put("slash", "/");
		ENTITIES.put("backslash", "\\");
		ENTITIES.put("nl", StringTools.LS);
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs args) throws EvaluationException {
		try {
			if (expression.startsWith("x")) {
				String code = expression.substring(1);
				int index = Integer.parseInt(code, 16);
				return new String(Character.toChars(index));
			} else if (expression.startsWith("o")) {
				String code = expression.substring(1);
				int index = Integer.parseInt(code, 8);
				return new String(Character.toChars(index));
			} else {
				int index = Integer.parseInt(expression);
				return new String(Character.toChars(index));
			}
		} catch (Exception e) {
			String value = ENTITIES.get(expression);
			if (value == null) {
				return notFound(expression);
			}
			return value;
		}
	}

}
