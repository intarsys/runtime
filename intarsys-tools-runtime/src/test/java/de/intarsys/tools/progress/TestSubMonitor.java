package de.intarsys.tools.progress;

import junit.framework.TestCase;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class TestSubMonitor extends TestCase {

	public void testPlain() {
		BaseProgressMonitor monitor;
		SubProgressMonitor subMonitor;
		SubProgressMonitor subsubMonitor;
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
		subMonitor = new SubProgressMonitor(monitor, 10);
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 0);
		assertTrue(!subMonitor.isAtEnd());
		subMonitor.begin("foo", 100);
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 0);
		assertTrue(monitor.getTaskName().equals("test"));
		assertTrue(monitor.getSubTaskName().equals("foo"));
		assertTrue(!subMonitor.isAtEnd());
		assertTrue(subMonitor.getWork() == 100);
		assertTrue(subMonitor.getWorked() == 0);
		assertTrue(subMonitor.getTaskName().equals("foo"));
		assertTrue(subMonitor.getSubTaskName() == null);
		subMonitor.worked(10);
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 1);
		assertTrue(!subMonitor.isAtEnd());
		assertTrue(subMonitor.getWork() == 100);
		assertTrue(subMonitor.getWorked() == 10);
		subMonitor.worked(10);
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 2);
		assertTrue(!subMonitor.isAtEnd());
		assertTrue(subMonitor.getWork() == 100);
		assertTrue(subMonitor.getWorked() == 20);
		subMonitor.worked(100);
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 10);
		assertTrue(subMonitor.isAtEnd());
		assertTrue(subMonitor.getWork() == 100);
		assertTrue(subMonitor.getWorked() == 100);

		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		subMonitor = new SubProgressMonitor(monitor, 10);
		subMonitor.begin("foo", 100);
		subMonitor.end();
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 10);
		assertTrue(subMonitor.isAtEnd());
		assertTrue(subMonitor.getWork() == 100);
		assertTrue(subMonitor.getWorked() == 100);
		// flawed use
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		subMonitor = new SubProgressMonitor(monitor, 10);
		// no begin here
		subMonitor.end();
		assertTrue(monitor.getWork() == 100);
		assertTrue(monitor.getWorked() == 10);
		assertTrue(subMonitor.isAtEnd());

		//
		monitor = new BaseProgressMonitor();
		monitor.begin("test", 100);
		subMonitor = new SubProgressMonitor(monitor, 10);
		subMonitor.begin("foo", 100);
		subsubMonitor = new SubProgressMonitor(subMonitor, 50);
		subsubMonitor.begin("bar", 100);
		subsubMonitor.worked(10);
		assertTrue(subsubMonitor.getWorked() == 10);
		assertTrue(subMonitor.getWorked() == 5);
		assertTrue(monitor.getWorked() == 0.5);
		subsubMonitor.worked(10);
		assertTrue(subsubMonitor.getWorked() == 20);
		assertTrue(subMonitor.getWorked() == 10);
		assertTrue(monitor.getWorked() == 1);
		subsubMonitor.end();
		assertTrue(subsubMonitor.getWorked() == 100);
		assertTrue(subMonitor.getWorked() == 50);
		assertTrue(monitor.getWorked() == 5);
		subMonitor.end();
		assertTrue(subMonitor.getWorked() == 100);
		assertTrue(monitor.getWorked() == 10);
	}
}
