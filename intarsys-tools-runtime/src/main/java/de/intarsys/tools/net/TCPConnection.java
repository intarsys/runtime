/*
 * intarsys GmbH
 * all rights reserved
 *
 */

package de.intarsys.tools.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * A connection spawned from a {@link TCPListener}.
 */
public class TCPConnection {

	// the listener that created the connection
	private TCPListener listener;

	// the connection data
	private Socket socket;
	private InputStream input;
	private OutputStream output;

	public TCPConnection(TCPListener newListener, Socket newSocket) {
		super();
		setListener(newListener);
		setSocket(newSocket);
	}

	public InputStream getInput() {
		return input;
	}

	public TCPListener getListener() {
		return listener;
	}

	public java.io.OutputStream getOutput() {
		return output;
	}

	public java.lang.String getRemoteAddr() {
		return socket.getInetAddress().getHostAddress();
	}

	public java.lang.String getRemoteHost() {
		return socket.getInetAddress().getHostName();
	}

	public java.lang.String getServerName() {
		return getSocket().getLocalAddress().getHostName();
	}

	public int getServerPort() {
		return getListener().getPort();
	}

	public java.net.Socket getSocket() {
		return socket;
	}

	private void setInput(InputStream newInput) {
		input = newInput;
	}

	private void setListener(TCPListener newListener) {
		listener = newListener;
	}

	private void setOutput(java.io.OutputStream newOutput) {
		output = newOutput;
	}

	private void setSocket(java.net.Socket newSocket) {
		socket = newSocket;
	}

	public void start() throws SocketException, IOException {
		socket.setSoTimeout(120000);
		setInput(socket.getInputStream());
		setOutput(socket.getOutputStream());
	}

	public void stop() {
		try {
			getOutput().close();
			output = null;
		} catch (IOException ignore) {
			// ignore
		}
		try {
			getInput().close();
			input = null;
		} catch (IOException ignore) {
			// ignore
		}
		try {
			getSocket().close();
			socket = null;
		} catch (IOException ignore) {
			// ignore
		}
	}
}
