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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.yalf.api.IMDC;

/**
 * A simple implementation for an {@link IMDC}.
 * 
 */
public class MDC implements IMDC {

	private static final ThreadLocal<Map<String, String>> MESSAGE = new ThreadLocal<>();

	@Override
	public void clear() {
		Map<String, String> map = MESSAGE.get();
		if (map != null) {
			map.clear();
			MESSAGE.remove();
		}
	}

	@Override
	public String get(String key) {
		Map<String, String> map = MESSAGE.get();
		if ((map != null) && (key != null)) {
			return map.get(key);
		} else {
			return null;
		}
	}

	@Override
	public Map<String, String> getCopyOfContextMap() {
		Map<String, String> map = MESSAGE.get();
		if (map == null) {
			return null; // NOSONAR
		}
		return new HashMap<>(map);
	}

	@Override
	public void put(String key, String val) {
		Map<String, String> map = MESSAGE.get();
		if (map == null) {
			map = Collections.<String, String> synchronizedMap(new HashMap<String, String>());
			MESSAGE.set(map);
		}
		map.put(key, val);
	}

	@Override
	public void remove(String key) {
		Map<String, String> map = MESSAGE.get();
		if (map != null) {
			map.remove(key);
		}
	}

	@Override
	public void setContextMap(Map<String, String> contextMap) {
		Map<String, String> map = Collections.synchronizedMap(new HashMap<String, String>());
		MESSAGE.set(map);
	}
}
