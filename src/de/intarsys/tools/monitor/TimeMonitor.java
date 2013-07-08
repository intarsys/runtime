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

import java.text.Format;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.format.TrivialDateFormat;

/**
 * A monitor for taking time samples in the application.
 */
public class TimeMonitor extends Monitor {
	private static Format DEFAULT_FORMAT = TrivialDateFormat.getInstance();

	public TimeMonitor() {
		super();
	}

	/**
	 * Create a TimeMonitor
	 * 
	 * @param name
	 *            monitor name
	 */
	public TimeMonitor(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.Monitor#createMonitorTrace()
	 */
	@Override
	protected TimeMonitorTrace createMonitorTrace() {
		return new TimeMonitorTrace(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.monitor.Monitor#doCalculation(de.intarsys.tools.monitor
	 * .IMonitorTrace)
	 */
	@Override
	protected void doCalculation(MonitorTrace trace) {
		long start = trace.getStart();
		long diff = trace.getDifference();
		doStatistic(statistic, diff);
		List samples = trace.getSamples();
		if (samples != null) {
			Iterator sampleIt = samples.iterator();
			int i = 0;
			for (; sampleIt.hasNext();) {
				MonitorSample sample = (MonitorSample) sampleIt.next();
				diff = sample.getValue() - start;
				start = sample.getValue();
				MonitorStatistic sampleStatistic;
				synchronized (this) {
					sampleStatistic = (MonitorStatistic) sampleStatistics
							.get(sample.getDescription());
					if (sampleStatistic == null) {
						sampleStatistic = new MonitorStatistic(sample
								.getDescription(), i);
						sampleStatistics.put(sample.getDescription(),
								sampleStatistic);
					}
				}
				doStatistic(sampleStatistic, diff);
				i++;
			}
		}
	}

	protected void doStatistic(MonitorStatistic sampleStatistic, long diff) {
		if (diff < sampleStatistic.min) {
			sampleStatistic.min = diff;
		}
		if (diff > sampleStatistic.max) {
			sampleStatistic.max = diff;
		}
		sampleStatistic.total = sampleStatistic.total + diff;
		sampleStatistic.count++;
		sampleStatistic.avg = sampleStatistic.total / sampleStatistic.count;
	}

	@Override
	protected Format getFormat() {
		return DEFAULT_FORMAT;
	}
}
