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

/**
 * A monitor for watching the memory usage in the application.
 */
public class MemoryMonitor extends Monitor {
	public MemoryMonitor() {
		super();
	}

	public MemoryMonitor(String name) {
		super(name);
	}

	@Override
	protected MemoryMonitorTrace createMonitorTrace() {
		return new MemoryMonitorTrace(this);
	}

	@Override
	protected void doCalculation(MonitorTrace trace) {
		if (trace.getStart() < statistic.min) {
			statistic.min = trace.getStart();
		}
		if (trace.getStop() < statistic.min) {
			statistic.min = trace.getStop();
		}

		if (trace.getStart() > statistic.max) {
			statistic.max = trace.getStart();
		}
		if (trace.getStop() > statistic.max) {
			statistic.max = trace.getStop();
		}

		statistic.total = last - first;

		statistic.avg = ((statistic.avg * statistic.count) + trace.getStop()) / (statistic.count + 1);

		statistic.count++;
	}
}
