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
package de.intarsys.tools.hex;

/**
 * 
 */
public class HexData {
	private byte[] bytes;

	private int length;

	private int offset;

	private int rowWidth = HexRow.DEFAULT_LENGTH;

	public HexData(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	/**
	 * 
	 */
	public HexData(byte[] bytes, int offset, int length) {
		super();
		byte[] newBytes = new byte[length];
		System.arraycopy(bytes, offset, newBytes, 0, length);
		this.bytes = newBytes;
		this.offset = 0;
		this.length = length;
	}

	protected byte[] basicGetBytes() {
		return bytes;
	}

	protected byte basicGetValue(int index) {
		return bytes[index];
	}

	protected void basicSetValue(int index, byte value) {
		if (index >= (offset + length)) {
			// do not write out of bounds
			int newLength = index - offset + 1;
			byte[] newBytes = new byte[newLength];
			System.arraycopy(bytes, offset, newBytes, 0, length);
			bytes = newBytes;
			length = newLength;
			offset = 0;
		}
		bytes[index] = value;
	}

	public byte getCellValue(int row, int column) {
		return getRow(row).getCellValue(column);
	}

	public String getCellValueAsString(int row, int column) {
		return getRow(row).getCellValueAsString(column);
	}

	public HexRow getRow(int index) {
		return new HexRow(this, offset + (index * rowWidth), rowWidth);
	}

	public int getRowCount() {
		return (length / rowWidth) + 1;
	}

	public HexRow[] getRows() {
		int count = getRowCount();
		HexRow[] rows = new HexRow[count];
		for (int i = 0; i < count; i++) {
			rows[i] = getRow(i);
		}
		return rows;
	}

	public byte getValue(int index) {
		return basicGetValue(offset + index);
	}

	public String getValueAsString(int index) {
		return new String(HexTools.ByteToHex[getValue(index)]);
	}

	public void insertCellValue(int row, int column, byte value) {
		int index = (row * rowWidth) + column;
		int newLength = length + 1;
		byte[] newBytes = new byte[newLength];
		System.arraycopy(bytes, offset, newBytes, 0, index);
		newBytes[index] = value;
		System.arraycopy(bytes, offset + index, newBytes, index + 1, length
				- index);
		bytes = newBytes;
		length = newLength;
		offset = 0;
	}

	public int length() {
		return bytes.length;
	}

	public void removeCellValue(int row, int column) {
		int index = (row * rowWidth) + column;
		int newLength = length - 1;
		byte[] newBytes = new byte[newLength];
		System.arraycopy(bytes, offset, newBytes, 0, index);
		System.arraycopy(bytes, offset + index + 1, newBytes, index, length
				- index - 1);
		bytes = newBytes;
		length = newLength;
		offset = 0;
	}

	public void setCellValue(int row, int column, byte value) {
		getRow(row).setCellValue(column, value);
	}

	public void setCellValue(int row, int column, String value) {
		getRow(row).setCellValue(column, value);
	}

	public void setValue(int index, byte value) {
		basicSetValue(offset + index, value);
	}

	public void setValue(int index, String value) {
		basicSetValue(offset + index, (byte) HexTools.hexStringToInt(value));
	}

	public byte[] toBytes() {
		byte[] result = new byte[length];
		System.arraycopy(bytes, offset, result, 0, length);
		return result;
	}
}
