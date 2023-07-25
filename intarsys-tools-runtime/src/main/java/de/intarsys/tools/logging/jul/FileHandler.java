/*
 * Copyright (c) 2000, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package de.intarsys.tools.logging.jul;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.functor.Args;

/*
 * modified by intarsys
 *
 * as we want to be more flexible with regards to filename patterns, the only way is to
 * completely copy and patch the standard implementation...
 *
 */
/**
 * Simple file logging {@code Handler}.
 * <p>
 * The {@code FileHandler} can either write to a specified file,
 * or it can write to a rotating set of files.
 * <p>
 * For a rotating set of files, as each file reaches a given size
 * limit, it is closed, rotated out, and a new file opened.
 * Successively older files are named by adding "0", "1", "2",
 * etc. into the base filename.
 * <p>
 * By default buffering is enabled in the IO libraries but each log
 * record is flushed out when it is complete.
 * <p>
 * By default the {@code XMLFormatter} class is used for formatting.
 * <p>
 * <b>Configuration:</b>
 * By default each {@code FileHandler} is initialized using the following
 * {@code LogManager} configuration properties where {@code <handler-name>}
 * refers to the fully-qualified class name of the handler.
 * If properties are not defined
 * (or have invalid values) then the specified default values are used.
 * <ul>
 * <li>   &lt;handler-name&gt;.level
 *        specifies the default level for the {@code Handler}
 *        (defaults to {@code Level.ALL}). </li>
 * <li>   &lt;handler-name&gt;.filter
 *        specifies the name of a {@code Filter} class to use
 *        (defaults to no {@code Filter}). </li>
 * <li>   &lt;handler-name&gt;.formatter
 *        specifies the name of a {@code Formatter} class to use
 *        (defaults to {@code java.util.logging.XMLFormatter}) </li>
 * <li>   &lt;handler-name&gt;.encoding
 *        the name of the character set encoding to use (defaults to
 *        the default platform encoding). </li>
 * <li>   &lt;handler-name&gt;.limit
 *        specifies an approximate maximum amount to write (in bytes)
 *        to any one file.  If this is zero, then there is no limit.
 *        (Defaults to no limit). </li>
 * <li>   &lt;handler-name&gt;.count
 *        specifies how many output files to cycle through (defaults to 1). </li>
 * <li>   &lt;handler-name&gt;.pattern
 *        specifies a pattern for generating the output file name.  See
 *        below for details. (Defaults to "%h/java%u.log"). </li>
 * <li>   &lt;handler-name&gt;.append
 *        specifies whether the FileHandler should append onto
 *        any existing files (defaults to false). </li>
 * <li>   &lt;handler-name&gt;.maxLocks
 *        specifies the maximum number of concurrent locks held by
 *        FileHandler (defaults to 100). </li>
 * </ul>
 * <p>
 * For example, the properties for {@code FileHandler} would be:
 * <ul>
 * <li>   java.util.logging.FileHandler.level=INFO </li>
 * <li>   java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter </li>
 * </ul>
 * <p>
 * For a custom handler, e.g. com.foo.MyHandler, the properties would be:
 * <ul>
 * <li>   com.foo.MyHandler.level=INFO </li>
 * <li>   com.foo.MyHandler.formatter=java.util.logging.SimpleFormatter </li>
 * </ul>
 * <p>
 * A pattern consists of a string that includes the following special
 * components that will be replaced at runtime:
 * <ul>
 * <li>    "/"    the local pathname separator </li>
 * <li>     "%t"   the system temporary directory </li>
 * <li>     "%h"   the value of the "user.home" system property </li>
 * <li>     "%g"   the generation number to distinguish rotated logs </li>
 * <li>     "%u"   a unique number to resolve conflicts </li>
 * <li>     "%%"   translates to a single percent sign "%" </li>
 * </ul>
 * If no "%g" field has been specified and the file count is greater
 * than one, then the generation number will be added to the end of
 * the generated filename, after a dot.
 * <p>
 * Thus for example a pattern of "%t/java%g.log" with a count of 2
 * would typically cause log files to be written on Solaris to
 * /var/tmp/java0.log and /var/tmp/java1.log whereas on Windows 95 they
 * would be typically written to C:\TEMP\java0.log and C:\TEMP\java1.log
 * <p>
 * Generation numbers follow the sequence 0, 1, 2, etc.
 * <p>
 * Normally the "%u" unique field is set to 0.  However, if the {@code FileHandler}
 * tries to open the filename and finds the file is currently in use by
 * another process it will increment the unique number field and try
 * again.  This will be repeated until {@code FileHandler} finds a file name that
 * is  not currently in use. If there is a conflict and no "%u" field has
 * been specified, it will be added at the end of the filename after a dot.
 * (This will be after any automatically added generation number.)
 * <p>
 * Thus if three processes were all trying to log to fred%u.%g.txt then
 * they  might end up using fred0.0.txt, fred1.0.txt, fred2.0.txt as
 * the first file in their rotating sequences.
 * <p>
 * Note that the use of unique ids to avoid conflicts is only guaranteed
 * to work reliably when using a local disk file system.
 *
 * @since 1.4
 */
@SuppressWarnings("all")
public class FileHandler extends StreamHandler {
	private static class InitializationErrorManager extends ErrorManager {
		Exception lastException;

		@Override
		public void error(String msg, Exception ex, int code) {
			lastException = ex;
		}
	}

    /**
     * A metered stream is a subclass of OutputStream that
     * (a) forwards all its output to a target stream
     * (b) keeps track of how many bytes have been written
     */
	private class MeteredStream extends OutputStream {
		OutputStream out;
		int written;

		MeteredStream(OutputStream out, int written) {
			this.out = out;
			this.written = written;
		}

		@Override
		public void close() throws IOException {
			out.close();
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void write(byte buff[]) throws IOException {
			out.write(buff);
			written += buff.length;
		}

		@Override
		public void write(byte buff[], int off, int len) throws IOException {
			out.write(buff, off, len);
			written += len;
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
			written++;
		}
	}

	private static final int MAX_LOCKS = 100;

	private static java.util.HashMap<String, String> locks = new java.util.HashMap<>();

	// Private native method to check if we are in a set UID program.
	private static native boolean isSetUID();

	private MeteredStream meter;
	private boolean append;
	private int limit; // zero => no limit.
	private int count;
	private String pattern;

	private String lockFileName;

	private FileOutputStream lockStream;

	private File files[];

    /**
     * Construct a default {@code FileHandler}.  This will be configured
     * entirely from {@code LogManager} properties (or their default values).
     *
     * @throws  IOException if there are IO problems opening the files.
     * @throws  SecurityException  if a security manager exists and if
     *             the caller does not have {@code LoggingPermission("control"))}.
     * @throws  NullPointerException if pattern property is an empty String.
     */
	public FileHandler() throws IOException, SecurityException {
		configure();
		openFiles();
	}

    /**
     * Initialize a {@code FileHandler} to write to the given filename.
     * <p>
     * The {@code FileHandler} is configured based on {@code LogManager}
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, and the file count is set to one.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern  the name of the output file
     * @throws  IOException if there are IO problems opening the files.
     * @throws  SecurityException  if a security manager exists and if
     *             the caller does not have {@code LoggingPermission("control")}.
     * @throws  IllegalArgumentException if pattern is an empty string
     */
	public FileHandler(String pattern) throws IOException, SecurityException {
		if (pattern.length() < 1) {
			throw new IllegalArgumentException();
		}
		configure();
		this.pattern = pattern;
		this.limit = 0;
		this.count = 1;
		openFiles();
	}

    /**
     * Initialize a {@code FileHandler} to write to the given filename,
     * with optional append.
     * <p>
     * The {@code FileHandler} is configured based on {@code LogManager}
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, the file count is set to one, and the append
     * mode is set to the given {@code append} argument.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern  the name of the output file
     * @param append  specifies append mode
     * @throws  IOException if there are IO problems opening the files.
     * @throws  SecurityException  if a security manager exists and if
     *             the caller does not have {@code LoggingPermission("control")}.
     * @throws  IllegalArgumentException if pattern is an empty string
     */
	public FileHandler(String pattern, boolean append) throws IOException, SecurityException {
		if (pattern.length() < 1) {
			throw new IllegalArgumentException();
		}
		configure();
		this.pattern = pattern;
		this.limit = 0;
		this.count = 1;
		this.append = append;
		openFiles();
	}

    /**
     * Initialize a {@code FileHandler} to write to a set of files.  When
     * (approximately) the given limit has been written to one file,
     * another file will be opened.  The output will cycle through a set
     * of count files.
     * <p>
     * The {@code FileHandler} is configured based on {@code LogManager}
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to the limit argument, and the file count is set to the
     * given count argument.
     * <p>
     * The count must be at least 1.
     *
     * @param pattern  the pattern for naming the output file
     * @param limit  the maximum number of bytes to write to any one file
     * @param count  the number of files to use
     * @throws  IOException if there are IO problems opening the files.
     * @throws  SecurityException  if a security manager exists and if
     *             the caller does not have {@code LoggingPermission("control")}.
     * @throws  IllegalArgumentException if {@code limit < 0}, or {@code count < 1}.
     * @throws  IllegalArgumentException if pattern is an empty string
     */
	public FileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
		if (limit < 0 || count < 1 || pattern.length() < 1) {
			throw new IllegalArgumentException();
		}
		configure();
		this.pattern = pattern;
		this.limit = limit;
		this.count = count;
		openFiles();
	}

    /**
     * Initialize a {@code FileHandler} to write to a set of files
     * with optional append.  When (approximately) the given limit has
     * been written to one file, another file will be opened.  The
     * output will cycle through a set of count files.
     * <p>
     * The {@code FileHandler} is configured based on {@code LogManager}
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to the limit argument, and the file count is set to the
     * given count argument, and the append mode is set to the given
     * {@code append} argument.
     * <p>
     * The count must be at least 1.
     *
     * @param pattern  the pattern for naming the output file
     * @param limit  the maximum number of bytes to write to any one file
     * @param count  the number of files to use
     * @param append  specifies append mode
     * @throws  IOException if there are IO problems opening the files.
     * @throws  SecurityException  if a security manager exists and if
     *             the caller does not have {@code LoggingPermission("control")}.
     * @throws  IllegalArgumentException if {@code limit < 0}, or {@code count < 1}.
     * @throws  IllegalArgumentException if pattern is an empty string
     */
	public FileHandler(String pattern, int limit, int count, boolean append) throws IOException, SecurityException {
		if (limit < 0 || count < 1 || pattern.length() < 1) {
			throw new IllegalArgumentException();
		}
		configure();
		this.pattern = pattern;
		this.limit = limit;
		this.count = count;
		this.append = append;
		openFiles();
	}

    /**
     * Close all the files.
     *
     * @throws  SecurityException  if a security manager exists and if
     *             the caller does not have {@code LoggingPermission("control")}.
     */
	@Override
	public synchronized void close() throws SecurityException {
		super.close();
		// Unlock any lock file.
		if (lockFileName == null) {
			return;
		}
		try {
			// Closing the lock file's FileOutputStream will close
			// the underlying channel and free any locks.
			lockStream.close();
		} catch (Exception ex) {
			// Problems closing the stream. Punt.
		}
		synchronized (locks) {
			locks.remove(lockFileName);
		}
		new File(lockFileName).delete();
		lockFileName = null;
		lockStream = null;
	}

	// Private method to configure a FileHandler from LogManager
	// properties and/or default values as specified in javadoc.
	private void configure() {
		LogManager manager = LogManager.getLogManager();

		String cname = getClass().getName();

		pattern = getStringProperty(cname + ".pattern", "%h/java%u.log");
		try {
			pattern = (String) TemplateEvaluator.get(Mode.TRUSTED).evaluate(pattern, Args.create());
		} catch (EvaluationException e) {
			// can't help that; ignore
		}
		limit = getIntProperty(cname + ".limit", 0);
		if (limit < 0) {
			limit = 0;
		}
		count = getIntProperty(cname + ".count", 1);
		if (count <= 0) {
			count = 1;
		}
		append = getBooleanProperty(cname + ".append", false);
		setLevel(getLevelProperty(cname + ".level", Level.ALL));
		setFilter(getFilterProperty(cname + ".filter", null));
		setFormatter(getFormatterProperty(cname + ".formatter", new XMLFormatter()));
		try {
			setEncoding(getStringProperty(cname + ".encoding", null));
		} catch (Exception ex) {
			try {
				setEncoding(null);
			} catch (Exception e) {
				// doing a setEncoding with null should always work.
				// assert false;
			}
		}
	}

	// Generate a filename from a pattern.
	private File generate(String pattern, int generation, int unique) throws IOException {
		File file = null;
		String word = "";
		int ix = 0;
		boolean sawg = false;
		boolean sawu = false;
		while (ix < pattern.length()) {
			char ch = pattern.charAt(ix);
			ix++;
			char ch2 = 0;
			if (ix < pattern.length()) {
				ch2 = Character.toLowerCase(pattern.charAt(ix));
			}
			if (ch == '/') {
				if (file == null) {
					file = new File(word);
				} else {
					file = new File(file, word);
				}
				word = "";
				continue;
			} else if (ch == '%') {
				if (ch2 == 't') {
					String tmpDir = System.getProperty("java.io.tmpdir");
					if (tmpDir == null) {
						tmpDir = System.getProperty("user.home");
					}
					file = new File(tmpDir);
					ix++;
					word = "";
					continue;
				} else if (ch2 == 'h') {
					file = new File(System.getProperty("user.home"));
					if (isSetUID()) {
						// Ok, we are in a set UID program. For safety's sake
						// we disallow attempts to open files relative to %h.
						throw new IOException("can't use %h in set UID program");
					}
					ix++;
					word = "";
					continue;
				} else if (ch2 == 'g') {
					word = word + generation;
					sawg = true;
					ix++;
					continue;
				} else if (ch2 == 'u') {
					word = word + unique;
					sawu = true;
					ix++;
					continue;
				} else if (ch2 == '%') {
					word = word + "%";
					ix++;
					continue;
				}
			}
			word = word + ch;
		}
		if (count > 1 && !sawg) {
			word = word + "." + generation;
		}
		if (unique > 0 && !sawu) {
			word = word + "." + unique;
		}
		if (word.length() > 0) {
			if (file == null) {
				file = new File(word);
			} else {
				file = new File(file, word);
			}
		}
		return file;
	}

	protected boolean getBooleanProperty(String name, boolean defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		val = val.toLowerCase();
		if ("true".equals(val) || "1".equals(val)) {
			return true;
		} else if ("false".equals(val) || "0".equals(val)) {
			return false;
		}
		return defaultValue;
	}

	protected Filter getFilterProperty(String name, Filter defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		try {
			if (val != null) {
				Class clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Filter) clz.newInstance();
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception. Return the defaultValue.
		return defaultValue;
	}

	protected Formatter getFormatterProperty(String name, Formatter defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		try {
			if (val != null) {
				Class clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Formatter) clz.newInstance();
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception. Return the defaultValue.
		return defaultValue;
	}

	protected int getIntProperty(String name, int defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	protected Level getLevelProperty(String name, Level defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		Level l = Level.parse(val.trim());
		return l != null ? l : defaultValue;
	}

	protected String getStringProperty(String name, String defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		return val.trim();
	}

	private void open(File fname, boolean append) throws IOException {
		int len = 0;
		if (append) {
			len = (int) fname.length();
		}
		FileOutputStream fout = new FileOutputStream(fname.toString(), append);
		BufferedOutputStream bout = new BufferedOutputStream(fout);
		meter = new MeteredStream(bout, len);
		setOutputStream(meter);
	}

	// Private method to open the set of output files, based on the
	// configured instance variables.
	private void openFiles() throws IOException {
		LogManager manager = LogManager.getLogManager();
		if (count < 1) {
			throw new IllegalArgumentException("file count = " + count);
		}
		if (limit < 0) {
			limit = 0;
		}

		// We register our own ErrorManager during initialization
		// so we can record exceptions.
		InitializationErrorManager em = new InitializationErrorManager();
		setErrorManager(em);

		// Create a lock file. This grants us exclusive access
		// to our set of output files, as long as we are alive.
		int unique = -1;
		for (;;) {
			unique++;
			if (unique > MAX_LOCKS) {
				throw new IOException("Couldn't get lock for " + pattern);
			}
			// Generate a lock file name from the "unique" int.
			lockFileName = generate(pattern, 0, unique).toString() + ".lck";
			// Now try to lock that filename.
			// Because some systems (e.g., Solaris) can only do file locks
			// between processes (and not within a process), we first check
			// if we ourself already have the file locked.
			synchronized (locks) {
				if (locks.get(lockFileName) != null) {
					// We already own this lock, for a different FileHandler
					// object. Try again.
					continue;
				}
				FileChannel fc;
				try {
					lockStream = new FileOutputStream(lockFileName);
					fc = lockStream.getChannel();
				} catch (IOException e) {
					// We got an IOException while trying to open the file.
					// Try the next file.
					continue;
				}
				boolean available;
				try {
					available = fc.tryLock() != null;
					// We got the lock OK.
				} catch (IOException e) {
					// We got an IOException while trying to get the lock.
					// This normally indicates that locking is not supported
					// on the target directory. We have to proceed without
					// getting a lock. Drop through.
					available = true;
				}
				if (available) {
					// We got the lock. Remember it.
					locks.put(lockFileName, lockFileName);
					break;
				}

				// We failed to get the lock. Try next file.
				fc.close();
			}
		}

		files = new File[count];
		for (int i = 0; i < count; i++) {
			files[i] = generate(pattern, i, unique);
		}

		// Create the initial log file.
		if (append) {
			open(files[0], true);
		} else {
			rotate();
		}

		// Did we detect any exceptions during initialization?
		Exception ex = em.lastException;
		if (ex != null) {
			if (ex instanceof IOException) {
				throw (IOException) ex;
			} else if (ex instanceof SecurityException) {
				throw (SecurityException) ex;
			} else {
				throw new IOException("Exception: " + ex);
			}
		}

		// Install the normal default ErrorManager.
		setErrorManager(new ErrorManager());
	}

    /**
     * Format and publish a {@code LogRecord}.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
	@Override
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		super.publish(record);
		flush();
		if (limit > 0 && meter.written >= limit) {
			// We performed access checks in the "init" method to make sure
			// we are only initialized from trusted code. So we assume
			// it is OK to write the target files, even if we are
			// currently being called from untrusted code.
			// So it is safe to raise privilege here.
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				@Override
				public Object run() {
					rotate();
					return null;
				}
			});
		}
	}

	// Rotate the set of output files
	private synchronized void rotate() {
		Level oldLevel = getLevel();
		setLevel(Level.OFF);

		super.close();
		for (int i = count - 2; i >= 0; i--) {
			File f1 = files[i];
			File f2 = files[i + 1];
			if (f1.exists()) {
				if (f2.exists()) {
					f2.delete();
				}
				f1.renameTo(f2);
			}
		}
		try {
			open(files[0], false);
		} catch (IOException e) {
			// We don't want to throw an exception here, but we
			// report the exception to any registered ErrorManager.
			reportError(null, e, ErrorManager.OPEN_FAILURE);

		}
		setLevel(oldLevel);
	}
}
