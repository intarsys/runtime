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

/**
 * Access a field or method member of a class to take the sample.
 * 
 */
public class MemberMonitorTrace extends MonitorTrace {
	/**
	 * Create instance
	 * 
	 * @param owner
	 *            monitor owning the trace
	 */
	public MemberMonitorTrace(Monitor owner) {
		super(owner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.MonitorTrace#createSampleValue()
	 */
	@Override
	protected long createSampleValue() {
		Field field = getField();
		if (field != null) {
			return createSampleValueField(field);
		} else {
			return createSampleValueMethod(getMethod());
		}
	}

	protected long createSampleValueField(Field field) {
		try {
			Object result = field.get(null);
			return ((Number) result).intValue();
		} catch (Exception e) {
			// ignore exceptions
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.monitor.MonitorEvent#createSample()
	 */
	protected long createSampleValueMethod(Method method) {
		try {
			Object result = method.invoke(null, new Object[] {});
			return ((Number) result).intValue();
		} catch (Exception e) {
			// ignore exceptions
		}
		return 0;
	}

	protected Field getField() {
		return ((MemberMonitor) getOwner()).getField();
	}

	protected Method getMethod() {
		return ((MemberMonitor) getOwner()).getMethod();
	}
}
