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
package de.intarsys.tools.functor;

import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;

/**
 * An enumeration of the supported styles for marshalling return values.
 * <p>
 * Supported styles are {@link EnumStyle#LITERAL} to literally include the whole
 * object in the return value (using an appropriate serializing)and
 * {@link EnumStyle#REFERENCE } to indicate the object is returned by reference.
 */
public class EnumStyle extends EnumItem {

	public static final String ARG_STYLE = "style";

	/**
	 * The meta data for the enumeration.
	 */
	public static final EnumMeta META = getMeta(EnumStyle.class);

	public static final EnumStyle LITERAL = new EnumStyle("literal");

	public static final EnumStyle REFERENCE = new EnumStyle("reference");

	public static final EnumStyle NAME = new EnumStyle("name");

	static {
		LITERAL.setDefault();
	}

	public static EnumStyle get(IArgs args) {
		return (EnumStyle) ArgTools.getEnumItemStrict(args, EnumStyle.META, ARG_STYLE);
	}

	/**
	 * 
	 */
	public EnumStyle(String id) {
		super(id);
	}

}
