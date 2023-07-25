/*
 * Copyright (c) 2008, intarsys GmbH
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

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import junit.framework.TestCase;

public class TestMonitorConfiguration extends TestCase {

	public TestMonitorConfiguration() {
		super();
	}

	public TestMonitorConfiguration(String name) {
		super(name);
	}

	public void testCompositeConfiguration() throws Exception, ConfigurationException {
		String configString = "<monitors>" //
				+ "<monitor name='Composite' class='de.intarsys.tools.monitor.CompositeMonitor'>" //
				+ "  <monitors>" //
				+ "    <monitor name='Test' class='de.intarsys.tools.monitor.TimeMonitor'/>" //
				+ "    <monitor name='Diedel' class='de.intarsys.tools.monitor.MemoryMonitor' />" //
				+ "  </monitors>"//
				+ "</monitor>" //
				+ "</monitors>";
		IElement element = ElementTools.parseElement(configString);
		MonitorFactory.createMonitors(element, null);
		//
		IMonitor m1 = MonitorTools.getMonitor("Composite"); //$NON-NLS-1$
		assertTrue(m1 instanceof CompositeMonitor);
		assertTrue(((CompositeMonitor) m1).getChildren().size() == 2);
		assertTrue(((CompositeMonitor) m1).getChildren().get(0) instanceof TimeMonitor);
		assertTrue(((CompositeMonitor) m1).getChildren().get(1) instanceof MemoryMonitor);
		IMonitor m2 = MonitorTools.getMonitor("Test"); //$NON-NLS-1$
		assertTrue(m2 instanceof TimeMonitor);
	}

	public void testEmptyConfiguration() throws Exception {
		String configString = "<monitors>" //
				+ "</monitors>";
		IElement element = ElementTools.parseElement(configString);
		MonitorFactory.createMonitors(element, null);
		//
		IMonitor m1 = MonitorTools.getMonitor("Foo"); //$NON-NLS-1$
		assertTrue(m1 instanceof NullMonitor);
	}

	public void testMemberConfiguration() throws Exception {
		String configString = "<monitors>" //
				+ "<monitor " //
				+ "    name='FieldMember' " //
				+ "    class='de.intarsys.tools.monitor.MemberMonitor' " //
				+ "    monitoredclass='de.intarsys.tools.monitor.ATestClass'" //
				+ "    monitoredfield='Counter'" //
				+ " />" //
				+ "<monitor " //
				+ "    name='MethodMember' " //
				+ "    class='de.intarsys.tools.monitor.MemberMonitor'" //
				+ "    monitoredclass='de.intarsys.tools.monitor.ATestClass'" //
				+ "    monitoredmethod='getCounter'" //
				+ " />" //
				+ "</monitors>"; //
		IElement element = ElementTools.parseElement(configString);
		MonitorFactory.createMonitors(element, null);
		//
		IMonitor m1 = MonitorTools.getMonitor("FieldMember"); //$NON-NLS-1$
		assertTrue(m1 instanceof MemberMonitor);
		assertTrue(((MemberMonitor) m1).getClazz() == ATestClass.class);
		assertTrue(((MemberMonitor) m1).getField() != null);
		assertTrue(((MemberMonitor) m1).getMethod() == null);
		IMonitor m2 = MonitorTools.getMonitor("MethodMember"); //$NON-NLS-1$
		assertTrue(m2 instanceof MemberMonitor);
		assertTrue(((MemberMonitor) m2).getClazz() == ATestClass.class);
		assertTrue(((MemberMonitor) m2).getField() == null);
		assertTrue(((MemberMonitor) m2).getMethod() != null);
	}

	public void testSimpleConfiguration() throws Exception {
		String configString = "<monitors>" //
				+ "<monitor name='Test' class='de.intarsys.tools.monitor.TimeMonitor'/>" //
				+ "</monitors>"; //
		IElement element = ElementTools.parseElement(configString);
		MonitorFactory.createMonitors(element, null);
		//
		IMonitor m1 = MonitorTools.getMonitor("Test"); //$NON-NLS-1$
		assertTrue(m1 instanceof TimeMonitor);
		assertTrue(((TimeMonitor) m1).getCollectAll() == 0);
		// assertTrue(((TimeMonitor)m1).getLogger() == null);
	}

	public void testTwoConfiguration() throws Exception {
		String configString = "<monitors>" //
				+ "<monitor name='Test' class='de.intarsys.tools.monitor.TimeMonitor' />" //
				+ "<monitor name='Diedel' class='de.intarsys.tools.monitor.MemoryMonitor'/>" //
				+ "</monitors>"; //$NON-NLS-1$ //$NON-NLS-2$
		IElement element = ElementTools.parseElement(configString);
		MonitorFactory.createMonitors(element, null);
		//
		IMonitor m1 = MonitorTools.getMonitor("Test"); //$NON-NLS-1$
		assertTrue(m1 instanceof TimeMonitor);
		assertTrue(((TimeMonitor) m1).getCollectAll() == 0);
		// assertTrue(((TimeMonitor)m1).getLogger() == null);
		IMonitor m2 = MonitorTools.getMonitor("Diedel"); //$NON-NLS-1$
		assertTrue(m2 instanceof MemoryMonitor);
		assertTrue(((MemoryMonitor) m2).getCollectAll() == 0);
		// assertTrue(((MemoryMonitor)m2).getLogger() == null);
	}
}
