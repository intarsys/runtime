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
package de.intarsys.tools.range;

import java.util.ArrayList;
import java.util.List;

public class NamedRange implements IRange {

	private EnumRange value;

	public NamedRange(EnumRange value) {
		super();
		this.value = value;
	}

	@Override
	public List<Integer> getIndices(int current, int count) {
		List<Integer> indices = new ArrayList<Integer>();
		if (getValue() == EnumRange.ALL) {
			for (int i = 0; i < count; i++) {
				indices.add(i);
			}
		} else if (getValue() == EnumRange.FIRST) {
			indices.add(0);
		} else if (getValue() == EnumRange.LAST) {
			indices.add(count - 1);
		} else if (getValue() == EnumRange.CURRENT) {
			if (current > -1) {
				indices.add(current);
			}
		} else {
			// default is all
			for (int i = 0; i < count; i++) {
				indices.add(i);
			}
		}
		return indices;
	}

	public EnumRange getValue() {
		return value;
	}

}
