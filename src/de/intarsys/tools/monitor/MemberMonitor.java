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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;

/**
 * A monitor for taking time samples in the application.
 */
public class MemberMonitor extends Monitor {
	// the clas we monitor
	private Class clazz;

	private Method method;

	private Field field;

	public MemberMonitor() {
		super();
	}

	/**
	 * Create instance
	 * 
	 * @param name
	 *            monitor name
	 */
	public MemberMonitor(String name) {
		super(name);
	}

	public MemberMonitor(String name, Class clazz, Field field) {
		super(name);
		this.clazz = clazz;
		this.field = field;
	}

	public MemberMonitor(String name, Class clazz, Method method) {
		super(name);
		this.clazz = clazz;
		this.method = method;
	}

	@Override
	public void configure(IElement element)
			throws ConfigurationException {
		super.configure(element);
		String className = ElementTools.getPathString(element,
				"monitoredclass", null);
		if (className == null) {
			throw new ConfigurationException(
					"<monitoredclass> may not be null");
		}
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(className + " not found");
		} catch (SecurityException e) {
			throw new ConfigurationException(className
					+ " not permitted");
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException(className
					+ " not instantiable");
		}
		String methodName = ElementTools.getPathString(element,
				"monitoredmethod", null);
		if (methodName != null) {
			try {
				method = getClazz().getMethod(methodName, new Class[0]);
			} catch (SecurityException e) {
				throw new ConfigurationException(methodName
						+ " not permitted");
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException(methodName
						+ " not instantiable");
			} catch (NoSuchMethodException e) {
				throw new ConfigurationException(methodName
						+ " not found");
			}
		}
		String fieldName = ElementTools.getPathString(element,
				"monitoredfield", null);
		if (fieldName != null) {
			try {
				field = getClazz().getField(fieldName);
			} catch (SecurityException e) {
				throw new ConfigurationException(fieldName
						+ " not permitted");
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException(fieldName
						+ " not instantiable");
			} catch (NoSuchFieldException e) {
				throw new ConfigurationException(fieldName
						+ " not found");
			}
		}
		if ((field == null) && (method == null)) {
			throw new ConfigurationException(
					"field or method must be set");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.Monitor#createMonitorTrace()
	 */
	@Override
	protected MemberMonitorTrace createMonitorTrace() {
		return new MemberMonitorTrace(this);
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

		statistic.avg = ((statistic.avg * statistic.count) + trace.getStop())
				/ (statistic.count + 1);

		statistic.count++;
	}

	protected Class getClazz() {
		return clazz;
	}

	protected Field getField() {
		return field;
	}

	protected Method getMethod() {
		return method;
	}
}
