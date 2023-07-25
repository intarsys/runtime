package de.intarsys.tools.exception;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.reflect.ObjectCreationException;

public class TestExceptionTools {

	@Test
	public void testCreateTyped() {
		Exception in;
		Exception out;
		//
		in = new IOException();
		out = ExceptionTools.createTyped(in, IOException.class);
		Assert.assertTrue(out instanceof IOException);
		Assert.assertTrue(out.getCause() == null);
		//
		in = new IOException();
		out = ExceptionTools.createTyped(in, ObjectCreationException.class);
		Assert.assertTrue(out instanceof ObjectCreationException);
		Assert.assertTrue(out.getCause() instanceof IOException);
	}

	@Test
	public void testCreateTypedFromChain() {
		Exception in;
		Exception out;
		//
		in = new IOException();
		out = ExceptionTools.createTypedFromChain(in, IOException.class);
		Assert.assertTrue(out instanceof IOException);
		Assert.assertTrue(out.getCause() == null);
		//
		in = new IOException();
		out = ExceptionTools.createTypedFromChain(in, ObjectCreationException.class);
		Assert.assertTrue(out instanceof ObjectCreationException);
		Assert.assertTrue(out.getCause() instanceof IOException);
		//
		in = new IOException(new ObjectCreationException());
		out = ExceptionTools.createTypedFromChain(in, ObjectCreationException.class);
		Assert.assertTrue(out instanceof ObjectCreationException);
		Assert.assertTrue(out.getCause() == null);
		//
		in = new IOException(new RuntimeException());
		out = ExceptionTools.createTypedFromChain(in, ObjectCreationException.class);
		Assert.assertTrue(out instanceof ObjectCreationException);
		Assert.assertTrue(out.getCause() instanceof IOException);
		Assert.assertTrue(out.getCause().getCause() instanceof RuntimeException);
	}

	@Test
	public void testFail() {
		try {
			ExceptionTools.fail();
			Assert.fail();
		} catch (Exception e) {
			//
		}
	}
}
