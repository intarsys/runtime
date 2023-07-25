package de.intarsys.tools.factory;

import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.MessageTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

final class PACKAGE {

	private PACKAGE() {
	}


	public static final IMessageBundle Messages = MessageTools.getMessageBundle(PACKAGE.class);

	public static final ILogger Log = LogTools.getLogger(PACKAGE.class);

}
