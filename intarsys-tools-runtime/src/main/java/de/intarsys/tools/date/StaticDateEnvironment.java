package de.intarsys.tools.date;

import java.util.Date;

import javax.annotation.PostConstruct;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

public class StaticDateEnvironment implements IDateEnvironment {

	private static final ILogger Log = LogTools.getLogger(StaticDateEnvironment.class);

	private Date fixedDate = new Date();

	public StaticDateEnvironment() {
	}

	public StaticDateEnvironment(Date fixedDate) {
		this.fixedDate = fixedDate;
	}

	public Date getFixedDate() {
		return fixedDate;
	}

	@PostConstruct
	public void install() {
		Log.info("DateEnvironment is {}", this);
		Log.info("+- System.currentTimeMillis() {}", System.currentTimeMillis());
		Log.info("+- DateEnvironment.now() {}", this.now().getTime());
	}

	@Override
	public Date now() {
		return fixedDate;
	}

	public void setFixedDate(Date fixedDate) {
		this.fixedDate = fixedDate;
	}

}
