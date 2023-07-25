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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An {@link IMessageBundle} that allows to "namespace" the messages from an
 * underlying {@link IMessageBundle}.
 * 
 */
public class PrefixedMessageBundle implements IMessageBundle {

	private final String prefix;

	private final IMessageBundle messageBundle;

	public PrefixedMessageBundle(IMessageBundle messageBundle, String prefix) {
		Objects.requireNonNull(messageBundle, "messageBundle cannot be null");
		this.messageBundle = messageBundle;
		this.prefix = prefix;
	}

	protected String createKey(String key) {
		return prefix + "." + key;
	}

	@Override
	public String format(String pattern, Object... args) {
		return getMessageBundle().format(pattern, args);
	}

	@Override
	public Set<String> getCodes() {
		return getMessageBundle().getCodes().stream().map((code) -> createKey(code)).collect(Collectors.toSet());
	}

	@Override
	public IMessage getMessage(String key, Object... arg) {
		return getMessageBundle().getMessage(createKey(key));
	}

	public IMessageBundle getMessageBundle() {
		return messageBundle;
	}

	@Override
	public String getName() {
		return getMessageBundle().getName();
	}

	@Override
	public String getPattern(String key) {
		return getMessageBundle().getPattern(createKey(key));
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getString(String key, Object... args) {
		return getMessageBundle().getString(createKey(key), args);
	}

}
