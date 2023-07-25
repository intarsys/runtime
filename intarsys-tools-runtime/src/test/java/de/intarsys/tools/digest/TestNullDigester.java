package de.intarsys.tools.digest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

public class TestNullDigester {

	public void testBytes() throws IOException {
		NullDigester digester = new NullDigester();
		digester.digest(new byte[] { 0x01, 0x02, 0x03, 0x04 });
		IDigest digest = digester.digestFinal();
		assertThat(digest.getAlgorithmName(), is("NULL"));
		assertThat(digest.getBytes().length, is(4));
		assertThat(digest.getBytes(), is(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
		assertThat(digest.getEncoded(), is(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
		digest = digester.digestFinal();
		assertThat(digest.getBytes().length, is(0));
	}

	@Test
	public void testEmpty() {
		NullDigester digester = new NullDigester();
		IDigest digest = digester.digestFinal();
		assertThat(digest.getAlgorithmName(), is("NULL"));
		assertThat(digest.getBytes().length, is(0));
		digest = digester.digestFinal();
		assertThat(digest.getBytes().length, is(0));
	}

}
