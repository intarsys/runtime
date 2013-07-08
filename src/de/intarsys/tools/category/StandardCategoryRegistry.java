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
package de.intarsys.tools.category;

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.message.MessageBundle;

/**
 * A registry to manage named categories.
 */
public class StandardCategoryRegistry implements ICategoryRegistry {

	private static final MessageBundle Msg = PACKAGE.Messages;

	public static final GenericCategory OTHER;

	public static final GenericCategory UNKNOWN;

	static {
		UNKNOWN = new GenericCategory();
		UNKNOWN.setId(ICategory.UNKNOWN);
		UNKNOWN.setLabel(Msg
				.getString("StandardCategoryRegistry.categoryUnknown.label")); //$NON-NLS-1$
		OTHER = new GenericCategory();
		OTHER.setId(ICategory.OTHER);
		OTHER.setLabel(Msg
				.getString("StandardCategoryRegistry.categoryOther.label")); //$NON-NLS-1$
	}

	/**
	 * All categories known to the registry
	 */
	private Map categories = new HashMap();

	/**
	 * 
	 */
	public StandardCategoryRegistry() {
		super();
		registerCategory(UNKNOWN);
		registerCategory(OTHER);
	}

	public ICategory[] getCategories() {
		return (ICategory[]) categories.values().toArray(
				new ICategory[categories.size()]);
	}

	public ICategory lookupCategory(String id) {
		ICategory result = (ICategory) categories.get(id);
		if (result == null) {
			result = UNKNOWN;
		}
		return result;
	}

	public void registerCategory(ICategory category) {
		categories.put(category.getId(), category);
	}

	public int size() {
		return categories.size();
	}
}
