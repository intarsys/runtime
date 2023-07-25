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
package de.intarsys.tools.notice;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

/**
 * Convert a {@link Notice} to a {@link IArgs}
 */
public class ArgsFromNoticeConverter implements IConverter<Notice, IArgs> {

	@Override
	public IArgs convert(Notice source) throws ConversionException {
		Args result = Args.create();
		String severity = null;
		if (source.isDebug()) {
			severity = "debug";
		} else if (source.isInfo()) {
			severity = "info";
		} else if (source.isWarning()) {
			severity = "warning";
		} else if (source.isError()) {
			severity = "error";
		} else {
			severity = String.valueOf(source.getSeverity());
		}
		result.put("severity", severity);
		result.put("code", source.getCode());
		result.put("string", source.getString());
		// legacy
		result.put("text", source.getText());
		return result;
	}

	@Override
	public Class<?> getSourceType() {
		return Notice.class;
	}

	@Override
	public Class<?> getTargetType() {
		return IArgs.class;
	}

}