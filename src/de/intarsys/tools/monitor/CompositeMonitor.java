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
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

public class CompositeMonitor extends AbstractMonitor {
	/** The monitors that are contained */
	private IMonitor[] children = new IMonitor[0];

	public CompositeMonitor() {
		super();
	}

	public CompositeMonitor(String name) {
		super(name);
	}

	public void addMonitor(IMonitor monitor) {
		IMonitor[] newMonitors;
		int count = 0;
		if (children == null) {
			count = 1;
		} else {
			count = children.length + 1;
		}
		newMonitors = new IMonitor[count];
		System.arraycopy(children, 0, newMonitors, 0, count - 1);
		newMonitors[count - 1] = monitor;
		children = newMonitors;
	}

	@Override
	public void configure(IElement element)
			throws ConfigurationException {
		super.configure(element);
		try {
			children = MonitorFactory.createMonitors(element, null);
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.AbstractMonitor#createMonitorTrace()
	 */
	@Override
	protected CompositeMonitorTrace createMonitorTrace() {
		ITrace[] traces = new ITrace[getChildren().length];
		for (int i = 0; i < children.length; i++) {
			traces[i] = children[i].getCurrentTrace();
		}
		CompositeMonitorTrace trace = new CompositeMonitorTrace(this, traces);
		return trace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.ICompositeMonitor#getChildren()
	 */
	public IMonitor[] getChildren() {
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getFormattedData()
	 */
	public Map getData() {
		Map result = new HashMap();
		for (int i = 0; i < children.length; i++) {
			Map childData = children[i].getData();
			for (Iterator it = childData.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				result.put("" + children[i].getName() + "." + entry.getKey(),
						entry.getValue());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#getFormattedData()
	 */
	public Map getFormattedData() {
		Map result = new HashMap();
		for (int i = 0; i < children.length; i++) {
			Map childData = children[i].getFormattedData();
			for (Iterator it = childData.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				result.put("" + children[i].getName() + "." + entry.getKey(),
						entry.getValue());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.IMonitor#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		for (int i = 0; i < children.length; i++) {
			children[i].reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Composite Monitor " + getName();
	}
}
