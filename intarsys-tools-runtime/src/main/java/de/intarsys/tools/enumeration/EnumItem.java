/*
 * Copyright (c) 2007, intarsys GmbH
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

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.MessageTools;

/**
 * The abstract superclass for an enumeration implementation.
 * <p>
 * To implement an enumeration class:
 * 
 * <pre>
 *                 - create a subclass of EnumItem
 *                 - create a public static final attribute named META to the EnumMeta of the class.
 *                     public static final EnumMeta META = getMeta(XYZ.class);
 *                 - create a public static final attribute for every member of the enumeration.
 *                     public static final XYZ ABC = new XYZ(&quot;abxc&quot;,&quot;My ABC&quot;);
 * </pre>
 */
public abstract class EnumItem implements IIdentifiable, Comparable {

	static {
		new CanonicalFromEnumItemConverter();
		new CanonicalFromEnumMetaConverter();
	}

	private static final Map META_INSTANCES = new HashMap();

	protected static EnumMeta getMeta(Class clazz) {
		return (EnumMeta) META_INSTANCES.computeIfAbsent(clazz, (tmp) -> new EnumMeta(clazz));
	}

	private String iconName;

	/** The enumeration id */
	private final String id;

	/** The label to use for the enumeration value */
	private Object label;

	/**
	 * An integer defining the natural order of the items.
	 */
	private final int weight;

	private final EnumMeta<EnumItem> meta;

	protected EnumItem(EnumMeta pMeta, String pId, Object pLabel, int pWeight) {
		super();
		this.meta = pMeta;
		this.meta.addItem(this);
		this.id = pId;
		this.label = pLabel;
		this.weight = pWeight;
	}

	protected EnumItem(String pId) {
		super();
		this.meta = getMeta(getClass());
		this.weight = this.meta.size();
		this.meta.addItem(this);
		this.id = pId;
		this.label = MessageTools.getMessage(this, id);
	}

	protected EnumItem(String pId, IMessage pMessage) {
		super();
		this.meta = getMeta(getClass());
		this.weight = this.meta.size();
		this.meta.addItem(this);
		this.id = pId;
		this.label = pMessage;
	}

	protected EnumItem(String pId, IMessage pMessage, int pWeight) {
		super();
		this.meta = getMeta(getClass());
		this.meta.addItem(this);
		this.id = pId;
		this.label = pMessage;
		this.weight = pWeight;
	}

	protected EnumItem(String pId, int pWeight) {
		super();
		this.meta = getMeta(getClass());
		this.meta.addItem(this);
		this.id = pId;
		this.label = MessageTools.getMessage(this, id);
		this.weight = pWeight;
	}

	protected EnumItem(String pId, String pLabel) {
		super();
		this.meta = getMeta(getClass());
		this.weight = this.meta.size();
		this.meta.addItem(this);
		this.id = pId;
		this.label = pLabel;
	}

	protected EnumItem(String pId, String pLabel, int pWeight) {
		super();
		this.meta = getMeta(getClass());
		this.meta.addItem(this);
		this.id = pId;
		this.label = pLabel;
		this.weight = pWeight;
	}

	@Override
	public int compareTo(Object o) {
		EnumItem other = (EnumItem) o;
		return this.getWeight() - other.getWeight();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnumItem) {
			return this.meta == ((EnumItem) obj).meta && this.id.equals(((EnumItem) obj).id);
		}
		return super.equals(obj);
	}

	protected String getDefaultLabel() {
		return id;
	}

	public String getDescription() {
		return getTip();
	}

	public String getIconName() {
		return iconName;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getLabel() {
		if (label instanceof IMessage) {
			return ((IMessage) label).getString();
		}
		return (String) label;
	}

	public String getLocalizedLabel() {
		return getLabel();
	}

	protected EnumMeta getMetaInstances() {
		return meta;
	}

	public String getTip() {
		return getLabel();
	}

	protected int getWeight() {
		return weight;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	protected void setDefault() {
		getMetaInstances().setDefault(this);
	}

	protected void setIconName(String iconName) {
		this.iconName = iconName;
	}

	protected void setLabel(Object label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
