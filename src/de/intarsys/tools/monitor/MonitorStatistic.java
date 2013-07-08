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

/**
 * This object keeps the statistical computations for a sample in a monitor.
 */
public class MonitorStatistic implements Comparable {
	/**
	 * The index within the sequence of sample when this description was first
	 * encountered
	 */
	protected int index;

	/**
	 * The description of the samples we are responsible for.
	 */
	protected String description;

	/** attributes for the collected event statistics */
	/** The minimum sample in all traces */
	protected long min;

	/** The maximum sample in all traces */
	protected long max;

	/** The sum of all trace results */
	protected long total;

	/** The number of traces taken so far */
	protected long count;

	/** The average sample value for all traces */
	protected long avg;

	/**
	 * 
	 */
	public MonitorStatistic(String description, int index) {
		super();
		reset();
		this.description = description;
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return index - ((MonitorStatistic) o).index;
	}

	public long getAvg() {
		return avg;
	}

	public long getCount() {
		return count;
	}

	public int getIndex() {
		return index;
	}

	public long getMax() {
		return max;
	}

	public long getMin() {
		return min;
	}

	public long getTotal() {
		return total;
	}

	public void reset() {
		min = Long.MAX_VALUE;
		max = Long.MIN_VALUE;
		total = 0;
		avg = 0;
		count = 0;
	}

	public void setAvg(long avg) {
		this.avg = avg;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public void setTotal(long total) {
		this.total = total;
	}
}
