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
package de.intarsys.tools.monitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * A monitor doing nothing...
 */
public class NullMonitor implements IMonitor {

	/** ...but at least it has a name */
	private String name;

	private ITrace nullTrace = new ITrace() {
		@Override
		public ISample sample(Level level, String description) {
			return null;
		}

		@Override
		public void stop() {
			// not required
		}

		@Override
		public void tag(String key, Object tag) {
			// not required
		}

	};

	/**
	 * Create a NullMonitor
	 * 
	 * @param name
	 *            monitor name
	 */
	public NullMonitor(String name) {
		super();
		this.name = name;
	}

	@Override
	public ITrace attach() {
		return nullTrace;
	}

	@Override
	public void detach() {
		// this is a null implementation
	}

	public int getActive() {
		return 0;
	}

	public int getCollectAll() {
		return 0;
	}

	public int getConcurrent() {
		return 0;
	}

	@Override
	public ITrace getCurrentTrace() {
		return nullTrace;
	}

	@Override
	public Map getData() {
		return new HashMap();
	}

	public long getDifference() {
		return 0;
	}

	@Override
	public Map getFormattedData() {
		return new HashMap();
	}

	public String getFormattedStart() {
		return "";
	}

	public String getFormattedStop() {
		return "";
	}

	@Override
	public ILogger getLogger() {
		return LogTools.getLogger("");
	}

	@Override
	public String getName() {
		return name;
	}

	public List getSamples() {
		return Collections.emptyList();
	}

	public long getStart() {
		return 0;
	}

	public long getStop() {
		return 0;
	}

	public Map getTags() {
		return Collections.emptyMap();
	}

	@Override
	public List getTraces() {
		return Collections.emptyList();
	}

	public boolean isActive() {
		return false;
	}

	@Override
	public void reset() {
		// this is a null implementation
	}
}
