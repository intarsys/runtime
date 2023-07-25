/*
 * Copyright (c) 2014, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.activity;

import java.io.File;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.string.StringTools;

/**
 * An activity that allows entry / selection of a file system artifact (a
 * directory or file).
 * 
 */
public class RequestFile extends Requester<String, IActivity<?>> {

	private static final IMessageBundle Msg = PACKAGE.Messages;

	/**
	 * Create the activity.
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param path
	 * @return
	 */
	public static RequestFile createDirectory(IActivity parent, IMessage title, IMessage message, String path) {
		RequestFile result = new RequestFile(parent);
		result.setTitle(title);
		result.setMessage(message);
		result.setInitialPath(path);
		result.setQueryDirectory(true);
		return result;
	}

	public static RequestFile createFilenameLoad(IActivity parent, IMessage title, String[] extensions, String[] names,
			String path) {
		RequestFile result = new RequestFile(parent);
		result.setTitle(title);
		result.setFileTypeExtensions(extensions);
		result.setFileTypeNames(names);
		result.setInitialPath(path);
		result.setQueryDirectory(false);
		result.setQueryLoad(true);
		return result;
	}

	public static RequestFile createFilenameSave(IActivity parent, IMessage title, String[] extensions, String[] names,
			String path, String filename, boolean checkOverwrite) {
		RequestFile result = new RequestFile(parent);
		result.setTitle(title);
		result.setFileTypeExtensions(extensions);
		result.setFileTypeNames(names);
		result.setInitialPath(path);
		result.setInitialFilename(filename);
		result.setCheckOverwrite(checkOverwrite);
		result.setQueryDirectory(false);
		result.setQueryLoad(false);
		return result;
	}

	public static String requestDirectory(IMessage title, IMessage message, String path) {
		RequestFile dialog = RequestFile.createDirectory(null, title, message, path);
		dialog.enter();
		try {
			return ExceptionTools.futureSimpleGet(dialog);
		} catch (Exception e) {
			return null;
		}
	}

	public static String requestFilenameLoad(IMessage title, String[] extensions, String[] names, String path) {
		return requestFilenameLoad(title, extensions, names, path, 0);
	}

	public static String requestFilenameLoad(IMessage title, String[] extensions, String[] names, String path,
			int defaultExtensionIndex) {
		RequestFile dialog = RequestFile.createFilenameLoad(null, title, extensions, names, path);
		dialog.setDefaultExtensionIndex(defaultExtensionIndex);
		dialog.enter();
		try {
			return ExceptionTools.futureSimpleGet(dialog);
		} catch (Exception e) {
			return null;
		}
	}

	public static String requestFilenameSave(IMessage title, String[] extensions, String[] names, String path,
			String filename, boolean checkOverwrite) {
		RequestFile dialog = RequestFile.createFilenameSave(null, title, extensions, names, path, filename,
				checkOverwrite);
		dialog.enter();
		try {
			return ExceptionTools.futureSimpleGet(dialog);
		} catch (Exception e) {
			return null;
		}
	}

	private String initialPath;

	private String[] fileTypeExtensions;

	private String[] fileTypeNames;

	private String initialFilename;

	private boolean checkOverwrite;

	private String filename;

	private boolean queryDirectory;

	private boolean queryLoad = true;

	private int defaultExtensionIndex;

	public RequestFile(IActivity<?> parent) {
		super(parent);
	}

	@Override
	protected void basicEnterBefore() throws Exception {
		super.basicEnterBefore();
		if (fileTypeExtensions != null && fileTypeNames != null) {
			// try to sort the current extension to the beginning.
			String extension = "*." + PathTools.getExtension(initialFilename); //$NON-NLS-1$
			for (int i = 0; i < fileTypeExtensions.length; i++) {
				if (fileTypeExtensions[i].equals(extension)) {
					String tempExtension = fileTypeExtensions[i];
					System.arraycopy(fileTypeExtensions, 0, fileTypeExtensions, 1, i);
					fileTypeExtensions[0] = tempExtension;
					String tempName = fileTypeNames[i];
					System.arraycopy(fileTypeNames, 0, fileTypeNames, 1, i);
					fileTypeNames[0] = tempName;
					break;
				}
			}
		}
	}

	public int getDefaultExtensionIndex() {
		return defaultExtensionIndex;
	}

	@Override
	protected String getDefaultResult() {
		return getFilename();
	}

	public String getFilename() {
		return filename;
	}

	public String[] getFileTypeExtensions() {
		return fileTypeExtensions;
	}

	public String[] getFileTypeNames() {
		return fileTypeNames;
	}

	public String getInitialFilename() {
		return initialFilename;
	}

	public String getInitialPath() {
		return initialPath;
	}

	public boolean isCheckOverwrite() {
		return checkOverwrite;
	}

	public boolean isQueryDirectory() {
		return queryDirectory;
	}

	public boolean isQueryLoad() {
		return queryLoad;
	}

	public void setCheckOverwrite(boolean checkOverwrite) {
		this.checkOverwrite = checkOverwrite;
	}

	public void setDefaultExtensionIndex(int defaultExtensionIndex) {
		this.defaultExtensionIndex = defaultExtensionIndex;
	}

	public void setFilename(String resultName) {
		this.filename = resultName;
	}

	public void setFileTypeExtensions(String[] extensions) {
		this.fileTypeExtensions = extensions;
	}

	public void setFileTypeNames(String[] names) {
		this.fileTypeNames = names;
	}

	public void setInitialFilename(String filename) {
		this.initialFilename = filename;
	}

	public void setInitialPath(String path) {
		this.initialPath = path;
	}

	public void setQueryDirectory(boolean directoryDialog) {
		this.queryDirectory = directoryDialog;
	}

	public void setQueryLoad(boolean load) {
		this.queryLoad = load;
	}

	@Override
	protected boolean validate(String value) {
		String resultName = getFilename();
		if (StringTools.isEmpty(resultName)) {
			return true;
		}
		// suppress suffix when '.' terminates name
		if (resultName.indexOf('.') == resultName.length() - 1) {
			resultName = resultName.substring(0, resultName.length() - 1);
		}
		File file = new File(resultName);
		if (checkOverwrite && file.exists()) {
			IMessage msgTitle = Msg.getMessage("RequestFile.TitleSaveAs");
			IMessage msgMessage = Msg.getMessage("RequestFile.QuerySaveAsAlreadyExists", file.getName());
			IMessage response = RequestConfirmation.requestYesNo(null, msgTitle, msgMessage,
					RequestConfirmation.OPTION_YES);
			if (response != RequestConfirmation.OPTION_YES) {
				setInitialPath(resultName);
				activityEnter(this);
				return false;
			}
		}
		return true;
	}
}
