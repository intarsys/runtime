/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.resourcetracker;

import java.lang.ref.ReferenceQueue;

/**
 * An object to keep track of platform resources that should be disposed upon
 * garbage collection of some container object.
 * <p>
 * To use this create a subclass implementing the dispose strategy and add new
 * resources using "track".
 * <p>
 * The resources will be disposed automatically after garbage collection of the
 * container in a separate worker thread.
 */
abstract public class ResourceTracker {

	private IResourceReference[] references;

	private int next = 0;

	final private ResourceFinalizer finalizer;

	public ResourceTracker() {
		this(500);
	}

	public ResourceTracker(int size) {
		super();
		this.references = new IResourceReference[size];
		this.finalizer = ResourceFinalizer.get();
	}

	public ResourceTracker(ResourceFinalizer finalizer) {
		this(finalizer, 500);
	}

	public ResourceTracker(ResourceFinalizer finalizer, int size) {
		super();
		this.references = new IResourceReference[size];
		this.finalizer = finalizer;
	}

	protected synchronized IResourceReference add(IResourceReference ref) {
		finalizer.ensureStarted();
		if (next >= references.length) {
			// ooops - maybe finalizer thread is starving. help!
			System.gc();
			finalizer.drainQueue();
			if (next >= references.length) {
				IResourceReference[] newReferences = new IResourceReference[references.length << 1];
				System.arraycopy(references, 0, newReferences, 0,
						references.length);
				references = newReferences;
			}
		}
		references[next++] = ref;
		return ref;
	}

	abstract protected void basicDispose(Object resource);

	protected void dispose(IResourceReference ref) {
		remove(ref);
		if (ref.getResource() != null) {
			basicDispose(ref.getResource());
		}
	}

	protected ReferenceQueue getQueue() {
		return finalizer.getQueue();
	}

	synchronized protected void remove(IResourceReference ref) {
		int length = next;
		for (int i = 0; i < length; i++) {
			if (references[i] == ref) {
				next--;
				references[i] = references[next];
				references[next] = null;
				break;
			}
		}
		// System.out.println("tracker removed instance, now " + next);
	}

	public IResourceReference trackPhantom(Object container, Object resource) {
		return add(new PhantomResourceReference(container, resource, this));
	}

	public IResourceReference trackSoft(Object container, Object resource) {
		return add(new SoftResourceReference(container, resource, this));
	}

	public IResourceReference trackWeak(Object container, Object resource) {
		return add(new WeakResourceReference(container, resource, this));
	}
}
