package de.intarsys.tools.oid;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestPronouncableOIDGenerator {

	@Test
	public void testPlain() {
		PronouncableOIDGenerator generator;
		String oid;
		Set<String> oids = new HashSet<>();
		//
		generator = new PronouncableOIDGenerator();
		for (int i = 0; i < 10; i++) {
			oid = generator.createOID();
			oids.add(oid);
		}
		assertThat(oids.size(), is(10));
	}

}
