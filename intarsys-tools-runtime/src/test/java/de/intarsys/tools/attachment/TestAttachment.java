package de.intarsys.tools.attachment;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

public class TestAttachment extends TestCase {

	public void testAttachmentContext() {
		List<Attachment> attachments;
		//
		attachments = AttachmentTools.getAttachments();
		assertTrue(attachments.size() == 0);
		//
		AttachmentTools.addAttachment("foo", "bar");
		attachments = AttachmentTools.getAttachments();
		assertTrue(attachments.size() == 0);
		//
		AttachmentTools.attach();
		AttachmentTools.addAttachment("foo", "bar");
		AttachmentTools.detach();
		attachments = AttachmentTools.getAttachments();
		assertTrue(attachments.size() == 0);
		//
		AttachmentTools.attach();
		AttachmentTools.addAttachment("foo", "bar");
		attachments = AttachmentTools.getAttachments();
		AttachmentTools.detach();
		assertTrue(attachments.size() == 1);
	}

	public void testAttachmentContextThreaded() throws Exception {
		Thread t1;
		Thread t2;
		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);
		AtomicReference<Throwable> ex = new AtomicReference<>();
		//
		t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				AttachmentTools.attach();
				AttachmentTools.addAttachment("foo", "bar");
				s1.release();
				s2.acquireUninterruptibly();
				List<Attachment> attachments = AttachmentTools.getAttachments();
				AttachmentTools.detach();
				attachments.get(0).getAttached().equals("bar");
				if (attachments.size() != 1) {
					ex.set(new RuntimeException());
				}
			}
		});
		t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				AttachmentTools.attach();
				AttachmentTools.addAttachment("gnu", "gnat");
				s2.release();
				s1.acquireUninterruptibly();
				List<Attachment> attachments = AttachmentTools.getAttachments();
				AttachmentTools.detach();
				attachments.get(0).getAttached().equals("gnat");
				if (attachments.size() != 1) {
					ex.set(new RuntimeException());
				}
			}
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		assertThat(ex.get(), nullValue());
	}

}
