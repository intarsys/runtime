package de.intarsys.tools.locator;

import java.io.File;
import java.io.IOException;

import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

public class PlainTransientLocatorFactory implements ITransientLocatorFactory {

	private static final ILogger Log = LogTools.getLogger(PlainTransientLocatorFactory.class);

	private long maxBufferSize = 1024L * 1024L * 10L;

	@Override
	public ILocator createLocator(ILocator locator) throws IOException {
		long size = locator.getLength();
		String name = locator.getName();
		if (size >= 0 && size < getMaxBufferSize()) {
			return new ByteArrayLocator(new byte[(int) size], 0, name);
		}
		File tempFile = FileTools.createTempFile(name);
		Log.trace("create plain transient locator at '{}'", tempFile);
		return new FileLocator(tempFile);
	}

	public long getMaxBufferSize() {
		return maxBufferSize;
	}

	public void setMaxBufferSize(long maxBufferSize) {
		this.maxBufferSize = maxBufferSize;
	}

}
