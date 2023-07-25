package de.intarsys.tools.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.attachment.Attachment;
import de.intarsys.tools.locator.FileLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Utility methods to handle attachment relationships between files.
 * 
 */
public class FileAttachmentTools {

	private static final ILogger Log = LogTools.getLogger(FileAttachmentTools.class);

	/**
	 * Create a name for the attachment.
	 * 
	 * The name creation process is determined by the transformation pattern defined
	 * by masterName and movedName.
	 * 
	 * @param masterName
	 * @param movedName
	 * @param attachName
	 * @return
	 */
	protected static String createAttachmentName(String masterName, String movedName, String attachName) {
		String prefix = StringTools.getCommonPrefix(masterName, attachName, true);
		if (StringTools.isEmpty(prefix)) {
			return attachName;
		}
		int masterSuffixLength = masterName.length() - prefix.length();
		int attachSuffixLength = attachName.length() - prefix.length();
		// set newName to movedName
		String newName = movedName;
		// remove master suffix from newName
		newName = newName.substring(0, newName.length() - masterSuffixLength);
		// add attachment suffix to newName
		newName = newName + attachName.substring(attachName.length() - attachSuffixLength);
		return newName;
	}

	/**
	 * Create a list of {@link File} instances selected from the array of candidates
	 * that are recognized as attachments to <code>master</code>.
	 * <p>
	 * The attachments are selected using a ";" separated list of suffixes
	 * (including the ".", like in ".ext").
	 * <p>
	 * A file is accepted as an attachment if it has an attachment suffix and the
	 * file base name is either the same as the master file name or master file base
	 * name. The master may be null to accept all attachments in a directory<br>
	 * <code>
	 * 		foo.bar
	 * 		foo.bar.suffix (accepted)
	 * 		foo.suffix (accepted)
	 * 		foo.txt (rejected)
	 * </code>
	 * <p>
	 * All checks are case insensitive.
	 * 
	 * @param master
	 * @param files
	 * @param extensions
	 * @return a list of {@link File} instances selected from the array of
	 *         candidates that are recognized as attachments to <code>master</code>.
	 */
	public static List<File> createAttachments(File master, File[] files, String extensions) {
		if (StringTools.isEmpty(extensions)) {
			return Collections.emptyList();
		}
		String masterName = null;
		String masterBaseName = null;
		if (master != null) {
			masterName = master.getName().toLowerCase();
			masterBaseName = PathTools.getBaseName(masterName);
		}
		List<File> result = new ArrayList<>();
		String[] tempExtensions = extensions.toLowerCase().split(";");
		for (int j = 0; j < files.length; j++) {
			File checkFile = files[j];
			String checkFileName = checkFile.getName().toLowerCase();
			if (checkFileName.equals(masterName)) {
				// this is master itself...
				continue;
			}
			String checkFileBaseName = PathTools.getBaseName(checkFileName);
			if (master == null || checkFileBaseName.equals(masterName) || checkFileBaseName.equals(masterBaseName)) {
				for (int i = 0; i < tempExtensions.length; i++) {
					String tempExtension = tempExtensions[i].trim();
					boolean required = tempExtension.startsWith("+");
					if (required) {
						tempExtension = tempExtension.substring(1);
					}
					if (checkFileName.endsWith(tempExtension)) {
						result.add(checkFile);
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Find all attachments to <code>master</code>. Attachments are defined to be
	 * all files in the same directory as <code>master</code> that satisfy one of
	 * the <code>extensions</code>.
	 * 
	 * @param master
	 * @param extensions
	 * @return all attachments to <code>master</code>
	 */
	public static List<File> createAttachments(File master, String extensions) {
		File tempDir;
		File tempFile;
		if (master.isDirectory()) {
			tempDir = master;
			tempFile = null;
		} else {
			tempDir = master.getParentFile();
			tempFile = master;
		}
		if (tempDir == null) {
			return Collections.emptyList();
		}
		File[] files = tempDir.listFiles();
		return createAttachments(tempFile, files, extensions);
	}

	public static void deleteAttachments(List<?> attachments) {
		for (Iterator<?> it = attachments.iterator(); it.hasNext();) {
			Object attachment = it.next();
			if (attachment instanceof Attachment) {
				attachment = ((Attachment) attachment).getAttached();
			}
			if (attachment instanceof File) {
				if (((File) attachment).exists() && !((File) attachment).delete()) { // NOSONAR
					Log.log(Level.WARN, "deleting '" //$NON-NLS-1$
							+ attachment + "' failed"); //$NON-NLS-1$
				}
			} else if (attachment instanceof ILocator) {
				if (((ILocator) attachment).exists()) {
					try {
						((ILocator) attachment).delete();
					} catch (IOException e) {
						Log.log(Level.WARN, "deleting '" //$NON-NLS-1$
								+ attachment + "' failed"); //$NON-NLS-1$
					}
				}
			} else if (attachment instanceof ILocatorSupport) {
				ILocator tempLocator = ((ILocatorSupport) attachment).getLocator();
				if (tempLocator.exists()) {
					try {
						tempLocator.delete();
					} catch (IOException e) {
						Log.log(Level.WARN, "deleting '" //$NON-NLS-1$
								+ tempLocator + "' failed"); //$NON-NLS-1$
					}
				}
			} else {
				//
			}
		}
	}

	/**
	 * true if all required attachments for master are available.
	 * 
	 * extensions is a ";" separated list of extensions in the form ".ext". If an
	 * extension is required, the extension is prefixed with a "+" (like in
	 * "+.ext").
	 * 
	 * All checks are case insensitive.
	 * 
	 * @param master
	 * @param files
	 * @param extensions
	 * @return
	 */
	public static boolean existsAttachments(File master, File[] files, String extensions) {
		if (StringTools.isEmpty(extensions)) {
			return true;
		}
		String masterName = null;
		String masterBaseName = null;
		if (master != null) {
			masterName = master.getName().toLowerCase();
			masterBaseName = PathTools.getBaseName(masterName);
		}
		String[] tempExtensions = extensions.toLowerCase().split(";");
		for (int i = 0; i < tempExtensions.length; i++) {
			String tempExtension = tempExtensions[i].trim();
			boolean required = tempExtension.startsWith("+");
			if (required) {
				tempExtension = tempExtension.substring(1);
				boolean exists = false;
				for (int j = 0; j < files.length; j++) {
					File checkFile = files[j];
					String checkFileName = checkFile.getName().toLowerCase();
					String checkFileBaseName = PathTools.getBaseName(checkFileName);
					if (master == null || checkFileBaseName.equals(masterName)
							|| checkFileBaseName.equals(masterBaseName)) {
						if (checkFileName.endsWith(tempExtension)) {
							exists = true;
							break;
						}
					}
				}
				if (!exists) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * true, if file is an attachment as defined in the extensions. extensions is a
	 * ";" separated list of extensions in the form ".ext".
	 * 
	 * All checks are case insensitive.
	 * 
	 * @param file
	 * @param extensions
	 * @return
	 */
	public static boolean isAttachment(File file, String extensions) {
		if (StringTools.isEmpty(extensions)) {
			return false;
		}
		String fileName = file.getName().toLowerCase();
		String[] tempFilters = extensions.toLowerCase().split(";"); //$NON-NLS-1$
		for (int i = 0; i < tempFilters.length; i++) {
			String tempFilter = tempFilters[i].trim();
			boolean required = tempFilter.startsWith("+");
			if (required) {
				tempFilter = tempFilter.substring(1);
			}
			if (fileName.endsWith(tempFilter)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Given the original master file and an attachment to this file, move the
	 * attachments to the correct location for the new master file location given in
	 * movedFile.
	 * <p>
	 * If delete is <code>true</code>, old attachment files are deleted.
	 * 
	 * @param master
	 * @param attachment
	 * @param movedFile
	 * @param delete
	 * @return The moved (or unchanged) attachment.
	 * @throws IOException
	 */
	public static File moveAttachment(File master, File attachment, File movedFile, boolean delete) throws IOException {
		if (master == null || movedFile == null || attachment == null) {
			return attachment;
		}
		String masterName = master.getName();
		String movedName = movedFile.getName();
		File movedDir;
		if (movedFile.isDirectory()) {
			movedDir = movedFile;
			movedName = masterName;
		} else {
			movedDir = movedFile.getParentFile();
		}
		if (movedDir == null) {
			return attachment;
		}
		File movedAttachment = moveAttachment(masterName, attachment, movedName, movedDir, delete);
		return movedAttachment;
	}

	protected static File moveAttachment(String masterName, Object attachment, String movedName, File dir,
			boolean delete) throws IOException {
		if (attachment instanceof Attachment) {
			attachment = ((Attachment) attachment).getAttached();
		}
		if (attachment instanceof ILocatorSupport) {
			attachment = ((ILocatorSupport) attachment).getLocator();
		}
		if (attachment instanceof FileLocator) {
			// avoid streaming
			attachment = ((FileLocator) attachment).getFile();
		}
		try {
			if (attachment instanceof File) {
				File tempFile = (File) attachment;
				if (tempFile.exists()) {
					String tempName = tempFile.getName();
					String newName = createAttachmentName(masterName, movedName, tempName);
					File newAttachment = new File(dir, newName);
					if (newAttachment.equals(tempFile)) {
						return tempFile;
					}
					if (delete) {
						if (Log.isLoggable(Level.DEBUG)) {
							Log.debug("move '{}' to '{}'", tempFile.getAbsolutePath(), newAttachment.getAbsolutePath());
						}
						FileTools.renameFile(tempFile, newAttachment);
					} else {
						if (Log.isLoggable(Level.DEBUG)) {
							Log.debug("copy '{}' to '{}'", tempFile.getAbsolutePath(), newAttachment.getAbsolutePath());
						}
						FileTools.copyFile(tempFile, newAttachment);
					}
					return newAttachment;
				} else {
					Log.debug("attachment '{}' no longer available", tempFile.getAbsolutePath());
				}
			}
			if (attachment instanceof ILocator) {
				ILocator tempLocator = (ILocator) attachment;
				if (tempLocator.exists()) {
					String tempName = tempLocator.getName();
					String newName = createAttachmentName(masterName, movedName, tempName);
					File newAttachment = new File(dir, newName);
					if (Log.isLoggable(Level.DEBUG)) {
						Log.debug("create file '{}'", newAttachment.getAbsolutePath());
					}
					LocatorTools.copy(tempLocator, new FileLocator(newAttachment));
					if (delete) {
						if (Log.isLoggable(Level.DEBUG)) {
							Log.debug("delete locator '{}'", tempLocator.getPath());
						}
						tempLocator.delete();
					}
					return newAttachment;
				} else {
					Log.debug("attachment '{}' no longer available", tempLocator.getPath());
				}
			}
			return null;
		} catch (Exception e) {
			throw new IOException("failed to move attachment '" + attachment + "' attached to '"
					+ masterName + "' (" + e.getMessage() + ")");
		}
	}

	/**
	 * Given the original master file and the list of attachments to this file, move
	 * all attachments to the correct location for the new master file location
	 * given in movedFile.
	 * <p>
	 * If delete is <code>true</code>, old attachment files are deleted.
	 * 
	 * @param master
	 * @param attachments
	 * @param movedFile
	 * @param delete
	 * @return The list of moved (or unchanged ) attachments
	 */
	public static List moveAttachments(File master, List attachments, File movedFile, boolean delete)
			throws IOException {
		if (master == null || movedFile == null || attachments == null || attachments.isEmpty()) {
			return attachments;
		}
		List<Object> result = new ArrayList<>();
		String masterName = master.getName();
		String movedName = movedFile.getName();
		File movedDir;
		if (movedFile.isDirectory()) {
			movedDir = movedFile;
		} else {
			movedDir = movedFile.getParentFile();
		}
		if (movedDir == null) {
			return attachments;
		}
		File masterDir;
		if (master.isDirectory()) {
			masterDir = master;
		} else {
			masterDir = master.getParentFile();
		}
		IOException ex = null;
		for (Iterator<?> it = attachments.iterator(); it.hasNext();) {
			Object tempAttachment = it.next();
			File targetSubdirectory = movedDir;
			if (tempAttachment instanceof File) {
				Path relativePath = masterDir.toPath().relativize(((File) tempAttachment).toPath());
				Path parent = relativePath.getParent();
				if (parent != null) {
					targetSubdirectory = new File(targetSubdirectory, parent.toString());
				}
			}
			try {
				File movedAttachment = moveAttachment(masterName, tempAttachment, movedName, targetSubdirectory,
						delete);
				if (movedAttachment != null) {
					result.add(movedAttachment);
				}
			} catch (IOException e) {
				// try to handle as much as possible
				ex = e;
			}
		}
		if (ex != null) {
			throw ex;
		}
		return result;
	}

	private FileAttachmentTools() {
	}

}
