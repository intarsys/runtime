package de.intarsys.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tool methods to implement a file based synchronization with other platform
 * processes.
 * <p>
 * This is especially useful for synchronizing in shell scripting integration
 * scenarios, in conjunction with some commandline based access to this methods.
 */
public class SynchTools {

	private static byte[] bytes;

	static public void delSynchFile(File file) throws IOException {
		file.delete();
	}

	public static byte[] getBytes() {
		return bytes;
	}

	static public File getSynchFile() throws IOException {
		return File.createTempFile("synchfile", ".synch");
	}

	public static void setBytes(byte[] bytes) {
		SynchTools.bytes = bytes;
	}

	static public void setSynchFile(File file) throws IOException {
		setSynchFile(file, getBytes());
		bytes = null;
	}

	static public void setSynchFile(File file, byte[] bytes) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		if (bytes != null) {
			os.write(bytes);
		}
		os.close();
	}

	static public void setSynchFile(File file, String value) throws IOException {
		FileWriter w = new FileWriter(file);
		w.write(value);
		w.close();
	}

	static public void waitSynchFile(File file, int timeout) throws IOException {
		long start = System.currentTimeMillis();
		long count = 0;
		while (count < timeout && !file.exists()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new IOException("waiting for synch file '"
						+ file.getPath() + "' interrupted");
			}
			count = System.currentTimeMillis() - start;
		}
		if (!file.exists()) {
			throw new IOException("timeout waiing for synch file '"
					+ file.getPath() + "'");
		}
	}

}
