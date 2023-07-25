///*
// * Copyright (c) 2007, intarsys GmbH
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * - Redistributions of source code must retain the above copyright notice,
// *   this list of conditions and the following disclaimer.
// *
// * - Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * - Neither the name of intarsys nor the names of its contributors may be used
// *   to endorse or promote products derived from this software without specific
// *   prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
package de.intarsys.tools.message;

import java.util.Set;

/**
 * Abstraction of an entity that can resolve codes to messages.
 * 
 */
public interface IMessageBundle {

	/**
	 * Format pattern using args.
	 * 
	 * If you want to handle preformatted object arguments, think of using
	 * {@link FormattedObject}.
	 * 
	 * @return The expanded / formatted pattern value.
	 */
	public String format(String pattern, Object... args);

	/**
	 * A {@link Set} of all codes that are contained in this bundle.
	 * 
	 * @return
	 */
	public Set<String> getCodes();

	/**
	 * The {@link IMessage} object for the code. This will always return an
	 * {@link IMessage} object, regardless if a mapping for code is defined.
	 * 
	 * If you want to handle preformatted object arguments, think of using
	 * {@link FormattedObject}.
	 * 
	 * To test for code mapping presence, use {@link #getPattern(String)} or
	 * {@link IMessage#getPattern()}.
	 * 
	 * @param code
	 * @return
	 */
	public IMessage getMessage(String code, Object... args);

	/**
	 * The fully qualified name of the message bundle.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The unexpanded/unformatted and translated string value.
	 * 
	 * This will return "null" if no mapping is defined for code.
	 * 
	 * This is a shortcut for {@link #getMessage(String, Object...)}{@code .getPattern()}
	 * 
	 * @return
	 */
	public String getPattern(String code);

	/**
	 * The expanded/formatted and translated string value. This will always
	 * return a {@link String} representation, regardless if a mapping for code
	 * is defined. If no mapping is present, a default {@link String} is
	 * constructed.
	 * 
	 * If you want to handle preformatted object arguments, think of using
	 * {@link FormattedObject}.
	 * 
	 * To test for code mapping presence, use {@link #getPattern(String)} or
	 * {@link IMessage#getPattern()}.
	 * 
	 * This is a shortcut for {@link #getMessage(String, Object...)}{@code .getString()}
	 * 
	 * @return
	 */
	public String getString(String code, Object... args);

}
