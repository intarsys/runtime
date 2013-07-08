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
public class HexRow {
	public static int DEFAULT_LENGTH = 16;

	private HexData hexData;

	private int length = DEFAULT_LENGTH;

	private int offset;

	/**
	 * 
	 */
	protected HexRow(HexData hexData, int offset, int length) {
		super();
		this.hexData = hexData;
		this.offset = offset;
		this.length = length;
	}

	public String getAsString() {
		return new String(hexData.basicGetBytes(), offset, Math.min(length,
				hexData.length() - offset));
	}

	public byte getCellValue(int column) {
		int index = offset + column;
		if (index >= hexData.length()) {
			return 0;
		} else {
			return hexData.basicGetValue(offset + column);
		}
	}

	public String getCellValueAsString(int column) {
		int index = offset + column;
		if (index >= hexData.length()) {
			return "  ";
		} else {
			return new String(HexTools.ByteToHex[0xff & hexData
					.basicGetValue(offset + column)]);
		}
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	public void setCellValue(int column, byte value) {
		hexData.basicSetValue(offset + column, value);
	}

	public void setCellValue(int column, String value) {
		hexData.basicSetValue(offset + column, (byte) HexTools
				.hexStringToInt(value));
	}

	public void setLength(int width) {
		this.length = width;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
