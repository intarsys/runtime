/*
 * Copyright (c) 2012, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.concurrent.ForwardedThreadLocal;

/**
 * A tool class for implementing a generic attachment feature.
 * 
 * Attachments are managed per thread.
 * 
 */
public final class AttachmentTools {

	static class AttachmentList {
		protected List<Attachment> list = new ArrayList<>();
		protected int counter;
	}

	private static final List<Attachment> EMPTY = Collections.emptyList();

	private static final ThreadLocal<AttachmentList> THREADATTACHMENTS = new ForwardedThreadLocal<>();

	/**
	 * Add a {@link Attachment} to the thread context.
	 * 
	 * @param attachment
	 *            The new attachment
	 */
	public static void addAttachment(Attachment attachment) {
		if (attachment == null) {
			return;
		}
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return;
		}
		temp.list.add(attachment);
	}

	/**
	 * Add a {@link Attachment} to the thread context.
	 * 
	 * 
	 * @param key
	 *            The key
	 * @param value
	 *            The attached object
	 */
	public static void addAttachment(String key, Object value) {
		addAttachment(new Attachment(key, value));
	}

	/**
	 * Add a list of {@link Attachment} instances to the thread context.
	 * 
	 * @param pAttachments
	 */
	public static void addAttachments(List<Attachment> pAttachments) {
		if (pAttachments == null) {
			return;
		}
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return;
		}
		temp.list.addAll(pAttachments);
	}

	public static void attach() {
		AttachmentList temp = THREADATTACHMENTS.get();
		if (temp == null) {
			temp = new AttachmentList();
			THREADATTACHMENTS.set(temp);
		} else {
			temp.counter++;
		}
	}

	protected static AttachmentList basicGetAttachments() {
		return THREADATTACHMENTS.get();
	}

	/**
	 * Clear all attachments for the thread context.
	 * 
	 */
	public static void clearAttachments() {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return;
		}
		temp.list.clear();
	}

	public static void detach() {
		AttachmentList temp = THREADATTACHMENTS.get();
		if (temp == null) {
			return;
		}
		if (temp.counter <= 0) {
			temp.list.clear();
			THREADATTACHMENTS.remove();
		}
		temp.counter--;
	}

	/**
	 * Get the first {@link Attachment} matching "key" in the thread context.
	 * 
	 * @param key
	 *            The key value that is searched
	 * @return The first matching {@link Attachment} or <code>null</code>
	 */
	public static Attachment getAttachment(String key) {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return null;
		}
		for (Iterator it = temp.list.iterator(); it.hasNext();) {
			Attachment attachment = (Attachment) it.next();
			if (key.equals(attachment.getKey())) {
				return attachment;
			}
		}
		return null;
	}

	/**
	 * Get all {@link Attachment} instances for the thread context.
	 * 
	 * This will never return null.
	 * 
	 * @return Get all attachments for the thread context.
	 */
	public static List<Attachment> getAttachments() {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return EMPTY;
		}
		return new ArrayList<>(temp.list);
	}

	/**
	 * Get all attachments matching "key" in the thread context.
	 * 
	 * @param key
	 *            The key value that is searched
	 * @return All matching attachments
	 */
	public static List<Attachment> getAttachments(String key) {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return EMPTY;
		}
		List<Attachment> result = new ArrayList<>();
		for (Iterator it = temp.list.iterator(); it.hasNext();) {
			Attachment attachment = (Attachment) it.next();
			if (key.equals(attachment.getKey())) {
				result.add(attachment);
			}
		}
		return result;
	}

	/**
	 * <code>true</code> if the thread context has an attachment for "key"
	 * 
	 * @param key
	 *            The key value that is searched
	 * @return <code>true</code> if object has an attachment for "key"
	 */
	public static boolean hasAttachment(String key) {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return false;
		}
		for (Iterator it = temp.list.iterator(); it.hasNext();) {
			Attachment attachment = (Attachment) it.next();
			if (key.equals(attachment.getKey())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove attachment with attached object in the thread context.
	 * 
	 * @param attached
	 * @return true if removed
	 */
	public static boolean removeAttached(Object attached) {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return false;
		}
		for (Iterator it = temp.list.iterator(); it.hasNext();) {
			Attachment attachment = (Attachment) it.next();
			if (attachment.getAttached() == attached) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove attachment in the thread context.
	 * 
	 * @param pAttachment
	 * @return true if removed
	 */
	public static boolean removeAttachment(Attachment pAttachment) {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return false;
		}
		for (Iterator it = temp.list.iterator(); it.hasNext();) {
			Attachment attachment = (Attachment) it.next();
			if (attachment == pAttachment) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove all attachments for key in the thread context.
	 * 
	 * @param key
	 *            The key value for the attachments to be removed
	 */
	public static void removeAttachments(String key) {
		AttachmentList temp = basicGetAttachments();
		if (temp == null) {
			return;
		}
		for (Iterator it = temp.list.iterator(); it.hasNext();) {
			Attachment attachment = (Attachment) it.next();
			if (key.equals(attachment.getKey())) {
				it.remove();
			}
		}
	}

	private AttachmentTools() {
	}
}
