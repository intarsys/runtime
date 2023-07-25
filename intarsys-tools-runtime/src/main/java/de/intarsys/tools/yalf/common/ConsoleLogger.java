package de.intarsys.tools.yalf.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.api.Yalf;

@SuppressWarnings({
		"java:S106", //
		"java:S4349" // no need for redefinition of write
})
public class ConsoleLogger extends OutputStream {

	/**
	 * Dirty hack to get rid of synchronization
	 * 
	 */
	static class PrintStreamFacade extends PrintStream {

		private final PrintStream ps;

		private boolean reentrant;

		PrintStreamFacade(PrintStream ps) {
			super(ps);
			this.ps = ps;
		}

		@Override
		public void close() {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.close();
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void flush() {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.flush();
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println() {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println();
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(boolean x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(char x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(char[] x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(double x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(float x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(int x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(long x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(Object x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void println(String x) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.println(x);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.write(buf, off, len);
			} finally {
				reentrant = false;
			}
		}

		@Override
		public void write(int b) {
			if (reentrant) {
				return;
			}
			reentrant = true;
			try {
				ps.write(b);
			} finally {
				reentrant = false;
			}
		}

	}

	public static final PrintStream OUT = System.out;

	public static final PrintStream ERR = System.err;

	public static void install() {
		install(Yalf.get().getLogger("System.out"), Yalf.get().getLogger("System.err"));
	}

	public static void install(ILogger out, ILogger err) {
		System.setOut(new PrintStreamFacade(new PrintStream(new ConsoleLogger(out, Level.DEBUG, System.out), true)));
		System.setErr(new PrintStreamFacade(new PrintStream(new ConsoleLogger(err, Level.SEVERE, System.err), true)));
	}

	private final ILogger logger;

	private final Level logLevel;

	private final OutputStream outputStream;

	private ByteArrayOutputStream os = new ByteArrayOutputStream();

	private CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

	public ConsoleLogger(ILogger logger, Level logLevel, OutputStream outputStream) {
		this.logger = logger;
		this.logLevel = logLevel;
		this.outputStream = outputStream;
	}

	@Override
	public void write(int b) throws IOException {
		if (b == '\r') {
			// ignore
			return;
		}
		if (b == '\n') {
			// flush - do not toByteArray WITHOUT this \n -> Bug in
			// com.sun.deploy.trace.TraceStream
			os.write('\n');
			byte[] bytes = os.toByteArray();
			os.reset();
			outputStream.write(bytes);
			logger.log(logLevel, decoder.decode(ByteBuffer.wrap(bytes, 0, bytes.length - 1)).toString());
			return;
		}
		os.write(b);
	}
}