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
package de.intarsys.tools.infoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * A factory for the infoset abstraction primitives.
 * 
 */
public interface IElementFactory {

	/**
	 * Create a new {@link IDocument}.
	 * 
	 * @return
	 */
	public IDocument createDocument();

	/**
	 * Create a new {@link IElement}
	 * 
	 * @param name
	 * @return
	 */
	public IElement createElement(String name);

	/**
	 * Parse an {@link IDocument} from an {@link InputStream}.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public IDocument parse(InputStream is) throws IOException;

	/**
	 * Parse an {@link IDocument} from a {@link Reader}.
	 * 
	 * @param r
	 * @return
	 * @throws IOException
	 */
	public IDocument parse(Reader r) throws IOException;
}
