/*
 * Copyright (c) 2008, intarsys consulting GmbH
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
package de.intarsys.tools.functor;

import java.util.regex.Pattern;

import de.intarsys.tools.string.StringTools;

/**
 * A common superclass for {@link IDeclarationElement} instances. This one has a
 * very simple implementation for modifiers. Only the presence of the modifier
 * string fragment in declared modifier string is checked.
 * 
 */
abstract public class DeclarationElement extends Declaration implements
		IDeclarationElement {

	public static boolean validateName(String name) {
		if (StringTools.isEmpty(name)) {
			return false;
		}
		return Pattern.matches("[\\.]|[a-zA-Z0-9_]+", name);
	}

	public static boolean validatePath(String name) {
		if (StringTools.isEmpty(name)) {
			return false;
		}
		return Pattern.matches("([a-zA-Z0-9_]+\\.)+", name);
	}

	private String modifierString;

	private String name;

	private String description;

	public DeclarationElement(Object declarationContext, String name,
			String modifiers) {
		this(declarationContext, name, modifiers, null);
	}

	public DeclarationElement(Object declarationContext, String name,
			String modifiers, String description) {
		super(declarationContext);
		this.modifierString = modifiers;
		this.name = name;
		this.description = description;
	}

	public void addModifier(String modifier) {
		if (StringTools.isEmpty(modifierString)) {
			modifierString = modifier;
			return;
		}
		if (modifierString.indexOf(modifier) >= 0) {
			return;
		}
		modifierString = modifierString + ";" + modifier;
	}

	public String getDescription() {
		return description;
	}

	public String[] getModifiers() {
		if (StringTools.isEmpty(modifierString)) {
			return new String[0];
		}
		return modifierString.replaceAll("\\s", "").split("\\;");
	}

	public String getModifierString() {
		return modifierString;
	}

	public String getName() {
		return name;
	}

	public boolean hasModifier(String modifier) {
		if (modifierString == null) {
			return false;
		}
		return modifierString.indexOf(modifier) >= 0;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setModifierString(String modifierString) {
		this.modifierString = modifierString;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "declaration <'" + getName() + "'> [" + modifierString + "]";
	}
}
