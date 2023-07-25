package de.intarsys.tools.notice;

import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;

/**
 * {@link INotice} transport via the event notification mechanism
 * 
 */
public class NoticeEvent extends Event {

	public static final EventType ID = new EventType(NoticeEvent.class.getName());

	private transient INotice notice;

	public NoticeEvent(Object source, IMessage msg) {
		super(source);
		this.notice = new Notice(msg);
	}

	public NoticeEvent(Object source, IMessageBundle bundle, String code, Object... args) {
		super(source);
		this.notice = new Notice(bundle, code, args);
	}

	public NoticeEvent(Object source, INotice notice) {
		super(source);
		this.notice = notice;
	}

	public NoticeEvent(Object source, String code, String text, Object... args) {
		super(source);
		this.notice = new Notice(code, text, args);
	}

	@Override
	public EventType getEventType() {
		return ID;
	}

	public INotice getNotice() {
		return notice;
	}

	public void setNotice(INotice notice) {
		this.notice = notice;
	}

}
