package de.intarsys.tools.jaxb;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Marshalling java timestamps.
 */
public class TimestampAdapter extends XmlAdapter<Long, Timestamp> {

	@Override
	public Long marshal(Timestamp v) throws Exception {
		return v.getTime();
	}

	@Override
	public Timestamp unmarshal(Long v) throws Exception {
		return new Timestamp(v);
	}

}
