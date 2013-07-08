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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.reader.DirectTagReader;
import de.intarsys.tools.reader.IDirectTagHandler;
import de.intarsys.tools.reader.ILocationProvider;
import de.intarsys.tools.string.StringTools;

/**
 * Create a "syntax tree" from a tagged string.
 * 
 * The syntax tree is simply a list of {@link TaggedStringNode} instances.
 */
public class TaggedStringParser {

	private boolean escape;

	private IDirectTagHandler handler = new IDirectTagHandler() {
		public Object endTag(String tagContent, Object context)
				throws IOException {
			processtag(tagContent, (List<TaggedStringNode>) context);
			return ""; //$NON-NLS-1$
		}

		public void setLocationProvider(ILocationProvider location) {
			// ignore
		}

		public void startTag() {
			//
		}
	};

	private StringBuilder sb = new StringBuilder();

	/**
	 * 
	 */
	public TaggedStringParser() {
		this(false);
	}

	public TaggedStringParser(boolean escape) {
		super();
		this.escape = escape;
	}

	public void declareVariables(TaggedStringVariables variables,
			String template) throws IOException {
		if (StringTools.isEmpty(template)) {
			return;
		}
		List<TaggedStringNode> nodes = parse(template);
		for (TaggedStringNode node : nodes) {
			if (node instanceof TaggedStringExpression) {
				String expr = ((TaggedStringExpression) node).getExpression();
				if (!variables.isDefined(expr)) {
					variables.put(expr, "");
				}
			}
		}
	}

	public boolean isEscape() {
		return escape;
	}

	public List<TaggedStringNode> parse(String expression) throws IOException {
		sb.setLength(0);
		List<TaggedStringNode> list = new ArrayList<TaggedStringNode>();
		Reader base = new StringReader(expression);
		Reader reader = new DirectTagReader(base, handler, list, isEscape());
		int i = reader.read();
		while (i != -1) {
			sb.append((char) i);
			i = reader.read();
		}
		if (sb.length() > 0) {
			list.add(new TaggedStringLiteral(sb.toString()));
			sb.setLength(0);
		}
		return list;
	}

	protected void processtag(String expression, List<TaggedStringNode> context) {
		if (sb.length() > 0) {
			context.add(new TaggedStringLiteral(sb.toString()));
			sb.setLength(0);
		}
		TaggedStringExpression var = null;
		var = new TaggedStringExpression(expression);
		context.add(var);
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

}
