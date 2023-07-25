package de.intarsys.tools.date;

import java.util.Date;

import javax.annotation.PostConstruct;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Standard IDateEnvironment implementation using the system time to provide
 * date information.
 * 
 */
public class StandardDateEnvironment implements IDateEnvironment {

	private static final ILogger Log = LogTools.getLogger(StandardDateEnvironment.class);

	public StandardDateEnvironment() {
		super();
	}

	@PostConstruct
	public void install() {
		Log.info("DateEnvironment is {}", this);
		Log.info("+- System.currentTimeMillis() {}", System.currentTimeMillis());
		Log.info("+- DateEnvironment.now() {}", this.now().getTime());
	}

	@Override
	public Date now() {
		return new Date();
	}

}
