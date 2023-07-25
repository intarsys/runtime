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
package de.intarsys.tools.yalf.common;

import java.io.IOException;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.yalf.api.IFilter;
import de.intarsys.tools.yalf.api.IHandlerFactory;

/**
 * A common superclass for implementing {@link IHandlerFactory}
 *
 */
public abstract class CommonHandlerFactory<R, H extends CommonHandler<?, R>> implements IHandlerFactory<R, H> {

	private static final String DEFAULT_PATTERN = "[%d{yyyy.MM.dd-HH:mm:ss.SSS}] %msg%n%rEx";

	private String pattern = DEFAULT_PATTERN;

	private IStringEvaluator templateEvaluator;

	private IFilter<R> filter;

	protected abstract H basicCreateHandler() throws IOException;

	@Override
	public final H createHandler() throws IOException {
		H tempHandler = basicCreateHandler();
		tempHandler.setFilter(getFilter());
		return tempHandler;
	}

	@Override
	public IFilter<R> getFilter() {
		return filter;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	public IStringEvaluator getTemplateEvaluator() {
		if (templateEvaluator == null) {
			return TemplateEvaluator.get(Mode.TRUSTED);
		}
		return templateEvaluator;
	}

	@Override
	public void setFilter(IFilter<R> filter) {
		this.filter = filter;
	}

	@Override
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setTemplateEvaluator(IStringEvaluator templateEvaluator) {
		this.templateEvaluator = templateEvaluator;
	}

}
