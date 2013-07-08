/*
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
package de.intarsys.tools.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.system.SystemTools;

/**
 * An {@link IStringEvaluator} implementation giving access common system state.
 */
public class SystemResolver implements IStringEvaluator {

	private static int COUNTER = 0;

	private static long UNIQUETIME = 0;

	final private static Map<String, Integer> counters = new HashMap<String, Integer>();

	private final static PropertiesResolver PropertiesResolver = new PropertiesResolver();

	public SystemResolver() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.expression.IStringEvaluator#evaluate(java.lang.String,
	 * de.intarsys.tools.functor.IArgs)
	 */
	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		if (expression != null) {
			if ("millis".equals(expression) || "time".equals(expression)) { //$NON-NLS-1$
				return System.currentTimeMillis();
			}
			if ("uniquemillis".equals(expression) || "uniquetime".equals(expression)) { //$NON-NLS-1$
				synchronized (SystemResolver.class) {
					long temp = System.currentTimeMillis();
					if (temp <= UNIQUETIME) {
						temp = UNIQUETIME + 1;
					}
					UNIQUETIME = temp;
					return temp;
				}
			}
			if ("uuid".equals(expression)) { //$NON-NLS-1$
				return UUID.randomUUID().toString();
			}
			if ("counter".equals(expression)) { //$NON-NLS-1$
				synchronized (SystemResolver.class) {
					return COUNTER++;
				}
			}
			if (expression.startsWith("counters.")) { //$NON-NLS-1$
				String[] tempStrings = expression.split("\\.", 2); //$NON-NLS-1$
				if (tempStrings.length == 2) {
					synchronized (SystemResolver.class) {
						Integer count = counters.get(tempStrings[1]);
						if (count == null) {
							count = 0;
						}
						counters.put(tempStrings[1], count + 1);
						return count;
					}
				}
			}
			if (expression.startsWith("getenv.")) { //$NON-NLS-1$
				String[] tempStrings = expression.split("\\.", 2); //$NON-NLS-1$
				if (tempStrings.length == 2) {
					return System.getenv(tempStrings[1]);
				}
			}
			if (expression.startsWith("properties.")) { //$NON-NLS-1$
				String[] tempStrings = expression.split("\\.", 2); //$NON-NLS-1$
				if (tempStrings.length == 2) {
					return PropertiesResolver.evaluate(tempStrings[1], args);
				} else {
					return null;
				}
			}
			if ("architecture".equals(expression)) { //$NON-NLS-1$
				return getArchitecture();
			}
		}
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected String getArchitecture() {
		String arch = SystemTools.getOSArch();
		if (arch != null && arch.indexOf("64") > -1) {
			return "64-bit";
		}
		return "32-bit";
	}
}
