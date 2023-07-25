/*
 * Copyright (c) 2012, intarsys GmbH
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
package de.intarsys.tools.enumeration;

import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.message.IMessage;

/**
 * A {@link Domain} implements an "ad-hoc" enumeration. For otherwise
 * unrestricted and unrelated tuples of base objects one can define a
 * {@link DomainItem}. These {@link DomainItem} instances are grouped in a
 * {@link Domain} that can for example be used for a choice in a user interface
 * element.
 * 
 */
public class Domain<M> extends EnumMeta<DomainItem<M>> {

	private boolean extendable = true;

	private boolean nullable = true;

	private int counter;

	public Domain() {
		super(DomainItem.class);
	}

	protected DomainItem<M> basicFindItem(M object) {
		for (DomainItem<M> item : getItems()) {
			if (item.accept(object)) {
				return item;
			}
		}
		return null;
	}

	public DomainItem<M> createItem(M object) {
		String id;
		if (object instanceof String) {
			id = (String) object;
		} else if (object instanceof IIdentifiable) {
			id = ((IIdentifiable) object).getId();
		} else if (object instanceof IMessage) {
			id = ((IMessage) object).getCode();
		} else {
			id = "item-" + counter++;
		}
		DomainItem<M> tempItem = new DomainItem<>(this, id, object);
		return tempItem;
	}

	public DomainItem<M> findItem(M object) {
		DomainItem<M> tempItem = basicFindItem(object);
		return tempItem;
	}

	public DomainItem<M> findItemOrDefault(M object) {
		DomainItem<M> tempItem = basicFindItem(object);
		if (tempItem == null) {
			if (isExtendable() && (object != null || isNullable())) {
				tempItem = createItem(object);
			} else {
				tempItem = getDefault();
			}
		}
		return tempItem;
	}

	public boolean isExtendable() {
		return extendable;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setExtendable(boolean extendable) {
		this.extendable = extendable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
}
