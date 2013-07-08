/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.dom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import de.intarsys.tools.file.Loader;
import de.intarsys.tools.string.StringTools;

public class SchemaDirectory implements LSResourceResolver {

	class SchemaFinder extends Loader {
		private final ArrayList<Source> xsdSources;

		public SchemaFinder() {
			xsdSources = new ArrayList<Source>();
		}

		@Override
		protected boolean basicLoadFile(File file, boolean readOnly, String path) {
			if (file.getName().toLowerCase().endsWith(".xsd")) { //$NON-NLS-1$
				xsdSources.add(new StreamSource(file));
			}
			return true;
		}

		public Source[] getSchemaSources() throws IOException {
			xsdSources.clear();
			if (StringTools.isEmpty(rootSchema)) {
				load(schemaDir, true, true);
			} else {
				load(schemaDir, rootSchema, true, false);
			}
			return xsdSources.toArray(new Source[xsdSources.size()]);
		}
	}

	static class SchemaLSInput implements LSInput {
		private String publicId;
		private String systemId;
		private String baseURI;
		private String encoding;
		private String stringData;
		private Reader characterStream;
		private boolean certifiedText;
		private InputStream byteStream;

		public SchemaLSInput(String publicId, String systemId, String baseURI) {
			this.publicId = publicId;
			this.systemId = systemId;
			this.baseURI = baseURI;
		}

		public String getBaseURI() {
			return baseURI;
		}

		public InputStream getByteStream() {
			return new InputStream() {

				@Override
				public void close() throws IOException {
					byteStream.close();
				}

				@Override
				public int read() throws IOException {
					return byteStream.read();
				}
			};
		}

		public boolean getCertifiedText() {
			return certifiedText;
		}

		public Reader getCharacterStream() {
			return characterStream;
		}

		public String getEncoding() {
			return encoding;
		}

		public String getPublicId() {
			return publicId;
		}

		public String getStringData() {
			return stringData;
		}

		public String getSystemId() {
			return systemId;
		}

		public void setBaseURI(String baseURI) {
			this.baseURI = baseURI;
		}

		public void setByteStream(InputStream byteStream) {
			this.byteStream = byteStream;
		}

		public void setCertifiedText(boolean certifiedText) {
			this.certifiedText = certifiedText;
		}

		public void setCharacterStream(Reader characterStream) {
			this.characterStream = characterStream;
		}

		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		public void setPublicId(String publicId) {
			this.publicId = publicId;
		}

		public void setStringData(String stringData) {
			this.stringData = stringData;
		}

		public void setSystemId(String systemId) {
			this.systemId = systemId;
		}
	}

	private static final Logger Log = PACKAGE.Log;

	private final File schemaDir;
	private final String rootSchema;

	private Schema schema;

	public SchemaDirectory(File pSchemaDir) {
		this(pSchemaDir, null);
	}

	public SchemaDirectory(File pSchemaDir, String pRootSchema) {
		schemaDir = pSchemaDir;
		rootSchema = pRootSchema;
	}

	public Schema getSchema() {
		return schema;
	}

	public Source[] getSources() throws IOException {
		return new SchemaFinder().getSchemaSources();
	}

	private String resolveFileName(String baseURI, String systemId) {
		String filename = StringTools.EMPTY;
		if (baseURI.toLowerCase().startsWith("http://")) { //$NON-NLS-1$
			String parent = new File(baseURI.substring(7)).getParent();
			if (parent != null) {
				filename = parent + File.separator;
			}
		}
		return filename + systemIdToFileName(systemId);
	}

	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		String filename = resolveFileName(baseURI, systemId);
		File file = new File(schemaDir, filename);
		if (file.canRead() && file.isFile()) {
			SchemaLSInput lsInput = new SchemaLSInput(publicId, systemId,
					baseURI);
			try {
				lsInput.setByteStream(new FileInputStream(file));
				return lsInput;
			} catch (FileNotFoundException e) {
				Log.log(Level.WARNING, e.getLocalizedMessage(), e);
			}
		}
		Log.log(Level.WARNING, "failed resolving schema type '" + type //$NON-NLS-1$
				+ "', namespaceURI '" + namespaceURI + "', baseURI '" + baseURI //$NON-NLS-1$ //$NON-NLS-2$
				+ "', systemId '" + systemId + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	private String systemIdToFileName(String systemId) {
		if (systemId.startsWith("http://")) { //$NON-NLS-1$
			return systemId.substring(7);
		} else {
			return systemId;
		}
	}
}
