package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
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

		private final ILocator zipLocator;

		private List<ZipEntry> entries;

		public ZipFile(ILocator zipLocator) {
			super();
			this.zipLocator = zipLocator;
		}

		protected List<ZipEntry> createEntries() throws IOException {
			List<ZipEntry> tempEntries = new ArrayList<>();
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

		protected synchronized List<ZipEntry> getEntries() throws IOException {
			if (entries == null) {
				entries = createEntries();
			}
			return entries;
		}

	}

	private final ZipFile zipFile;

	private final String path;

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

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ILocator getChild(String name) {
		String tempPath = path + "/" + name;
		return new ZipFileLocator(this, tempPath);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public ILocator getParent() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		return null;
	}

	@Override
	public Reader getReader() throws IOException {
		return null;
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		return null;
	}

	@Override
	public Writer getWriter() throws IOException {
		return null;
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		return null;
	}

	public ZipFile getZipFile() {
		return zipFile;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean isOutOfSynch() {
		return false;
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) throws IOException {
		return new ILocator[0];
	}

	@Override
	public void synch() {
		// not required
	}

	@Override
	public URI toURI() {
		return null;
	}

}
