/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.logging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import de.intarsys.tools.concurrent.ExecutorEnvironment;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

public class PerformLogVM {

	private static final ILogger Log = LogTools.getLogger(PerformLogVM.class);

	protected void logVM(final Map properties) {
		Log.info("maxmemory=" + Runtime.getRuntime().maxMemory());
		for (Iterator it = properties.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Log.info(String.valueOf(entry.getKey()) + "=" + entry.getValue());
		}
	}

	@PostConstruct
	public void perform() {
		ExecutorEnvironment.get().getService().submit(new Runnable() {
			@Override
			public void run() {
				// defend against concurrent modification!
				logVM(new HashMap(System.getProperties()));
			}
		});
	}

}
