/*
 * Copyright (c) 2014, intarsys GmbH
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
package de.intarsys.tools.yalf.jul;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.intarsys.tools.logging.jul.MemoryHandler;
import de.intarsys.tools.logging.jul.SimpleFormatter;
import de.intarsys.tools.yalf.handler.IMemoryHandlerFactory;

public class JulMemoryHandlerFactory extends JulHandlerFactory<JulMemoryHandler>
		implements IMemoryHandlerFactory<LogRecord, JulMemoryHandler> {

	private int size = 1000;

	@Override
	protected JulMemoryHandler basicCreateHandler() throws IOException {
		MemoryHandler handler = new MemoryHandler("", getSize());
		handler.setLevel(Level.ALL);
		SimpleFormatter formatter = SimpleFormatter.parse(getPattern());
		if (formatter != null) {
			handler.setFormatter(formatter);
		}
		JulMemoryHandler result = new JulMemoryHandler(handler);
		return result;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void setSize(int size) {
		this.size = size;
	}

}
