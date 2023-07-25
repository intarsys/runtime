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
package de.intarsys.tools.ui;

import static de.intarsys.tools.ui.PACKAGE.Log;

import de.intarsys.tools.activity.ActivityEnvironment;
import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.IActivityHandler;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;

/** 
 * 
 */
public abstract class CommonToolkit implements IToolkit, IActivityHandler, IComponentProvider {

	private boolean silent;

	protected CommonToolkit() {
		super();
	}

	@Override
	public <R> void activityEnter(IActivity<R> activity) {
		Class<?> clazz = activity.getClass();
		Class<?> expectedClass = IActivityHandler.class;
		Object toolkitObject = createToolkitObject(clazz, expectedClass, "Handler"); //$NON-NLS-1$
		if (toolkitObject != null) {
			IActivityHandler handler = (IActivityHandler) toolkitObject;
			handler.activityEnter(activity);
		} else {
			Log.debug("no toolkit object found for {}", activity); //$NON-NLS-1$
		}
	}

	@Override
	public void createComponent(Object parent) {
		IActivity active = ActivityEnvironment.get().getActiveActivity();
		if (active instanceof IComponentProvider) {
			((IComponentProvider) active).createComponent(parent);
		}
	}

	protected <T> Class<T> createToolkitClass(ClassLoader classLoader, Class<T> expectedType, String packageName,
			String id, String unqualifiedName, String suffix) {
		if (id == null) {
			return null;
		}
		id = id.toLowerCase();
		String className = packageName + StringTools.DOT + id + StringTools.DOT + unqualifiedName + suffix;
		try {
			return ClassTools.createClass(className, expectedType, classLoader);
		} catch (ObjectCreationException e) {
			//
		}
		return null;
	}

	@Override
	public <T> T createToolkitObject(Class<?> clazz, Class<T> expectedType, String suffix) {
		String className;
		Class<?> toolkitClass = null;
		T toolkitObject = null;

		className = clazz.getName();
		String packageName = ClassTools.getPackageName(className);
		String unqualifiedName = ClassTools.getUnqualifiedName(className);
		if (clazz.isInterface()) {
			if (unqualifiedName.startsWith("I")) { //$NON-NLS-1$
				unqualifiedName = unqualifiedName.substring(1);
			}
		}
		suffix = suffix == null ? StringTools.EMPTY : suffix;
		if (isUseSilentHandler()) {
			toolkitClass = createToolkitClass(null, expectedType, packageName, "silent", unqualifiedName, suffix);
		}
		if (toolkitClass == null) {
			toolkitClass = createToolkitClass(null, expectedType, packageName, getToolkitID(), unqualifiedName, suffix);
		}
		if (toolkitClass == null) {
			toolkitClass = createToolkitClass(null, expectedType, packageName, getToolkitIDFallback(), unqualifiedName,
					suffix);
		}
		if (toolkitClass != null) {
			try {
				toolkitObject = ObjectTools.createObject(toolkitClass, expectedType);
			} catch (ObjectCreationException e) {
				//
			}
		}
		return toolkitObject;
	}

	@Override
	public Object getComponent() {
		IActivity active = ActivityEnvironment.get().getActiveActivity();
		if (active instanceof IComponentProvider) {
			return ((IComponentProvider) active).getComponent();
		}
		return null;
	}

	protected String getToolkitID() {
		return "UI"; //$NON-NLS-1$
	}

	protected String getToolkitIDFallback() {
		return "UI"; //$NON-NLS-1$
	}

	@Override
	public boolean isSilent() {
		return silent;
	}

	protected boolean isUseSilentHandler() {
		return isSilent();
	}

	@Override
	public boolean setSilent(boolean silent) {
		boolean oldValue = this.silent;
		this.silent = silent;
		return oldValue;
	}

}
