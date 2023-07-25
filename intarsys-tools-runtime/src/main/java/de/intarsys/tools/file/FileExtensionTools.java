/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.file;

import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.message.IMessageBundle;

public class FileExtensionTools {

	private static final IMessageBundle Msg = PACKAGE.Messages;

	public static void fillFileFilter(FileExtensionSet set, List<String> extensions, List<String> names) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<FileExtension> i = set.getFileExtensions().iterator(); i.hasNext();) {
			FileExtension extension = i.next();
			names.add(extension.getLabel());
			sb.setLength(0);
			for (Iterator j = extension.getExtensions().iterator(); j.hasNext();) {
				String ext = (String) j.next();
				sb.append("*.");
				sb.append(ext);
				if (j.hasNext()) {
					sb.append(";");
				}
			}
			extensions.add(sb.toString());
		}
		//
		extensions.add("*.*"); //$NON-NLS-1$
		names.add(Msg.getString("FileExtensionTools.allFiles")); //$NON-NLS-1$
	}

	private FileExtensionTools() {
	}
}
