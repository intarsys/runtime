/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.file;

import java.io.File;

/**
 * Handles wildcards for filename matching.
 */
public class WildcardMatch {
	private boolean ignoreCase = true;

	private boolean matchSeparators = true;

	/**
	 * 
	 */
	public WildcardMatch() {
		super();
	}

	protected char adjustCase(char c) {
		return ignoreCase ? Character.toLowerCase(c) : c;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public boolean isMatchSeparators() {
		return matchSeparators;
	}

	public boolean match(final String pattern, final String string) {
		// TODO lot of optimizations
		// be sure test still runs
		char c;
		int len = pattern.length();
		int n = 0;
		int p = 0;
		while (p < len) {
			c = adjustCase(pattern.charAt(p));
			switch (c) {
			case '?':
				if (string.length() == n) {
					return false;
				}
				if (!matchSeparators && (string.charAt(n) == File.separatorChar)) {
					return false;
				}
				break;
			case '*':
				if (++p == pattern.length()) {
					for (; string.length() > n; ++n) {
						if ((string.charAt(n) == File.separatorChar) && !matchSeparators) {
							return false;
						}
					}
					return true;
				} else {
					for (; string.length() >= n; ++n) {
						if (match(pattern.substring(p), string.substring(n))) {
							return true;
						}
						if ((n == string.length()) || ((string.charAt(n) == File.separatorChar) && !matchSeparators)) {
							return false;
						}
					}
				}
				return false;
			default:
				if (string.length() == n) {
					return false;
				}
				if (c != adjustCase(string.charAt(n))) {
					return false;
				}
			}
			p++;
			++n;
		}
		if (string.length() == n) {
			return true;
		}
		return false;
	}

	public void setIgnoreCase(boolean newIgnoreCase) {
		ignoreCase = newIgnoreCase;
	}

	public void setMatchSeparators(boolean newMatchSeparators) {
		matchSeparators = newMatchSeparators;
	}
}
