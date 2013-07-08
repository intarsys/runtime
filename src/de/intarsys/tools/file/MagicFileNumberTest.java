/*
 * Copyright (c) 2007, intarsys consulting GmbH
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.stream.StreamTools;

public class MagicFileNumberTest {
	public static class Type {
		private String suffix;

		private byte[] magicBytes;

		public Type(String suffix, byte[] magic) {
			this.suffix = suffix;
			this.magicBytes = magic;
		}

		public String getKey() {
			return HexTools.bytesToHexString(magicBytes);
		}

		public byte[] getMagicBytes() {
			return magicBytes;
		}

		public String getSuffix() {
			return suffix;
		}

		public boolean matches(IRandomAccess data) throws IOException {
			data.reset();
			for (int i = 0; i < magicBytes.length; i++) {
				if (magicBytes[i] != data.read()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			return suffix;
		}
	}

	static private Map types;

	static {
		types = new HashMap();
		addTypes();
	}

	/**
	 * @param fileSuffix
	 *            just the suffix without any starting suffix delimiters (e.g.:
	 *            'pdf')
	 * @param magicBytes
	 *            any bytes, not <code>null</code> or zero size
	 */
	static public void addType(String fileSuffix, byte[] magicBytes) {
		if ((fileSuffix == null) || (magicBytes == null)
				|| (magicBytes.length == 0)) {
			return;
		}
		Type type = new Type(fileSuffix, magicBytes);
		if (!types.containsKey(type.getKey())) {
			types.put(type.getKey(), type);
		}
	}

	static protected void addTypes() {
		/*
		 * known types, found on
		 * http://www.astro.keele.ac.uk/oldusers/rno/Computing/File_magic.html
		 */
		/* image files */
		/* Bitmap format */
		addType("bmp", new byte[] { 0x42, 0x4d }); //$NON-NLS-1$
		/* FITS format */
		addType("fits", new byte[] { 0x53, 0x49, 0x4d, 0x50, 0x4c, 0x45 }); //$NON-NLS-1$
		/* GIF format */
		addType("gif", new byte[] { 0x47, 0x49, 0x46, 0x38 }); //$NON-NLS-1$
		/* Graphics Kernel System */
		addType("gks", new byte[] { 0x47, 0x4b, 0x53, 0x4d }); //$NON-NLS-1$
		/* IRIS rgb format */
		addType("rgb", new byte[] { 0x01, (byte) 0xda }); //$NON-NLS-1$
		/* ITC (CMU WM) format */
		addType("itc", new byte[] { (byte) 0xf1, 0x00, 0x40, (byte) 0xbb }); //$NON-NLS-1$
		/* JPEG File Interchange Format */
		addType("jpg", new byte[] { (byte) 0xff, (byte) 0xD8, (byte) 0xff }); //$NON-NLS-1$
		/* NIFF (Navy TIFF) */
		addType("nif", new byte[] { 0x49, 0x49, 0x4e, 0x31 }); //$NON-NLS-1$
		/* PM format */
		addType("pm", new byte[] { 0x56, 0x49, 0x45, 0x57 }); //$NON-NLS-1$
		/* PNG format */
		addType("png", new byte[] { (byte) 0x89, 0x50, 0x4e, 0x47 }); //$NON-NLS-1$
		/* Postscript format */
		addType("ps", new byte[] { 0x25, 0x21 }); //$NON-NLS-1$
		/* Sun Rasterfile */
		addType("ras", new byte[] { 0x59, (byte) 0xa6, 0x6a, (byte) 0x95 }); //$NON-NLS-1$
		/* TIFF format (Motorola - big endian) */
		addType("tif", new byte[] { 0x4d, 0x4d, 0x00, 0x2a }); //$NON-NLS-1$
		/* TIFF format (Intel - little endian) */
		addType("tif", new byte[] { 0x49, 0x49, 0x2a, 0x00 }); //$NON-NLS-1$
		/* XCF Gimp file structure */
		addType("xcf", new byte[] { 0x67, 0x69, 0x6d, 0x70, 0x20, 0x78, 0x63, //$NON-NLS-1$
				0x66, 0x20, 0x76 });
		/* Xfig format */
		addType("fig", new byte[] { 0x23, 0x46, 0x49, 0x47 }); //$NON-NLS-1$
		/* XPM format */
		addType("xpm", new byte[] { 0x2f, 0x2a, 0x20, 0x58, 0x50, 0x4d, 0x20, //$NON-NLS-1$
				0x2a, 0x2f });

		/* compressed files */
		/* Bzip */
		addType("bz", new byte[] { 0x42, 0x5a }); //$NON-NLS-1$
		/* Compress */
		addType("Z", new byte[] { 0x1f, (byte) 0x9d }); //$NON-NLS-1$
		/* gzip format */
		addType("gz", new byte[] { 0x1f, (byte) 0x8b }); //$NON-NLS-1$
		/* pkzip format */
		addType("zip", new byte[] { 0x50, 0x4b, 0x03, 0x04 }); //$NON-NLS-1$

		/* archive files */
		/* TAR */
		addType("tar", new byte[] { 0x75, 0x73, 0x74, 0x61, 0x72 }); //$NON-NLS-1$

		/* excecutable files */
		/* MS-DOS, OS/2 or MS Windows */
		addType("exe", new byte[] { 0x4d, 0x5a }); //$NON-NLS-1$
		/* Unix elf */
		addType("unix elf", new byte[] { 0x7f, 0x45, 0x4c, 0x46 }); //$NON-NLS-1$

		/* pgp */
		/* pgp public ring */
		addType("pgp public ring", new byte[] { (byte) 0x99, 0x00 }); //$NON-NLS-1$
		/* pgp security ring */
		addType("pgp security ring", new byte[] { (byte) 0x95, 0x01 }); //$NON-NLS-1$
		/* pgp security ring */
		addType("pgp security ring", new byte[] { (byte) 0x95, 0x00 }); //$NON-NLS-1$
		/* pgp encrypted data */
		addType("pgp encrypted data", new byte[] { (byte) 0xA6, 0x00 }); //$NON-NLS-1$

		/* other */
		try {
			addType("pdf", "%PDF".getBytes("ASCII")); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
		} catch (UnsupportedEncodingException e) {
			// will not happen
		}
	}

	/**
	 * @param data
	 *            any not null or zero size data
	 * @return a file suffix without a delimiter (e.g.: 'pdf') or
	 *         <code>null</code>
	 */
	static public String guessFileSuffix(byte[] data) {
		if (data == null) {
			return null;
		}
		IRandomAccess ra = null;
		try {
			ra = new RandomAccessByteArray(data);
			return guessFileSuffix(ra);
		} catch (IOException e) {
			// should not occur
			throw new RuntimeException(e);
		} finally {
			StreamTools.close(ra);
		}
	}

	/**
	 * @param data
	 *            any not null or zero size data
	 * @return a file suffix without a delimiter (e.g.: 'pdf') or
	 *         <code>null</code>
	 */
	static public String guessFileSuffix(IRandomAccess data) throws IOException {
		for (Iterator i = types.entrySet().iterator(); i.hasNext();) {
			Type type = (Type) ((Map.Entry) i.next()).getValue();
			if (type.matches(data)) {
				return type.getSuffix();
			}
		}
		return null;
	}

	/**
	 * does the data contain only ISO-8819-x printable characters ?
	 */
	static public boolean isText(byte[] data) {
		IRandomAccess ra = null;
		try {
			ra = new RandomAccessByteArray(data);
			return isText(ra);
		} catch (IOException e) {
			// should not occur
			throw new RuntimeException(e);
		} finally {
			StreamTools.close(ra);
		}
	}

	/**
	 * does the data contain only ISO-8819-x printable characters ?
	 */
	static public boolean isText(IRandomAccess data) throws IOException {
		if (data == null) {
			return false;
		}
		data.reset();
		for (int i = 0, c = data.read(); c != -1 && i < 1024; c = data.read(), i++) {
			c = c & 0xFF;
			if (c >= 0x20 && c <= 0x7E) {
				// ASCII printable
				continue;
			}
			if (c >= 0xA0) {
				// ISO 8819 extension
				continue;
			}
			switch (c) {
			case 0x09: // HORIZONTAL TABULATION
			case 0x0A: // LINE FEED
			case 0x0B: // VERTICAL TABULATION
			case 0x0C: // FORM FEED
			case 0x0D: // CARRIAGE RETURN
				continue;
			default:
			}
			return false;
		}
		return true;
	}

	private MagicFileNumberTest() {
	}

}
