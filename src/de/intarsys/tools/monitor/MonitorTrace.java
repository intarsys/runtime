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
package de.intarsys.tools.monitor;

import java.io.StringWriter;
import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * An abstract superclass to ease implementation of "leaf" monitor trace
 * objects. These "leaf" monitors take real samples from their domain.
 * <p>
 * The monitors also implement the concrete algorithm to compute statistics from
 * the samples taken.
 * 
 */
public abstract class MonitorTrace extends AbstractMonitorTrace {
	/** The initial sample value of the trace. */
	private long start = 0;

	/** The final sample value of the trace. */
	private long stop = 0;

	/** the number of concurrent active trace */
	private int concurrent = 0;

	/** The collection of additional samples */
	private List samples = null;

	public MonitorTrace(Monitor owner) {
		super(owner);
	}

	/**
	 * @param description
	 * @return
	 */
	@Override
	protected ISample basicSample(String description) {
		if (samples == null) {
			samples = new ArrayList();
		}
		ISample sample = createMonitorSample(description);
		samples.add(sample);
		return sample;
	}

	@Override
	protected void basicStart() {
		ISample sample = createMonitorSample("start");
		start = sample.getValue();
		stop = -1;
		if ((samples != null) && (samples.size() > 0)) {
			samples.clear();
		}
		setConcurrent(((Monitor) getOwner()).getActive());
		super.basicStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#stop()
	 */
	@Override
	protected void basicStop() {
		ISample sample;
		if (samples == null) {
			sample = createMonitorSample("stop");
		} else {
			sample = sample(Level.INFO, "stop");
		}
		stop = sample.getValue();
		super.basicStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.AbstractMonitorTrace#createMonitorSample()
	 */
	protected ISample createMonitorSample(String description) {
		return new MonitorSample(this, description, createSampleValue());
	}

	/**
	 * Factory method for creating a physical sample value.
	 * 
	 * <p>
	 * A physical sample is for example the current timestamp or the memory
	 * footprint.
	 * </p>
	 * 
	 * @return The physical sample value (in an undefined unit)
	 */
	protected abstract long createSampleValue();

	public int getConcurrent() {
		return concurrent;
	}

	public long getDifference() {
		return stop - start;
	}

	protected Format getFormat() {
		return ((Monitor) getOwner()).getFormat();
	}

	public String getFormattedValue(long value) {
		return getFormat().format(new Long(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorSample#getSamples()
	 */
	public List getSamples() {
		return samples;
	}

	public long getStart() {
		return start;
	}

	public long getStop() {
		return stop;
	}

	/**
	 * @return Returns the relative.
	 */
	protected boolean isRelative() {
		return ((Monitor) getOwner()).isRelative();
	}

	/**
	 * Set the number of concurrent traces active in the monitor.
	 * 
	 * @param concurrent
	 *            The number of concurrent traces in the monitor.
	 */
	protected void setConcurrent(int concurrent) {
		this.concurrent = concurrent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		sw.write("[Trace=");
		sw.write(getOwner().getName());
		sw.write("][");
		sw.write("start=");
		sw.write(getFormattedValue(getStart()));
		sw.write(";");
		long oldValue = getStart();
		if (getSamples() != null) {
			for (Iterator i = getSamples().iterator(); i.hasNext();) {
				ISample sample = (ISample) i.next();
				sw.write(sample.getDescription());
				sw.write("=");
				long value = sample.getValue();
				if (isRelative()) {
					value = value - oldValue;
					oldValue = sample.getValue();
					if (value > 0) {
						sw.write("+");
					}
					sw.write(String.valueOf(value));
				} else {
					sw.write(getFormattedValue(value));
				}
				sw.write(";");
			}
		}
		sw.write("stop=");
		long value = getStop();
		if (isRelative()) {
			value = value - oldValue;
			oldValue = getStop();
			if (value > 0) {
				sw.write("+");
			}
			sw.write(String.valueOf(value));
		} else {
			sw.write(getFormattedValue(value));
		}
		sw.write("]");

		sw.write("[");
		if (isRelative()) {
			sw.write("total=");
			value = getStop() - getStart();
			if (value > 0) {
				sw.write("+");
			}
			sw.write(String.valueOf(value));
			sw.write(";");
		}

		sw.write("concurrent=");
		sw.write(String.valueOf(getConcurrent()));
		if (getTags() != null) {
			Iterator i = getTags().entrySet().iterator();
			if (i.hasNext()) {
				sw.write(";");
			}
			for (; i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				sw.write(String.valueOf(entry.getKey()));
				sw.write("=");
				sw.write(String.valueOf(entry.getValue()));
				if (i.hasNext()) {
					sw.write(";");
				}
			}
		}
		sw.write("]");

		return sw.toString();
	}

	@Override
	protected void traceLog() {
		if (getOwner().getLogger() != null) {
			getOwner().getLogger().logp(getOwner().getLevel(), "", "",
					toString());
		}
	}
}
