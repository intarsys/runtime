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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;

/**
 * An abstract superclass to ease implementation of IMonitor objects.
 */
public abstract class AbstractMonitor implements IMonitor, IElementConfigurable {

	/** The default logging level to use for a new monitor instance */
	private static Level defaultLevel = Level.INFO;

	public static Level getDefaultLevel() {
		return defaultLevel;
	}

	public static void setDefaultLevel(Level defaultLevel) {
		AbstractMonitor.defaultLevel = defaultLevel;
	}

	/** The logical full qualified name of the monitor */
	private String name;

	/**
	 * The logger instance that should receive log messages when a trace is
	 * taken.
	 */
	private Logger logger;

	/** The log level to use for this monitor */
	private Level level = defaultLevel;

	/** the collection of traces (depending on the trace flags) */
	private LinkedList traces = new LinkedList();

	/** control the trace collecting behavior. */
	private int collectAll = 0;

	private int logCycle = 100;

	private int traceCount = 0;

	/**
	 * The current IMonitorTrace. The monitor may be used from different
	 * threads, so any will get its own trace.
	 * 
	 * <p>
	 * In a typical production environment the number of trace instances is
	 * bound as threads will be pooled.
	 * </p>
	 */
	private ThreadLocal<AbstractMonitorTrace> tracePerThread = new ThreadLocal<AbstractMonitorTrace>() {
		@Override
		protected AbstractMonitorTrace initialValue() {
			return createMonitorTrace();
		}
	};

	public AbstractMonitor() {
		this("");
	};

	public AbstractMonitor(String name) {
		super();
		this.name = name;
		logger = Logger.getLogger(name);
	}

	/*
	 * Start a new "measuring".
	 * 
	 * @see de.intarsys.monitor.IMonitor#start()
	 */
	final public ITrace attach() {
		// todo how to handle re-entry of active monitors
		AbstractMonitorTrace trace = (AbstractMonitorTrace) getCurrentTrace();
		trace.start();
		Trace.registerTrace(trace);
		return trace;
	}

	public void configure(IElement element)
			throws ConfigurationException {
		setName(ElementTools.getPathString(element, "name", ""));
		setCollectAll(ElementTools.getPathInt(element, "collect.count", 0));
		String loggerString = ElementTools.getPathString(element, "logger",
				getName());
		if (loggerString != null) {
			Logger logger = Logger.getLogger(loggerString);
			setLogger(logger);
		}
		String defaultLevelString = defaultLevel.getName();
		String levelString = ElementTools.getPathString(element, "level",
				defaultLevelString);
		setLevel(Level.parse(levelString));
		int intValue = ElementTools.getPathInt(element, "logcycle", 100);
		setLogCycle(intValue);
	}

	/**
	 * Factory method for a new IMonitorSample.
	 * 
	 * @return The new sample.
	 */
	protected abstract AbstractMonitorTrace createMonitorTrace();

	final public void detach() {
		AbstractMonitorTrace trace = (AbstractMonitorTrace) getCurrentTrace();
		trace.stop();
	}

	/**
	 * The internal representation of the trace collection (if any).
	 * 
	 * @return The internal representation of the trace collection (if any).
	 */
	protected LinkedList getBasicTraces() {
		return traces;
	}

	public int getCollectAll() {
		return collectAll;
	}

	/**
	 * The currently active IMonitorTrace for the running thread.
	 * 
	 * @return The currently active IMonitorTrace for the running thread.
	 */
	public ITrace getCurrentTrace() {
		return tracePerThread.get();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return Returns the traceLogLevel.
	 */
	public Level getLevel() {
		return level;
	}

	public int getLogCycle() {
		return logCycle;
	}

	public Logger getLogger() {
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.monitor.IMonitor#getName()
	 */
	public String getName() {
		return name;
	}

	public synchronized List getTraces() {
		List list = new ArrayList(traces);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.monitor.IMonitor#reset()
	 */
	public void reset() {
		traceLog();
		traces = new LinkedList();
		traceCount = 0;
	}

	public void setCollectAll(int count) {
		this.collectAll = count;
	}

	public void setLevel(Level traceLogLevel) {
		this.level = traceLogLevel;
	}

	public void setLogCycle(int pLogCycle) {
		this.logCycle = pLogCycle;
	}

	public void setLogger(Logger traceLog) {
		this.logger = traceLog;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @deprecated use attach
	 */
	@Deprecated
	public void start() {
		attach();
	}

	/**
	 * Get informed that a trace has been started.
	 * 
	 * @param trace
	 *            The trace that is started.
	 */
	protected synchronized void started(ITrace trace) {
		//
	}

	/**
	 * @deprecated use detach
	 */
	@Deprecated
	public void stop() {
		detach();
	}

	/**
	 * Get informed that a trace has been finished.
	 * 
	 * @param trace
	 *            The trace that is finished.
	 */
	protected synchronized void stopped(ITrace trace) {
		Trace.unregisterTrace(trace);
		if (getCollectAll() > 0) {
			getBasicTraces().add(trace);
			tracePerThread.set(createMonitorTrace());

			if (getCollectAll() < getBasicTraces().size()) {
				getBasicTraces().removeFirst();
			}
		}
		traceCount++;
		if (getLogCycle() != -1 && traceCount >= getLogCycle()) {
			reset();
		}
	}

	protected void traceLog() {
		if (getLogger() != null) {
			getLogger().logp(getLevel(), "", "", toString());
		}
	}
}
