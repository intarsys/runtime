package de.intarsys.tools.eventbus;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.wrapper.IRequestEvent;
import de.intarsys.tools.facade.FacadeTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * This object describes a subscription to the {@link IEventBus}.
 * 
 */
public class FunctorSubscription implements IElementConfigurable {

	private static final ILogger Log = LogTools.getLogger(FunctorSubscription.class);

	private final INotificationListener listener = new INotificationListener() {
		@Override
		public void handleEvent(Event event) {
			onNotification(event);
		}
	};

	private EventType type;

	private EventSourcePredicate predicate;

	private IFunctor functor;

	@Override
	public void configure(IElement element) throws ConfigurationException {
		try {
			Class eventClazz = ElementTools.createClass(element, "event", Event.class, null);
			if (eventClazz == null) {
				type = EventType.ALWAYS;
			} else {
				type = EventType.lookupEventType(eventClazz.getName());
			}
			IElement ePredicate = element.element("predicate");
			if (ePredicate == null) {
				Class sourceClazz = ElementTools.createClass(element, "source", Object.class, null);
				if (sourceClazz == null) {
					predicate = new EventSourceAll();
				} else {
					predicate = new EventSourceClass(sourceClazz);
				}
			} else {
				predicate = ElementTools.createObject(ePredicate, EventSourcePredicate.class, null, Args.create());
			}
			functor = ElementTools.createFunctor(this, element, "functor", null);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	protected void onNotification(Event event) {
		try {
			IArgs args = Args.create();
			args.put(IRequestEvent.ARG_EVENT, FacadeTools.createFacade(event));
			args.put(IRequestEvent.ARG_JEVENT, event);
			IFunctorCall call = new FunctorCall(this, args);
			functor.perform(call);
		} catch (FunctorException e) {
			Log.log(Level.WARN, e.getMessage(), e);
		}
	}

	@PostConstruct
	public void postConstruct() {
		EventBus.get().subscribe(predicate, type, listener);
	}
}
