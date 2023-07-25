/*
 * intarsys GmbH
 * all rights reserved
 *
 */

package de.intarsys.tools.net;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * A TCP listener framework for a port on localhost.
 * 
 */
public class TCPListener implements Runnable {
	private static final ILogger Log = LogTools.getLogger("de.intarsys.tools.net");

	// a name for this listener (and its thread)
	private String name = "a TCP Listener";

	// the port number to listen to
	private int port;

	// the host address we should bind to
	private String host;

	// the socket on the port
	private ServerSocket serverSocket;

	// the listener thread
	private Thread listenerThread;

	private ExecutorService executor;

	public TCPListener(int port) {
		setPort(port);
	}

	public TCPConnection createConnection(Socket socket) {
		return new TCPConnection(this, socket);
	}

	protected ExecutorService createExecutor() {
		return Executors.newFixedThreadPool(getFixedThreadCount(), new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "TCPListener executor");
				t.setDaemon(true);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						Log.log(Level.WARN, "uncaught exception in " + t, e);
					}
				});
				return null;
			}
		});
	}

	protected Thread createListenerThread() {
		Thread t = new Thread(this, getName());
		t.setDaemon(true);
		return t;
	}

	protected synchronized ExecutorService getExecutor() {
		if (executor == null) {
			executor = createExecutor();
		}
		return executor;
	}

	protected int getFixedThreadCount() {
		return 3;
	}

	public String getHost() {
		return host;
	}

	public java.lang.Thread getListenerThread() {
		return listenerThread;
	}

	public java.lang.String getName() {
		return name + " on port " + Integer.toString(port);
	}

	public int getPort() {
		return port;
	}

	protected java.net.ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void handleConnection(final TCPConnection c) {
		getExecutor().submit(new Runnable() {
			@Override
			public void run() {
				handleConnectionThreaded(c);
			}
		});
	}

	public void handleConnectionThreaded(TCPConnection c) {
		c.stop();
	}

	public void handleException(Exception e) {
		Log.log(Level.SEVERE, "Socket Error, terminating Listener (" + e.getMessage() + ")");
		stop();
	}

	@Override
	public void run() {
		try {
			Socket s = null;
			while (!Thread.interrupted() && getListenerThread() != null) {
				try {
					s = getServerSocket().accept();
					TCPConnection c = createConnection(s);
					c.start();
					try {
						handleConnection(c);
					} catch (Exception e) {
						// once executing the request we ignore any failure
					}
				} catch (Exception e) {
					synchronized (this) {
						// when listener thread is null intended stop
						if (getListenerThread() != null) {
							handleException(e);
						}
					}
					break;
				}
			}
		} finally {
			stop();
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	protected void setListenerThread(java.lang.Thread newListenerThread) {
		listenerThread = newListenerThread;
	}

	public void setPort(int newPort) {
		port = newPort;
	}

	protected void setServerSocket(java.net.ServerSocket newServerSocket) {
		serverSocket = newServerSocket;
	}

	public void start() throws IOException {
		InetAddress bindAddress = StringTools.isEmpty(getHost()) ? null : InetAddress.getByName(getHost());
		setServerSocket(new ServerSocket(getPort(), 10, bindAddress));
		setPort(getServerSocket().getLocalPort());
		setListenerThread(createListenerThread());
		getListenerThread().start();
	}

	public synchronized void stop() {
		Thread l = getListenerThread();
		// mark stop to listener thread
		setListenerThread(null);
		if (l != null) {
			l.interrupt();
		}
		try {
			if (getServerSocket() != null) {
				getServerSocket().close();
			}
		} catch (IOException ignore) {
			// ignore
		}
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
	}
}
