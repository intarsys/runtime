package de.intarsys.tools.jmx;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public abstract class MBeanSupport {

	private NotificationBroadcasterSupport mbean;
	private ObjectName mbeanName;
	private int mbeanNotificationCounter;

	protected MBeanSupport() {
		super();
	}

	protected abstract NotificationBroadcasterSupport mbeanCreate();

	protected abstract void mbeanDeclareProperties(Hashtable<String, String> properties); // NOSONAR

	protected String mbeanGetDomain() {
		return "de.intarsys";
	}

	public void mbeanPublish() throws JMException {
		mbean = mbeanCreate();
		String domain = mbeanGetDomain();
		Hashtable<String, String> properties = new Hashtable<String, String>(); // NOSONAR
		mbeanDeclareProperties(properties);
		mbeanName = new ObjectName(domain, properties);
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		server.registerMBean(mbean, mbeanName);
	}

	public void mbeanSendNotification(String type, String message) {
		if (mbean == null) {
			return;
		}
		Notification notification = new Notification(type, mbeanName, mbeanNotificationCounter++,
				System.currentTimeMillis(), message);
		mbean.sendNotification(notification);
	}

	public void mbeanUnpublish() throws JMException {
		if (mbeanName == null) {
			return;
		}
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		server.unregisterMBean(mbeanName);
		mbean = null;
		mbeanName = null;
	}

}
