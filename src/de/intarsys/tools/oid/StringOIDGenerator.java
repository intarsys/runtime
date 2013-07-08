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

/**
 * Create an OID unique to the running VM. Multiple {@link StringOIDGenerator}
 * instances create different OIDs.
 * 
 */
public class StringOIDGenerator extends CommonOIDGenerator {
	private static int COUNTER = 0;

	final private int myId = COUNTER++;

	private long lastMillis = 0;

	final private String prefix;

	final private String suffix;

	/**
	 * 
	 */
	public StringOIDGenerator(String prefix, String suffix) {
		super();
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.oid.OIDGenerator#createOID()
	 */
	public Object createOID() {
		long millis = System.currentTimeMillis();
		synchronized (this) {
			while (millis <= lastMillis) {
				millis++;
			}
			lastMillis = millis;
		}
		return prefix + "_" + myId + "_" + millis + "_" + suffix;
	}
}
