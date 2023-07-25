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
package de.intarsys.tools.yalf.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.yalf.api.IHandlerFactory;
import de.intarsys.tools.yalf.common.CommonHandlerFactory;

/**
 * 
 * An {@link IHandlerFactory} that creates a {@link CompositeHandler} by
 * delegating to a collection of other {@link IHandlerFactory} instances.
 * 
 */
public class CompositeHandlerFactory<R> extends CommonHandlerFactory<R, CompositeHandler<?, R>> {

	private List<IHandlerFactory> handlerFactories = new ArrayList<>();

	public void addLogHandlerFactory(IHandlerFactory factory) {
		handlerFactories.add(factory);
	}

	@Override
	protected CompositeHandler basicCreateHandler() throws IOException {
		CompositeHandler handler = new CompositeHandler();
		for (IHandlerFactory factory : handlerFactories) {
			handler.addHandler(factory.createHandler());
		}
		return handler;
	}

	public void removeLogHandlerFactory(IHandlerFactory factory) {
		handlerFactories.remove(factory);
	}

}
