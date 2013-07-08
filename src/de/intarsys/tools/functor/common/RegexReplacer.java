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
package de.intarsys.tools.functor.common;

import java.util.Iterator;
import java.util.regex.PatternSyntaxException;

import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IFunctor} to replace substrings.
 * <p>
 * The {@link IFunctor} assumes the first argument to be the string to be
 * replaced.
 * <p>
 * The substrings to be replaced are defined using regular expressions. The list
 * of replacements to be made is provided in the configuration element using the
 * child elements with the name <code>replace</code>. The <code>regex</code>
 * attribute defines the regular expression. The <code>replacement</code>
 * attribute defines the replacement string.
 * 
 */
public class RegexReplacer extends ConfigurableFunctor {

	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		String value = StringTools.safeString(call.getArgs().get(0));
		try {
			if (getElement() != null) {
				Iterator<IElement> it = getElement().elementIterator("replace"); //$NON-NLS-1$
				while (it.hasNext()) {
					IElement replaceElement = it.next();
					String regex = replaceElement.attributeValue("regex", null); //$NON-NLS-1$
					String replacement = replaceElement
							.attributeValue("replacement", null); //$NON-NLS-1$
					if (regex != null && replacement != null) {
						value = value.replaceAll(regex, replacement);
					}
				}
			}
		} catch (PatternSyntaxException e) {
			throw new FunctorInvocationException(e);
		}
		return value;
	}

}
