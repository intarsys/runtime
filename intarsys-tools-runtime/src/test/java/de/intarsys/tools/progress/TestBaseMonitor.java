package de.intarsys.tools.progress;

import junit.framework.TestCase;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class TestBaseMonitor extends TestCase {

	public void testPlain() {
		BaseProgressMonitor monitor;
		//
		monitor = new BaseProgressMonitor();
		assertTrue(!monitor.isCancelled());
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWorked() == 0);
		assertTrue(monitor.getTaskName() == null);
		assertTrue(monitor.getSubTaskName() == null);
		//
		monitor = new BaseProgressMonitor();
		try {
			monitor.worked(1);
			fail();
		} catch (Exception e) {
			//
		}
		assertTrue(!monitor.isCancelled());
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWorked() == 0);
		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		assertTrue(!monitor.isCancelled());
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 0);
		assertTrue(monitor.getTaskName().equals("test"));
		assertTrue(monitor.getSubTaskName() == null);
		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		monitor.worked(1);
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 1);
		monitor.worked(1);
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 2);
		monitor.worked(1);
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 3);
		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		monitor.worked(50);
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 50);
		monitor.worked(50);
		assertTrue(monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 100);
		monitor.worked(50);
		assertTrue(monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 100);
		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		monitor.worked(50);
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 50);
		monitor.end();
		assertTrue(monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 100);
		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		monitor.subTask("foo");
		monitor.worked(50);
		assertTrue(!monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 50);
		assertTrue(monitor.getSubTaskName().equals("foo"));
		monitor.end();
		assertTrue(monitor.isAtEnd());
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 100);
		assertTrue(monitor.getSubTaskName() == null);
	}
}
