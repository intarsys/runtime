package de.intarsys.tools.date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;

public class TestTimeEnvironment {

	public TestTimeEnvironment() {
	}

	@Test
	public void convert() {
		Date date1 = new java.util.Date();
		Instant instant = Instant.ofEpochMilli(date1.getTime());
		Date date2 = Date.from(instant);

		assertThat(date2, is(date1));
	}
}
