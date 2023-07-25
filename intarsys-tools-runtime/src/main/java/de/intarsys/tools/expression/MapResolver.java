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

import javax.annotation.PostConstruct;

import de.intarsys.tools.functor.IArgs;

/**
 * An {@link IStringEvaluator} that looks up a variable in a simple Map.
 * <p>
 * Only named (associative) lookup is supported.
 */
public class MapResolver extends ContainerResolver {

	public static class Install {

		private MapResolver target;

		private IStringEvaluator evaluator;

		private String name;

		public IStringEvaluator getEvaluator() {
			return evaluator;
		}

		public String getName() {
			return name;
		}

		public MapResolver getTarget() {
			return target;
		}

		@PostConstruct
		public void install() {
			getTarget().put(getName(), getEvaluator());
		}

		public void setEvaluator(IStringEvaluator evaluator) {
			this.evaluator = evaluator;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setTarget(MapResolver target) {
			this.target = target;
		}
	}

	public static MapResolver create() {
		return new MapResolver();
	}

	public static MapResolver create(String key, IStringEvaluator resolver) {
		return new MapResolver().put(key, resolver);
	}

	public static MapResolver createStrict() {
		return new MapResolver(true); // NOSONAR
	}

	public static MapResolver createStrict(Map<String, Object> variables) {
		return new MapResolver(variables, true); // NOSONAR
	}

	public static MapResolver createStrict(String key, IStringEvaluator resolver) {
		return MapResolver.createStrict().put(key, resolver);
	}

	private final Map<String, Object> variables;

	public MapResolver() {
		this(new HashMap<>(), false); // NOSONAR
	}

	/**
	 * @deprecated Use {@link MapResolver#createStrict()}, which clearly communicates the intent.
	 */
	@Deprecated(since = "4.24")
	public MapResolver(boolean strict) {
		this(new HashMap<>(), strict); // NOSONAR
	}

	public MapResolver(Map<String, Object> variables) {
		this(variables, false); // NOSONAR
	}

	/**
	 * @deprecated Use {@link MapResolver#createStrict(Map)}, which clearly communicates the intent.
	 */
	@Deprecated(since = "4.24")
	public MapResolver(Map<String, Object> variables, boolean strict) {
		super(PATH_SEPARATOR, strict, null);
		this.variables = new HashMap<>(variables);
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs args) throws EvaluationException {
		if (variables.containsKey(expression)) {
			return variables.get(expression);
		}
		return notFound(expression);
	}

	public Object get(String key) {
		return variables.get(key);
	}

	public MapResolver put(String key, Object value) {
		variables.put(key, value);
		return this;
	}
}
