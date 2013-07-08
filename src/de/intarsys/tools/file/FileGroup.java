package de.intarsys.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.attachment.Attachment;
import de.intarsys.tools.expression.CacheResolver;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.file.FileTools.Lock;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.locator.FileLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.logging.LogTools;
import de.intarsys.tools.stream.StreamTools;

/**
 * A group of files belonging together in the sense that there's a master file
 * combined with a bunch of attachments.
 * 
 * The master file is handled as a descendant of root.
 * 
 */
public class FileGroup {

	private File master;

	private File root;

	private List<Attachment> attachments = new ArrayList<>();

	private static final Logger Log = LogTools.getLogger(FileGroup.class);

	public FileGroup(File master) {
		super();
		this.master = master;
	}

	public FileGroup(File root, File master) {
		super();
		this.root = root;
		this.master = master;
	}

	protected File createTargetFile(File dir, File file, String template,
			IStringEvaluator resolver) throws IOException {
		String extensionPrefix = FileTools.getExtension(master);
		IStringEvaluator filenameResolver = new MapResolver().define("file",
				new FilenameResolver(file, extensionPrefix));
		IStringEvaluator evaluator = TaggedStringEvaluator.decorate(
				filenameResolver, resolver);
		String tempName;
		try {
			tempName = (String) evaluator.evaluate(template, Args.create());
		} catch (Exception ee) {
			tempName = template;
		}
		tempName = FileTools.trimPath(tempName);
		File tempFile = FileTools.resolvePath(dir, tempName);
		File parentFile = tempFile.getParentFile();
		if (parentFile != null) {
			FileTools.checkDirectory(parentFile, true, true, true);
		}
		return tempFile;
	}

	protected File createTargetFile(File destination, Object item,
			String template, IStringEvaluator resolver) throws IOException {
		Object temp = item;
		if (temp instanceof Attachment) {
			temp = ((Attachment) item).getAttached();
		}
		if (temp instanceof ILocatorSupport) {
			temp = ((ILocatorSupport) temp).getLocator();
		}
		if (temp instanceof FileLocator) {
			// avoid streaming
			temp = ((FileLocator) temp).getFile();
		}
		if (temp instanceof File) {
			File tempFile = (File) temp;
			return createTargetFile(destination, tempFile, template, resolver);
		}
		// todo review locator handling
		if (temp instanceof ILocator) {
			ILocator tempLocator = (ILocator) temp;
			return createTargetFile(destination,
					new File(tempLocator.getFullName()), template, resolver);
		}
		return null;
	}

	public void delete() throws IOException {
		for (Attachment attachment : attachments) {
			delete(attachment);
		}
		delete(master);
	}

	protected void delete(Object item) throws IOException {
		Object temp = item;
		if (temp instanceof Attachment) {
			temp = ((Attachment) temp).getAttached();
		}
		if (temp instanceof ILocatorSupport) {
			temp = ((ILocatorSupport) temp).getLocator();
		}
		if (temp instanceof FileLocator) {
			// avoid streaming
			temp = ((FileLocator) temp).getFile();
		}
		if (temp instanceof File) {
			File tempFile = (File) temp;
			if (tempFile.exists()) {
				FileTools.delete(tempFile);
			} else {
				if (Log.isLoggable(Level.FINE)) {
					Log.log(Level.FINE,
							"file group item '" + tempFile.getAbsolutePath()
									+ "' no longer available");
				}
			}
		}
		if (temp instanceof ILocator) {
			ILocator tempLocator = (ILocator) temp;
			if (tempLocator.exists()) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.log(Level.FINEST,
							"locator move delete '" + tempLocator.getFullName()
									+ "'");
				}
				tempLocator.delete();
			} else {
				Log.log(Level.FINE,
						"file group item '" + tempLocator.getFullName()
								+ "' no longer available");
			}
		}
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public File getMaster() {
		return master;
	}

	public File getRoot() {
		return root;
	}

	/**
	 * Move the file group to the destination directory.
	 * 
	 * The master file and any attachment is moved into the destination, offset
	 * by "relativePath", regardless of the current location.
	 * 
	 * The move operation is performed "atomically" in the sense that a
	 * cooperative {@link Lock} is acquired on the master before moving.
	 * 
	 * A collision detection is performed before moving - in case of collision,
	 * the move fails with a {@link CollisionException} before any file
	 * operation takes place.
	 * 
	 * @param deleteSubdirectories
	 *            TODO
	 * @param directory
	 * 
	 * @throws IOException
	 * @throws CollisionException
	 */
	public void move(File destination, String template,
			IStringEvaluator resolver, boolean deleteSource,
			boolean deleteSubdirectories, boolean keepLastModified)
			throws IOException {
		// keep dynamic evaluation results constant for all attachments
		IStringEvaluator cacheResolver = new CacheResolver(resolver);
		Lock lock = null;
		try {
			File masterTarget = createTargetFile(destination, master, template,
					cacheResolver);
			if (masterTarget.equals(master)) {
				// no move operation requested
				return;
			}
			if (masterTarget.exists()) {
				throw new CollisionException("'" + masterTarget + "' exists");
			}
			lock = FileTools.lock(masterTarget);
			if (lock == null) {
				throw new CollisionException("'" + masterTarget
						+ "' lock exists");
			}
			// now, even after locking we must be prepared for collisions on the
			// attachments. This may happen when the master was not included in
			// a move operation last time.
			for (Attachment attachment : attachments) {
				File attachTarget = createTargetFile(destination, attachment,
						template, cacheResolver);
				if (attachTarget.exists()) {
					throw new CollisionException("'" + attachTarget
							+ "' exists");
				}
			}
			List<Attachment> tempAttachments = new ArrayList<>();
			// now everything is fine, we can start moving around
			for (Attachment attachment : attachments) {
				File attachTarget = createTargetFile(destination, attachment,
						template, cacheResolver);
				Attachment tempAttachment = move(attachment, attachTarget,
						deleteSource, keepLastModified);
				tempAttachments.add(tempAttachment);
			}
			// master is moved last to act as indicator
			if (masterTarget.exists()) {
				// well, someone created an attachment with same name
				// this is intended, do not overwrite...
				if (deleteSource) {
					FileTools.delete(master);
				}
			} else {
				move(master, masterTarget, deleteSource, keepLastModified);
			}
			if (deleteSubdirectories) {
				FileTools.deleteEmptyDirectories(getRoot(), master);
			}
			attachments = tempAttachments;
			master = masterTarget;
			root = destination;
		} finally {
			if (lock != null) {
				lock.release();
			}
		}
	}

	protected Attachment move(Object item, File target, boolean deleteSource,
			boolean keepLastModified) throws IOException {
		Object temp = item;
		Attachment attachment = null;
		if (temp instanceof Attachment) {
			attachment = (Attachment) temp;
			temp = ((Attachment) temp).getAttached();
		}
		if (temp instanceof ILocatorSupport) {
			temp = ((ILocatorSupport) temp).getLocator();
		}
		if (temp instanceof FileLocator) {
			// avoid streaming
			temp = ((FileLocator) temp).getFile();
		}
		if (temp instanceof File) {
			File tempFile = (File) temp;
			if (tempFile.exists()) {
				if (deleteSource) {
					long lastModified = 0;
					if (keepLastModified) {
						lastModified = tempFile.lastModified();
					}
					FileTools.renameFile(tempFile, target);
					if (lastModified != 0) {
						target.setLastModified(lastModified);
					}
				} else {
					FileTools.copyFile(tempFile, target);
				}
				return new Attachment(target.getName(), target);
			} else {
				if (Log.isLoggable(Level.FINE)) {
					Log.log(Level.FINE,
							"file group item '" + tempFile.getAbsolutePath()
									+ "' no longer available");
				}
				return null;
			}
		}
		if (temp instanceof ILocator) {
			ILocator tempLocator = (ILocator) temp;
			if (tempLocator.exists()) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.log(Level.FINEST, "locator move create target file '"
							+ target.getAbsolutePath() + "'");
				}
				InputStream is = null;
				OutputStream os = null;
				try {
					is = tempLocator.getInputStream();
					os = new FileOutputStream(target);
					StreamTools.copyStream(is, os);
					if (Log.isLoggable(Level.FINEST)) {
						Log.log(Level.FINEST, "locator move success");
					}
				} catch (Exception e) {
					if (Log.isLoggable(Level.FINE)) {
						Log.log(Level.FINE, "locator move failed");
					}
					throw new IOException("locator move failed for '"
							+ tempLocator.getFullName() + "' to '"
							+ target.getAbsolutePath() + "'", e);
				} finally {
					StreamTools.close(is);
					StreamTools.close(os);
				}
				if (deleteSource) {
					if (Log.isLoggable(Level.FINEST)) {
						Log.log(Level.FINEST, "locator move delete '"
								+ tempLocator.getFullName() + "'");
					}
					tempLocator.delete();
				}
				return new Attachment(target.getName(), target);
			} else {
				Log.log(Level.FINE,
						"file group item '" + tempLocator.getFullName()
								+ "' no longer available");
				return null;
			}
		}
		return attachment;
	}

}
