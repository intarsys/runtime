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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A monitor doing nothing...
 */
public class NullMonitor implements IMonitor {

	/** ...but at least it has a name */
	private String name;

	private ITrace nullTrace = new ITrace() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.tools.monitor.IMonitorTrace#sample(java.lang.String)
		 */
		public ISample sample(Level level, String description) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.tools.monitor.IMonitorTrace#tag(java.lang.String,
		 * java.lang.Object)
		 */
		public void tag(String key, Object tag) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#start()
	 */
	public ITrace attach() {
		return nullTrace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#stop()
	 */
	public void detach() {
		// this is a null implementation
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getActive()
	 */
	public int getActive() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getCollectAll()
	 */
	public int getCollectAll() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getConcurrent()
	 */
	public int getConcurrent() {
		return 0;
	}

	public ITrace getCurrentTrace() {
		return nullTrace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getData()
	 */
	public Map getData() {
		return new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getDifference()
	 */
	public long getDifference() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getFormattedData()
	 */
	public Map getFormattedData() {
		return new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getFormattedStart()
	 */
	public String getFormattedStart() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getFormattedStop()
	 */
	public String getFormattedStop() {
		return "";
	}

	@Override
	public Logger getLogger() {
		return Logger.getAnonymousLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getSample(java.lang.String)
	 */
	public ISample getSample(String description) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getSamples()
	 */
	public List getSamples() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getStart()
	 */
	public long getStart() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getStop()
	 */
	public long getStop() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#getTags()
	 */
	public Map getTags() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getTraces()
	 */
	public List getTraces() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#isActive()
	 */
	public boolean isActive() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#reset()
	 */
	public void reset() {
		// this is a null implementation
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#sample(java.lang.String)
	 */
	public ISample sample(String description) {
		// this is a null implementation
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#setCollectAll(int)
	 */
	public void setCollectAll(int count) {
		// this is a null implementation
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#setCollectExtremes(int)
	 */
	public void setCollectExtremes(int count) {
		// this is a null implementation
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitorTrace#tag(java.lang.String,
	 * java.lang.Object)
	 */
	public void tag(String key, Object tag) {
		// this is a null implementation
	}
}
