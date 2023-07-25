package de.intarsys.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * A snapshot of the file system state.
 * <p>
 * The target file and substructure state is "frozen" and can be checked for
 * changes.
 * 
 */
public class FileSnapshot {

	private static final ILogger Log = PACKAGE.Log;

	private final File file;

	private List<FileSnapshot> children;

	private boolean logged = false;

	private long fileLength;

	private long lastModified;

	public FileSnapshot(File file) {
		super();
		this.file = file;
		long newLastModified = getFile().lastModified();
		long newLength = getFile().length();
		if (Log.isLoggable(Level.TRACE)) {
			Log.log(Level.TRACE, "snapshot of " + getFile().getAbsolutePath());
		}
		updateLocal(newLength, newLastModified);
		File[] tempFiles = file.listFiles();
		if (tempFiles != null) {
			updateChildren(Arrays.asList(tempFiles));
		}
	}

	public FileSnapshot[] getChildren() {
		if (children == null) {
			return new FileSnapshot[0];
		}
		return children.toArray(new FileSnapshot[children.size()]);
	}

	public File getFile() {
		return file;
	}

	public long getFileLength() {
		return fileLength;
	}

	public long getLastModified() {
		return lastModified;
	}

	/**
	 * true if the snapshot files are "available" for use by a reader/writer (not
	 * locked by any other client).
	 * 
	 * @return true when snapshot available
	 */
	public boolean isAvailable() {
		if (children != null) {
			Iterator<FileSnapshot> it = children.iterator();
			while (it.hasNext()) {
				FileSnapshot child = it.next();
				if (!child.isAvailable()) {
					if (Log.isLoggable(Level.TRACE)) {
						Log.log(Level.TRACE, "snapshot not available " + getFile().getAbsolutePath());
					}
					return false;
				}
			}
			// this is a directory, bail out
			if (Log.isLoggable(Level.TRACE)) {
				Log.log(Level.TRACE, "snapshot available " + getFile().getAbsolutePath());
			}
			return true;
		}
		if (!getFile().exists()) {
			if (Log.isLoggable(Level.TRACE)) {
				Log.log(Level.TRACE, "snapshot available " + getFile().getAbsolutePath());
			}
			return true;
		}
		FileOutputStream os = null;
		FileLock lock = null;
		try {
			os = new FileOutputStream(getFile(), true);
			lock = os.getChannel().tryLock();
			if (lock != null) {
				if (Log.isLoggable(Level.TRACE)) {
					Log.log(Level.TRACE, "snapshot available " + getFile().getAbsolutePath());
				}
				return true;
			}
		} catch (Exception e) {
			// file not available
		} finally {
			if (lock != null) {
				try {
					lock.release();
				} catch (IOException e) {
					// ignore
				}
			}
			StreamTools.close(os);
		}
		Level level = logged ? Level.TRACE : Level.INFO;
		logged = true;
		Log.log(level, "snapshot not available " + getFile().getAbsolutePath());
		return false;
	}

	/**
	 * true if the snapshot has changed since the last inspection via "isChanged"
	 * 
	 * @return true if snapshot changed
	 */
	public boolean isChanged() {
		long newLastModified = getFile().lastModified();
		long newLength = getFile().length();
		File[] tempFiles = getFile().listFiles();
		List<File> newFiles = tempFiles == null ? Collections.emptyList() : new ArrayList<>(Arrays.asList(tempFiles));

		boolean changed = false;

		boolean exists = getFile().exists();
		if (!exists) {
			children = null;
			changed = true;
		}

		if (children != null) {
			Iterator<FileSnapshot> it = children.iterator();
			while (it.hasNext()) {
				FileSnapshot child = it.next();
				if (child.isChanged()) {
					changed = true;
					if (child.isLost()) {
						it.remove();
					}
				}
				newFiles.remove(child.getFile());
			}
		}

		if (!newFiles.isEmpty()) {
			updateChildren(newFiles);
			changed = true;
		}

		if (newLastModified != lastModified || newLength != fileLength) {
			updateLocal(newLength, newLastModified);
			changed = true;
		}

		if (Log.isLoggable(Level.TRACE)) {
			if (changed) {
				Log.log(Level.TRACE, "snapshot changed " + getFile().getAbsolutePath());
			} else {
				Log.log(Level.TRACE, "snapshot unchanged " + getFile().getAbsolutePath());
			}
		}
		return changed;
	}

	public boolean isLost() {
		return !getFile().exists();
	}

	@Override
	public String toString() {
		return file.toString();
	}

	protected void updateChildren(List<File> files) {
		if (children == null) {
			children = new ArrayList<>();
		}
		for (File newFile : files) {
			children.add(new FileSnapshot(newFile));
		}
	}

	protected void updateLocal(long newLength, long newModified) {
		this.fileLength = newLength;
		this.lastModified = newModified;
	}

}
