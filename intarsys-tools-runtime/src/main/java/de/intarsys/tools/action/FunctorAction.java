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
package de.intarsys.tools.action;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * The behavior of this action is defined using IFunctor objects.
 */
public class FunctorAction extends Action implements INotificationListener {

	public static boolean toBoolean(Object object) {
		if (object instanceof Boolean) {
			return ((Boolean) object).booleanValue();
		}
		if (object instanceof String) {
			return ((String) object).equalsIgnoreCase("true");
		}
		if (object instanceof Number) {
			return ((Number) object).intValue() != 0;
		}
		return false;
	}

	/** functor for the behaviour of requesting the checked state. */
	private IFunctor checkedFunctor;

	/** functor for the behaviour of requesting the enabled state. */
	private IFunctor enabledFunctor;

	/** functor for the behaviour when selecting the action. */
	private IFunctor effectFunctor;

	public FunctorAction() {
		super();
	}

	public FunctorAction(Object owner) {
		super(owner);
	}

	public FunctorAction(Object owner, boolean checked) {
		super(owner, checked);
	}

	public FunctorAction(String id, Object owner) {
		super(id, owner);
	}

	public FunctorAction(String id, Object owner, boolean checked) {
		super(id, owner, checked);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		IElement functorElement;
		IFunctor functor;
		//
		try {
			functorElement = element.element("effect");
			if (functorElement != null) {
				functor = ElementTools.createFunctor(getOwner(), functorElement, null, getOwner());
				setEffectFunctor(functor);
			}
			functorElement = element.element("checked");
			if (functorElement != null) {
				functor = ElementTools.createFunctor(getOwner(), functorElement, null, getOwner());
				setCheckedFunctor(functor);
			}
			functorElement = element.element("enabled");
			if (functorElement != null) {
				functor = ElementTools.createFunctor(getOwner(), functorElement, null, getOwner());
				setEnabledFunctor(functor);
			}
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	public IFunctor getCheckedFunctor() {
		return checkedFunctor;
	}

	public IFunctor getEffectFunctor() {
		return effectFunctor;
	}

	public IFunctor getEnabledFunctor() {
		return enabledFunctor;
	}

	@Override
	public void handleEvent(Event event) {
		triggerChange(null);
	}

	@Override
	public boolean isChecked(IFunctorCall call) {
		IFunctor functor = getCheckedFunctor();
		if (functor != null) {
			try {
				if (getOwner() != null) {
					call.setReceiver(getOwner());
				}
				return toBoolean(functor.perform(call));
			} catch (FunctorException e) {
				// todo 2 check design, maybe throw runtime exception
				return false;
			}
		} else {
			return super.isChecked(call);
		}
	}

	@Override
	public boolean isEnabled(IFunctorCall call) {
		IFunctor functor = getEnabledFunctor();
		if (functor != null) {
			try {
				if (getOwner() != null) {
					call.setReceiver(getOwner());
				}
				return toBoolean(functor.perform(call));
			} catch (FunctorException e) {
				// todo 2 check design, maybe throw runtim exception
				return false;
			}
		} else {
			return super.isEnabled(call);
		}
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		IFunctor functor = getEffectFunctor();
		if (functor != null) {
			if (getOwner() != null) {
				call.setReceiver(getOwner());
			}
			return functor.perform(call);
		} else {
			return super.perform(call);
		}
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		super.serialize(element);
		if (effectFunctor instanceof IElementSerializable) {
			IElement child = element.newElement("effect");
			((IElementSerializable) effectFunctor).serialize(child);
		}
		if (checkedFunctor instanceof IElementSerializable) {
			IElement child = element.newElement("checked");
			((IElementSerializable) checkedFunctor).serialize(child);
		}
		if (enabledFunctor instanceof IElementSerializable) {
			IElement child = element.newElement("enabled");
			((IElementSerializable) enabledFunctor).serialize(child);
		}
	}

	public void setCheckedFunctor(IFunctor functor) {
		checkedFunctor = functor;
		if (checkedFunctor instanceof INotificationSupport) {
			((INotificationSupport) checkedFunctor).addNotificationListener(AttributeChangedEvent.ID, this);
		}
		setCheckStyleOn();
	}

	public void setEffectFunctor(IFunctor functor) {
		effectFunctor = functor;
		if (effectFunctor instanceof INotificationSupport) {
			((INotificationSupport) effectFunctor).addNotificationListener(AttributeChangedEvent.ID, this);
		}
	}

	public void setEnabledFunctor(IFunctor functor) {
		enabledFunctor = functor;
		if (enabledFunctor instanceof INotificationSupport) {
			((INotificationSupport) enabledFunctor).addNotificationListener(AttributeChangedEvent.ID, this);
		}
	}

	@Override
	public String toString() {
		try {
			return "FunctorAction '" + getId() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RuntimeException e) {
			return "<unprintable FunctorAction>"; //$NON-NLS-1$
		}
	}
}
