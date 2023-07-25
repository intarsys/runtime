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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

public class CompositeMonitor extends AbstractMonitor {

	/** The monitors that are contained */
	private List<IMonitor> children = new ArrayList<>();

	public CompositeMonitor() {
		super();
	}

	public CompositeMonitor(String name) {
		super(name);
	}

	public void addMonitor(IMonitor monitor) {
		children.add(monitor);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		try {
			children = MonitorFactory.createChildMonitors(element, null);
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	@Override
	protected CompositeMonitorTrace createMonitorTrace() {
		ITrace[] traces = new ITrace[getChildren().size()];
		int i = 0;
		for (IMonitor monitor : children) {
			traces[i] = monitor.getCurrentTrace();
			i++;
		}
		CompositeMonitorTrace trace = new CompositeMonitorTrace(this, traces);
		return trace;
	}

	public List<IMonitor> getChildren() {
		return children;
	}

	@Override
	public Map getData() {
		Map result = new HashMap();
		for (IMonitor monitor : children) {
			Map childData = monitor.getData();
			for (Iterator it = childData.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				result.put("" + monitor.getName() + "." + entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Override
	public Map getFormattedData() {
		Map result = new HashMap();
		for (IMonitor monitor : children) {
			Map childData = monitor.getFormattedData();
			for (Iterator it = childData.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				result.put("" + monitor.getName() + "." + entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Override
	public void reset() {
		super.reset();
		for (IMonitor monitor : children) {
			monitor.reset();
		}
	}

	@Override
	public String toString() {
		return "Composite Monitor " + getName();
	}
}
