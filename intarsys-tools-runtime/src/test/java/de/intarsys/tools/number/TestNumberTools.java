package de.intarsys.tools.number;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

public class TestNumberTools {

	@Test
	public void compute_bytes_int() {
		int size;
		//
		size = NumberTools.byteSizeOf(0);
		assertThat(size, is(0));
		//
		size = NumberTools.byteSizeOf(1);
		assertThat(size, is(1));
		//
		size = NumberTools.byteSizeOf(0xff);
		assertThat(size, is(1));
		//
		size = NumberTools.byteSizeOf(0x0100);
		assertThat(size, is(2));
		//
		size = NumberTools.byteSizeOf(0x010000);
		assertThat(size, is(3));
		//
		size = NumberTools.byteSizeOf(0x01000000);
		assertThat(size, is(4));
	}

	@Test
	public void compute_bytes_long() {
		int size;
		//
		size = NumberTools.byteSizeOf(0L);
		assertThat(size, is(0));
		//
		size = NumberTools.byteSizeOf(1L);
		assertThat(size, is(1));
		//
		size = NumberTools.byteSizeOf(0xffL);
		assertThat(size, is(1));
		//
		size = NumberTools.byteSizeOf(0x0100L);
		assertThat(size, is(2));
		//
		size = NumberTools.byteSizeOf(0x010000L);
		assertThat(size, is(3));
		//
		size = NumberTools.byteSizeOf(0x01000000L);
		assertThat(size, is(4));
		//
		size = NumberTools.byteSizeOf(0x0100000000L);
		assertThat(size, is(5));
		//
		size = NumberTools.byteSizeOf(0x010000000000L);
		assertThat(size, is(6));
		//
		size = NumberTools.byteSizeOf(0x01000000000000L);
		assertThat(size, is(7));
		//
		size = NumberTools.byteSizeOf(0x0100000000000000L);
		assertThat(size, is(8));
	}

	@Test
	public void testToByteSigned() {
		int result;
		//
		result = NumberTools.toIntSigned((byte) 0x00);
		Assert.assertTrue(result == 0);
		//
		result = NumberTools.toIntSigned((byte) 0x01);
		Assert.assertTrue(result == 1);
		//
		result = NumberTools.toIntSigned((byte) 0x7f);
		Assert.assertTrue(result == 127);
		//
		result = NumberTools.toIntSigned((byte) 0x80);
		Assert.assertTrue(result == -128);
		//
		result = NumberTools.toIntSigned((byte) 0x81);
		Assert.assertTrue(result == -127);
		//
		result = NumberTools.toIntSigned((byte) 0xff);
		Assert.assertTrue(result == -1);
	}

	@Test
	public void testToByteUnsigned() {
		int result;
		//
		result = NumberTools.toIntUnsigned((byte) 0x00);
		Assert.assertTrue(result == 0);
		//
		result = NumberTools.toIntUnsigned((byte) 0x01);
		Assert.assertTrue(result == 1);
		//
		result = NumberTools.toIntUnsigned((byte) 0x7f);
		Assert.assertTrue(result == 127);
		//
		result = NumberTools.toIntUnsigned((byte) 0x80);
		Assert.assertTrue(result == 128);
		//
		result = NumberTools.toIntUnsigned((byte) 0x81);
		Assert.assertTrue(result == 129);
		//
		result = NumberTools.toIntUnsigned((byte) 0xff);
		Assert.assertTrue(result == 255);
	}

}
