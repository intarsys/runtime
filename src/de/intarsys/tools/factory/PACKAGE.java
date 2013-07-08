package de.intarsys.tools.factory;

import java.util.logging.Logger;

import de.intarsys.tools.logging.LogTools;
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.message.MessageBundleTools;

public class PACKAGE {

	public final static MessageBundle Messages = MessageBundleTools
			.getMessageBundle(PACKAGE.class);

	public final static Logger Log = LogTools.getLogger(PACKAGE.class);

}
