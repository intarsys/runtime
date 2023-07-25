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

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.yalf.api.Level;

/**
 * An abstract superclass to ease implementation of monitor trace objects.
 * 
 * <p>
 * This class does not have to be synchronized anywhere.
 * </p>
 */
public abstract class AbstractMonitorTrace implements ITrace {

	/** The monitor owning the trace */
	private final AbstractMonitor owner;

	private final Level level;

	private int nesting;

	/**
	 * A generic container holding information describing the context of the
	 * trace.
	 */
	private Map tags;

	/**
	 * Create a MonitorTrace.
	 * 
	 * @param owner
	 *            The monitor that created the trace.
	 */
	protected AbstractMonitorTrace(AbstractMonitor owner) {
		super();
		this.owner = owner;
		this.level = owner.getLevel();
	}

	protected abstract ISample basicSample(String description);

	protected void basicStart() {
		nesting++;
		if (nesting == 1) {
			owner.started(this);
			if ((tags != null) && (tags.size() > 0)) {
				tags.clear();
			}
		} else {
			sample(Level.INFO, "start");
		}
	}

	protected void basicStop() {
		nesting--;
		if (nesting == 0) {
			owner.stopped(this);
			traceLog();
		} else {
			sample(Level.INFO, "stop");
		}
	}

	protected void basicTag(String key, Object tag) {
		if (tags == null) {
			tags = new HashMap();
		}
		tags.put(key, tag);
	}

	/**
	 * The monitor that owns this trace.
	 * 
	 * @return The monitor that owns this trace.
	 */
	protected final AbstractMonitor getOwner() {
		return owner;
	}

	public Map getTags() {
		return tags;
	}

	@Override
	public final ISample sample(Level pLevel, String description) {
		if (level.compareTo(pLevel) > 0) {
			return null;
		}
		return basicSample(description);
	}

	/**
	 * Reset the trace so that it can safely be reused.
	 */
	protected final void start() {
		basicStart();
	}

	@Override
	public final void stop() {
		basicStop();
	}

	@Override
	public final void tag(String key, Object tag) {
		basicTag(key, tag);
	}

	protected void traceLog() {
		// do nothing
	}
}
