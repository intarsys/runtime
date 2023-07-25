package de.intarsys.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.attachment.Attachment;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.expression.CacheResolver;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.file.FileTools.Lock;
import de.intarsys.tools.locator.FileLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * A group of files belonging together in the sense that there's a master file
 * combined with a bunch of attachments.
 *
 * The master file is handled as a descendant of root.
 *
 */
public class FileGroup {

	private static final ILogger Log = LogTools.getLogger(FileGroup.class);

	private File master;

	private File root;

	private List<Attachment> attachments = new ArrayList<>();

	public FileGroup(File master) {
		super();
		this.master = master;
	}

	public FileGroup(File root, File master) {
		super();
		this.root = root;
		this.master = master;
	}

	/**
	 * Create a copy of the item as an {@link Attachment} or null if nothing is copied.
	 * 
	 * @param item
	 * @param target
	 * @param keepLastModified
	 * @return
	 * @throws IOException
	 */
	protected Attachment copy(Object item, File target)
			throws IOException {
		Object attached = item;
		if (item instanceof Attachment) {
			attached = ((Attachment) attached).getAttached();
		}
		if (attached instanceof ILocatorSupport) {
			attached = ((ILocatorSupport) attached).getLocator();
		}
		if (attached instanceof FileLocator) {
			// avoid streaming
			attached = ((FileLocator) attached).getFile();
		}
		if (attached instanceof File) {
			return copyFile((File) attached, target);
		}
		if (attached instanceof ILocator) {
			return copyLocator((ILocator) attached, target);
		}
		return null;
	}

	protected Attachment copyFile(File source, File target)
			throws IOException {
		if (FileTools.equals(source, target)) {
			return null;
		} else if (source.exists()) {
			FileTools.copyFile(source, target);
			return new Attachment(target.getName(), target);
		} else {
			Log.debug("{} item '{}' no longer available", getLogId(), source);
			return null;
		}
	}

	protected Attachment copyLocator(ILocator source, File target) throws IOException {
		if (source.exists()) {
			Log.trace("{} locator copy to '{}'", getLogId(), target.getAbsolutePath());
			InputStream is = null;
			OutputStream os = null;
			try {
				is = source.getInputStream();
				os = new FileOutputStream(target);
				StreamTools.copy(is, os);
				if (Log.isLoggable(Level.TRACE)) {
					Log.log(Level.TRACE, "locator move success");
				}
			} catch (Exception e) {
				Log.debug("{} locator copy failed", getLogId());
				throw new IOException("locator move failed for '"
						+ source.getPath() + "' to '"
						+ target.getAbsolutePath() + "'", e);
			} finally {
				StreamTools.close(is);
				StreamTools.close(os);
			}
			return new Attachment(target.getName(), target);
		} else {
			Log.debug("{} item '{}' no longer available", getLogId(), source.getPath());
			return null;
		}
	}

	/**
	 * Create a target file for use in targetDir that will receive the input
	 * file when moved. The file is not copied yet!
	 */
	protected File createTargetFile(File dir, File file, String template, IStringEvaluator resolver)
			throws IOException {
		String extensionPrefix = FileTools.getExtension(master);
		IStringEvaluator filenameResolver = new MapResolver().put("file", new FilenameResolver(file, extensionPrefix));
		IStringEvaluator evaluator = TaggedStringEvaluator.decorate(filenameResolver, resolver);
		String tempName = StringEvaluatorTools.evaluateString(evaluator, template);
		tempName = FileTools.trimPath(tempName);
		File tempFile = FileTools.resolvePath(dir, tempName);
		File parentFile = tempFile.getParentFile();
		if (parentFile != null) {
			FileTools.checkDirectory(parentFile, true, true, true);
		}
		return tempFile;
	}

	protected File createTargetFile(File destination, Object item, String template, IStringEvaluator resolver)
			throws IOException {
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
		// todo generalize locator usage
		if (temp instanceof ILocator) {
			ILocator tempLocator = (ILocator) temp;
			return createTargetFile(destination, new File(tempLocator.getPath()), template, resolver);
		}
		return null;
	}

	public void delete() throws IOException {
		deleteAll(attachments);
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
			FileTools.delete((File) temp);
		}
		if (temp instanceof ILocator) {
			ILocator tempLocator = (ILocator) temp;
			if (tempLocator.exists()) {
				Log.trace("{} locator delete '{}' ", getLogId(), tempLocator.getPath());
				tempLocator.delete();
			} else {
				Log.debug("{} locator skip '{}'", getLogId(), tempLocator.getPath());
			}
		}
	}

	protected void deleteAll(List<?> deletions) throws IOException {
		for (Object object : deletions) {
			delete(object);
		}
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	protected Object getLogId() {
		return "FileGroup " + master;
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
	 * The master file and any attachment is moved into the destination, offset by
	 * "relativePath", regardless of the current location.
	 * 
	 * The move operation is performed "atomically" in the sense that a cooperative
	 * {@link Lock} is acquired on the master before moving.
	 * 
	 * A collision detection is performed before moving - in case of collision, the
	 * move fails with a {@link CollisionException} before any file operation takes
	 * place.
	 * 
	 * @param targetDir            The directory to transfer the group into
	 * @param template             Use this template to create a new filename in
	 *                             targetDir, based on source file
	 * @param resolver             The resolver used for expanding the template
	 * @param deleteSource         if true, source is deleted after move
	 * @param deleteSubdirectories if true, empty source directories are deleted
	 *                             after move
	 * @param collisionDetect      if true, destination is checked for collision
	 *                             before move
	 * @throws IOException
	 */
	public void move(File targetDir, String template,
			IStringEvaluator resolver, boolean deleteSource,
			boolean deleteSubdirectories,
			boolean collisionDetect) throws IOException {
		int i = 3;
		while (--i >= 0) {
			try {
				moveRetry(targetDir, template, resolver, deleteSource, deleteSubdirectories, collisionDetect);
				return;
			} catch (IOException e) {
				/**
				 * we have installed a retry mechanism because we encountered errors caused by
				 * 3rd party (e.g. virus scanner) on a regular base.
				 */
				if (i == 0) {
					throw e;
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					throw e;
				}
				Log.debug("{} retry ('{}')", getLogId(), ExceptionTools.getMessage(e));
			}
		}
	}

	protected void moveRetry(File targetDir, String template, IStringEvaluator resolver, boolean deleteSource,
			boolean deleteSubdirectories, boolean collisionDetect)
			throws IOException {
		Log.debug("{} move to '{}'", getLogId(), targetDir);
		// keep dynamic evaluation results constant for all attachments
		IStringEvaluator cacheResolver = new CacheResolver(resolver);
		Lock lock = null;
		List<Object> creations = new ArrayList<>();
		File targetMaster;
		List<Attachment> targetAttachments = new ArrayList<>();
		try {
			targetMaster = createTargetFile(targetDir, master, template, cacheResolver);
			if (FileTools.equals(targetMaster, master)) {
				// no move operation requested
				return;
			}
			if (collisionDetect && targetMaster.exists()) {
				throw new CollisionException("'" + targetMaster + "' exists");
			}
			lock = FileTools.lock(targetMaster);
			// this may lead to unexpected behavior!!
			if (collisionDetect && lock == null) {
				throw new CollisionException("'" + targetMaster + "' locked");
			}
			// now, even after locking we must be prepared for collisions on the attachments.
			for (Attachment attachment : attachments) {
				File attachTarget = createTargetFile(targetDir, attachment, template, cacheResolver);
				if (collisionDetect && attachTarget.exists()) {
					throw new CollisionException("'" + attachTarget + "' exists");
				}
			}
			boolean masterReplaced = false;
			// now everything is fine, we can start moving around
			for (Attachment attachment : attachments) {
				File targetAttach = createTargetFile(targetDir, attachment, template, cacheResolver);
				masterReplaced = masterReplaced || FileTools.equals(targetMaster, targetAttach);
				Attachment copyAttachment = copy(attachment, targetAttach);
				if (copyAttachment != null) {
					for (Object key : attachment.getAttributes()) {
						copyAttachment.setAttribute(key, attachment.getAttribute(key));
					}
					targetAttachments.add(copyAttachment);
					creations.add(copyAttachment);
				} else {
					targetAttachments.add(attachment);
				}
			}
			// master is moved last to act as indicator
			// when someone created an attachment with same name as master this is intended, do not overwrite...
			if (!masterReplaced) {
				Attachment tempMaster = copy(master, targetMaster);
				if (tempMaster != null) {
					creations.add(tempMaster);
				}
			}
		} catch (Exception e) {
			deleteAll(creations);
			throw e;
		} finally {
			if (lock != null) {
				lock.release();
			}
		}
		/*
		 * "point of no return" - we can only try to remove artifacts
		 */
		try {
			if (deleteSource) {
				delete();
			}
			if (deleteSubdirectories) {
				FileTools.deleteEmptyDirectories(getRoot(), master);
			}
		} catch (IOException e) {
			//
		}
		attachments = targetAttachments;
		master = targetMaster;
		root = targetDir;
	}
}
