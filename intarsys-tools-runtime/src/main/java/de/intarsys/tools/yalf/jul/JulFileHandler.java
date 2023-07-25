/*
 * Copyright (c) 2014, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
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
package de.intarsys.tools.yalf.jul;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;

import de.intarsys.tools.yalf.handler.IFileHandler;

public class JulFileHandler extends JulHandler<FileHandler> implements IFileHandler<LogRecord> {

	public JulFileHandler(FileHandler handler) {
		super(handler);
	}

	@Override
	@SuppressWarnings("java:S3011")
	public File getDirectory() {
		FileHandler handler = getImplementation();
		try {
			handler.flush();
			Field field = FileHandler.class.getDeclaredField("files");
			field.setAccessible(true);
			File[] files = (File[]) field.get(handler);
			if (files == null || files.length == 0) {
				return null;
			}
			return files[0].getCanonicalFile().getParentFile();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("java:S3011")
	public File getFile() {
		FileHandler handler = getImplementation();
		try {
			handler.flush();
			Field field = FileHandler.class.getDeclaredField("files");
			field.setAccessible(true);
			File[] files = (File[]) field.get(handler);
			if (files == null || files.length == 0) {
				return null;
			}
			return files[0];
		} catch (Exception e) {
			//
		}
		return null;
	}

	@Override
	@SuppressWarnings("java:S3011")
	public List<File> getFiles() {
		List<File> result = new ArrayList<>();
		FileHandler handler = getImplementation();
		try {
			handler.flush();
			Field field = FileHandler.class.getDeclaredField("files");
			field.setAccessible(true);
			File[] files = (File[]) field.get(handler);
			if (files == null) {
				return result;
			}
			for (int i = 0; i < files.length; i++) {
				result.add(files[i].getCanonicalFile());
			}
		} catch (Exception e) {
			//
		}
		return result;
	}

}
