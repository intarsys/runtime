/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * A simple transaction implementation.
 * <p>
 * "Transactional" tasks are delegated to the {@link IResource} instances
 * managed by the transaction.
 */
public abstract class Transaction implements ITransaction, Serializable {

	private static final AtomicInteger ID_COUNTER = new AtomicInteger();

	private static ILogger Log = PACKAGE.Log;

	private final List children = new ArrayList();

	private final String id = String.valueOf(ID_COUNTER.getAndIncrement());

	private final List<IResource> resources = new ArrayList<>();

	protected Transaction() {
		super();
	}

	protected void addTransaction(Transaction tx) {
		children.add(tx);
	}

	@Override
	public void commit() throws TransactionException {
		List lcChildren = new ArrayList(children);
		for (Iterator it = lcChildren.iterator(); it.hasNext();) {
			Transaction child = (Transaction) it.next();
			child.commit();
		}
		commitResources();
		stop();
	}

	protected void commitResources() throws TransactionException {
		for (Iterator it = resources.iterator(); it.hasNext();) {
			IResource resource = (IResource) it.next();
			try {
				resource.commit();
			} catch (ResourceException e) {
				throw new TransactionException(e);
			}
		}
	}

	@Override
	public void commitResume() throws TransactionException {
		List lcChildren = new ArrayList(children);
		for (Iterator it = lcChildren.iterator(); it.hasNext();) {
			Transaction child = (Transaction) it.next();
			child.commitResume();
		}
		commitResources();
	}

	protected Transaction createTransaction() {
		return new SubTransaction(this);
	}

	public void delist(IResource resource) throws TransactionException {
		if (!resources.remove(resource)) {
			throw new TransactionException("resource not listed");
		}
		try {
			resource.rollback();
		} catch (ResourceException e) {
			throw new TransactionException(e);
		}
	}

	public void enlist(IResource resource) throws TransactionException {
		try {
			resource.begin();
		} catch (ResourceException e) {
			throw new TransactionException(e);
		}
		resources.add(resource);
	}

	public String getId() {
		return id;
	}

	protected <T extends IResource> T getResource(IResourceType<T> resourceType) {
		for (IResource resource : resources) {
			if (resource.getType() == resourceType) {
				return (T) resource;
			}
		}
		return null;
	}

	protected void removeTransaction(Transaction tx) {
		children.remove(tx);
	}

	protected void resume() {
		if (getParent() != null) {
			((Transaction) getParent()).resume();
		}
		for (Iterator it = resources.iterator(); it.hasNext();) {
			IResource resource = (IResource) it.next();
			resource.resume();
		}
		Log.log(Level.DEBUG, "transaction[" + getId() + "] resumed");
	}

	@Override
	public void rollback() throws TransactionException {
		List lcChildren = new ArrayList(children);
		for (Iterator it = lcChildren.iterator(); it.hasNext();) {
			Transaction child = (Transaction) it.next();
			child.rollback();
		}
		rollbackResources();
		stop();
	}

	protected void rollbackResources() throws TransactionException {
		for (Iterator it = resources.iterator(); it.hasNext();) {
			IResource resource = (IResource) it.next();
			try {
				resource.rollback();
			} catch (ResourceException e) {
				throw new TransactionException(e);
			}
		}
	}

	@Override
	public void rollbackResume() throws TransactionException {
		List lcChildren = new ArrayList(children);
		for (Iterator it = lcChildren.iterator(); it.hasNext();) {
			Transaction child = (Transaction) it.next();
			child.rollbackResume();
		}
		rollbackResources();
	}

	protected void start() {
		Log.log(Level.DEBUG, "transaction[" + getId() + "] started");
	}

	protected void stop() {
		Log.log(Level.DEBUG, "transaction[" + getId() + "] stopped");
	}

	protected void suspend() {
		Log.log(Level.DEBUG, "transaction[" + getId() + "] suspended");
		List lcChildren = new ArrayList(children);
		for (Iterator it = lcChildren.iterator(); it.hasNext();) {
			Transaction child = (Transaction) it.next();
			child.suspend();
		}
		for (Iterator it = resources.iterator(); it.hasNext();) {
			IResource resource = (IResource) it.next();
			resource.suspend();
		}
	}

}
