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
package de.intarsys.tools.expression;

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IStringEvaluator} that allows "entity coded" literals.
 * 
 * This is for example useful when encoding a string in a browser environment
 * where lots of characters are reserved or simply not accessible (newline in
 * applet parameters).
 * 
 * The following syntax is supported:
 * <ul>
 * <li>x+Digits</li> A hexadecimal encoded character
 * <li>o+Digits</li> A octal encoded character
 * <li>Digits</li> A decimal encoded character
 * <li>All entries in entities map</li> The mapped String
 * </ul>
 * 
 */
public class EntityResolver extends ContainerResolver {

	static private Map<String, String> entities = new HashMap<String, String>();

	static {
		entities.put("amp", "&");
		entities.put("gt", ">");
		entities.put("lt", "<");
		entities.put("copy", "\u00A9");
		entities.put("trade", "\u2122");
		entities.put("cr", "\r");
		entities.put("lf", "\n");
		entities.put("quot", "\"");
		entities.put("squot", "'");
		entities.put("slash", "/");
		entities.put("backslash", "\\");
		entities.put("nl", StringTools.LS);
	}

	public EntityResolver() {
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs args)
			throws EvaluationException {
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
			return entities.get(expression);
		}
	}

}
