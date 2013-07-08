package de.intarsys.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.dom.PACKAGE;
import de.intarsys.tools.stream.StreamTools;

/**
 * A snapshot of the file system state.
 * <p>
 * The target file and substructure state is "frozen" and can be checked for
 * changes.
 * 
 */
public class FileSnapshot {

	private static final Logger Log = PACKAGE.Log;

	final private File file;

	private List<FileSnapshot> children;

	final private boolean directory;

	private long fileLength = 0;

	private long lastModified = 0;

	public FileSnapshot(File file) {
		super();
		this.file = file;
		this.directory = file.isDirectory();
		updateLocal(file.length(), file.lastModified());
		File[] tempFiles = file.listFiles();
		if (tempFiles != null) {
			updateChildren(Arrays.asList(tempFiles));
		}
	}

	public FileSnapshot[] getChildren() {
		if (children == null) {
			return null;
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

	public boolean isAvailable() {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "checking availability of file "
					+ getFile().getAbsolutePath());
		}
		if (children != null) {
			Iterator<FileSnapshot> it = children.iterator();
			while (it.hasNext()) {
				FileSnapshot child = it.next();
				if (!child.isAvailable()) {
					return false;
				}
			}
			return true;
		}
		if (directory) {
			return !isLost();
		}
		FileOutputStream os = null;
		FileLock lock = null;
		try {
			os = new FileOutputStream(getFile(), true);
			lock = os.getChannel().tryLock();
			if (lock != null) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.log(Level.FINEST, "file " + getFile().getAbsolutePath()
							+ " available");
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
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "file " + getFile().getAbsolutePath()
					+ " not available");
		}
		return false;
	}

	public boolean isChanged() {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "checking file "
					+ getFile().getAbsolutePath() + " for changes");
		}
		long newLastModified = getFile().lastModified();
		long newLength = getFile().length();
		File[] tempFiles = getFile().listFiles();
		List<File> newFiles = tempFiles == null ? null : new ArrayList<File>(
				Arrays.asList(tempFiles));
		boolean exists = getFile().exists();

		if (!exists) {
			children = null;
			return true;
		}

		boolean changed = false;
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
				if (newFiles != null) {
					newFiles.remove(child.getFile());
				}
			}
		}

		if (newFiles != null && newFiles.size() > 0) {
			updateChildren(newFiles);
			changed = true;
		}

		if (newLastModified != lastModified || newLength != fileLength) {
			updateLocal(newLength, newLastModified);
			changed = true;
		}

		if (Log.isLoggable(Level.FINEST)) {
			if (changed) {
				Log.log(Level.FINEST, "file " + getFile().getAbsolutePath()
						+ " changed");
			} else {
				Log.log(Level.FINEST, "file " + getFile().getAbsolutePath()
						+ " unchanged");
			}
		}
		return changed;
	}

	public boolean isLost() {
		return !getFile().exists();
	}

	protected void updateChildren(List<File> newFiles) {
		if (children == null) {
			children = new ArrayList<FileSnapshot>();
		}
		for (File file : newFiles) {
			children.add(new FileSnapshot(file));
		}
	}

	protected void updateLocal(long newLength, long newModified) {
		this.fileLength = newLength;
		this.lastModified = newModified;
	}

}
