package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.stream.StreamTools;

/**
 * ! not yet functional !
 * 
 * An {@link ILocator} into a zip file.
 * 
 */
public class ZipFileLocator extends CommonLocator {

	static class ZipFile {

		final private ILocator zipLocator;

		private List<ZipEntry> entries;

		public ZipFile(ILocator zipLocator) {
			super();
			this.zipLocator = zipLocator;
		}

		protected List<ZipEntry> createEntries() throws IOException {
			List<ZipEntry> tempEntries = new ArrayList<ZipEntry>();
			InputStream is = zipLocator.getInputStream();
			try {
				ZipInputStream zis = new ZipInputStream(is);
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					tempEntries.add(entry);
				}
			} finally {
				StreamTools.close(is);
			}
			return tempEntries;
		}

		synchronized protected List<ZipEntry> getEntries() throws IOException {
			if (entries == null) {
				entries = createEntries();
			}
			return entries;
		}

	}

	final private ZipFile zipFile;

	final private String path;

	public ZipFileLocator(ILocator zipLocator, String path) {
		super();
		this.zipFile = new ZipFile(zipLocator);
		this.path = path;
	}

	protected ZipFileLocator(ZipFile zipFile, String path) {
		super();
		this.zipFile = zipFile;
		this.path = path;
	}

	public boolean exists() {
		return false;
	}

	protected ZipEntry findEntry(String tempPath) throws IOException {
		for (ZipEntry entry : zipFile.getEntries()) {
			if (entry.getName().equals(path)) {
				return entry;
			}
		}
		return null;
	}

	public ILocator getChild(String name) {
		String tempPath = path + "/" + name;
		return new ZipFileLocator(this, tempPath);
	}

	public String getFullName() {
		return null;
	}

	public InputStream getInputStream() throws IOException {
		return null;
	}

	public String getLocalName() {
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	public ILocator getParent() {
		return null;
	}

	public IRandomAccess getRandomAccess() throws IOException {
		return null;
	}

	public Reader getReader() throws IOException {
		return null;
	}

	public Reader getReader(String encoding) throws IOException {
		return null;
	}

	public String getType() {
		return null;
	}

	public String getTypedName() {
		return null;
	}

	public Writer getWriter() throws IOException {
		return null;
	}

	public Writer getWriter(String encoding) throws IOException {
		return null;
	}

	public boolean isDirectory() {
		return false;
	}

	public boolean isOutOfSynch() {
		return false;
	}

	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		return null;
	}

	public void synch() {
	}

	public URL toURL() {
		return null;
	}

}
