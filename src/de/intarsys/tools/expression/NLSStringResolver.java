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
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.message.MessageBundleTools;

/**
 * An {@link IStringEvaluator} accessing message strings in a expression defined
 * package
 * 
 * The expression is mapped against a bundle name, looked up via the class
 * loader. The message itself is separated with a "#" sign, as the bundle uses
 * "." separated package navigation.
 * 
 * This one returns a {@link MessageBundleStringResolver} object!
 */
public class NLSStringResolver extends ContainerResolver {

	final private ClassLoader classLoader;

	private Map<String, IStringEvaluator> resolvers = new HashMap<String, IStringEvaluator>();

	public NLSStringResolver() {
		this(NLSStringResolver.class.getClassLoader());
	}

	public NLSStringResolver(ClassLoader classLoader) {
		super('#');
		this.classLoader = classLoader;
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs args)
			throws EvaluationException {
		IStringEvaluator evaluator = resolvers.get(expression);
		if (evaluator == null) {
			MessageBundle bundle = MessageBundleTools.getMessageBundle(
					expression, getClassLoader());
			evaluator = new MessageBundleStringResolver(bundle);
			resolvers.put(expression, evaluator);
		}
		return evaluator;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
