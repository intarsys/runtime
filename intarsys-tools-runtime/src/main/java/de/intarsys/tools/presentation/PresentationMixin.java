package de.intarsys.tools.presentation;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IAttribute;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;
import de.intarsys.tools.message.MessageBundleFactory;
import de.intarsys.tools.message.MessageTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.IClassLoaderSupport;

/**
 * A ready to use, pluggable {@link IPresentationSupport} solution.
 * 
 */
public class PresentationMixin implements IPresentationSupport {

	private static final Object UNDEFINED = new Object();

	private final Object object;

	private Object label;

	private Object description;

	private Object tip;

	private Object iconName = UNDEFINED;

	private IMessageBundle messageBundle;

	private String codePrefix;

	private Object[] formatArgs;

	public PresentationMixin(Object object) {
		super();
		this.object = object;
	}

	protected String basicGetDescription() {
		IMessageBundle bundle = getMessageBundle();
		String key = getCodePrefix() + ".description";
		String pattern = bundle.getPattern(key);
		if (pattern != null) {
			description = bundle.format(pattern, getFormatArgs());
			return (String) description;
		}
		return getTip();
	}

	protected String basicGetIconName() {
		IMessageBundle bundle = getMessageBundle();
		String key = getCodePrefix() + ".icon";
		String pattern = bundle.getPattern(key);
		if (pattern != null) {
			iconName = bundle.format(pattern, getFormatArgs());
			return (String) iconName;
		}
		iconName = null;
		return (String) iconName;
	}

	protected String basicGetLabel() {
		IMessageBundle bundle = getMessageBundle();
		String key = getCodePrefix() + ".label";
		String pattern = bundle.getPattern(key);
		if (pattern != null) {
			label = bundle.format(pattern, getFormatArgs());
			return (String) label;
		}
		return key;
	}

	protected String basicGetTip() {
		IMessageBundle bundle = getMessageBundle();
		String key = getCodePrefix() + ".tip";
		String pattern = bundle.getPattern(key);
		if (pattern != null) {
			tip = bundle.format(pattern, getFormatArgs());
			return (String) tip;
		}
		return getLabel();
	}

	public void configure(IElement element) throws ConfigurationException {
		if (element == null) {
			return;
		}
		IAttribute attribute;
		attribute = element.attribute("icon");
		if (attribute != null) {
			setIconName(attribute.getData());
		}
		attribute = element.attribute("label");
		if (attribute != null) {
			setLabel(attribute.getData());
		}
		attribute = element.attribute("tip");
		if (attribute != null) {
			setTip(attribute.getData());
		}
		attribute = element.attribute("description");
		if (attribute != null) {
			setDescription(attribute.getData());
		}
	}

	protected IMessageBundle createMessageBundle() {
		if (object instanceof IMessageBundleSupport) {
			return ((IMessageBundleSupport) object).getMessageBundle();
		}
		if (object instanceof IClassLoaderSupport) {
			return MessageBundleFactory.get().getMessageBundle(getMessageBundleName(), ((IClassLoaderSupport) object)
					.getClassLoader());
		}
		return MessageTools.getMessageBundle(getMessageBundleClass());
	}

	public String getCodePrefix() {
		if (codePrefix == null) {
			return ClassTools.getUnqualifiedName(getMessageBundleClass());
		}
		return codePrefix;
	}

	@Override
	public String getDescription() {
		if (description == null) {
			return basicGetDescription();
		}
		if (description instanceof IMessage) {
			return ((IMessage) description).getString();
		}
		return (String) description;
	}

	public Object[] getFormatArgs() {
		return formatArgs;
	}

	@Override
	public String getIconName() {
		if (iconName == UNDEFINED) {
			return basicGetIconName();
		}
		if (iconName instanceof IMessage) {
			return ((IMessage) iconName).getString();
		}
		return (String) iconName;
	}

	@Override
	public String getLabel() {
		if (label == null) {
			return basicGetLabel();
		}
		if (label instanceof IMessage) {
			return ((IMessage) label).getString();
		}
		return (String) label;
	}

	public IMessageBundle getMessageBundle() {
		if (messageBundle == null) {
			messageBundle = createMessageBundle();
		}
		return messageBundle;
	}

	protected Class getMessageBundleClass() {
		return object.getClass();
	}

	protected String getMessageBundleName() {
		return MessageTools.getBundleName(object.getClass());
	}

	public Object getObject() {
		return object;
	}

	@Override
	public String getTip() {
		if (tip == null) {
			return basicGetTip();
		}
		if (tip instanceof IMessage) {
			return ((IMessage) tip).getString();
		}
		return (String) tip;
	}

	public boolean isLabelDefined() {
		if (label != null) {
			return true;
		}
		IMessageBundle bundle = getMessageBundle();
		String key = getCodePrefix() + ".label";
		String pattern = bundle.getPattern(key);
		return pattern != null;
	}

	public void setCodePrefix(String codePrefix) {
		this.codePrefix = codePrefix;
	}

	public void setDescription(Object description) {
		this.description = description;
	}

	public void setFormatArgs(Object[] formatArgs) {
		this.formatArgs = formatArgs;
	}

	public void setIconName(Object iconName) {
		this.iconName = iconName;
	}

	public void setLabel(Object label) {
		this.label = label;
	}

	public void setMessageBundle(IMessageBundle messageBundle) {
		this.messageBundle = messageBundle;
	}

	public void setTip(Object tip) {
		this.tip = tip;
	}

}
