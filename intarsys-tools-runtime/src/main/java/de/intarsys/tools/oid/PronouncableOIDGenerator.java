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
package de.intarsys.tools.oid;

import java.util.Random;

/**
 * Create an OID that look like english words to ease mental capture, e.g. in log files.
 * 
 * Typical result look like
 * <ul>
 * <li>vokwer</li>
 * <li>kortex</li>
 * <li>wadfet</li>
 * </ul>
 * 
 */
public class PronouncableOIDGenerator extends CommonOIDGenerator<String> {

	private static char[] Vowels = "aeiou".toCharArray();

	private static char[] Consonants = "bcdfghjklmnprstvwxyz".toCharArray();

	private int length = 6;

	private final Random random = new Random();

	private char[] pattern = "cvc".toCharArray();

	public PronouncableOIDGenerator() {
		super();
	}

	public PronouncableOIDGenerator(String pattern) {
		super();
		setPattern(pattern.toCharArray());
		setLength(pattern.length());
	}

	public PronouncableOIDGenerator(String pattern, int length) {
		super();
		setPattern(pattern.toCharArray());
		setLength(length);
	}

	@Override
	public String createOID() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c;
			int index = i % pattern.length;
			char type = pattern[index];
			switch (type) {
			case 'c':
				c = Consonants[random.nextInt(Consonants.length)];
				break;
			case 'v':
				c = Vowels[random.nextInt(Vowels.length)];
				break;
			default:
				throw new IllegalArgumentException("unknown pattern selector " + type);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public int getLength() {
		return length;
	}

	public char[] getPattern() {
		return pattern;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setPattern(char[] pattern) {
		this.pattern = pattern;
	}

}
