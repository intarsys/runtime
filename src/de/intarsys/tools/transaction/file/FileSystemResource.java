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
package de.intarsys.tools.transaction.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.transaction.CommonResource;
import de.intarsys.tools.transaction.IResource;
import de.intarsys.tools.transaction.ResourceException;

/**
 * This {@link IResource} manages changes on the file system.
 * 
 */
public class FileSystemResource extends CommonResource {

	private List<IFunctor> rollbackActions = new ArrayList<IFunctor>();

	private List<IFunctor> commitActions = new ArrayList<IFunctor>();

	protected FileSystemResource(FileSystemResourceType type,
			FileSystemResource parent) {
		super(type, parent);
	}

	@Override
	public void commit() throws ResourceException {
		List<IFunctor> tempUndos = new ArrayList<IFunctor>(commitActions);
		for (IFunctor action : tempUndos) {
			IFunctorCall call = new FunctorCall(this, Args.create());
			try {
				action.perform(call);
			} catch (FunctorInvocationException e) {
				throw new ResourceException(e);
			}
		}
		commitActions.clear();
		rollbackActions.clear();
	}

	public void onCommit(IFunctor action) {
		if (!isActive()) {
			return;
		}
		commitActions.add(action);
	}

	public void onRollback(IFunctor action) {
		if (!isActive()) {
			return;
		}
		rollbackActions.add(action);
	}

	public void onRollbackDelete(final File file) {
		if (file == null || !isActive()) {
			return;
		}
		onRollback(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call)
					throws FunctorInvocationException {
				file.delete();
				return null;
			}
		});
	}

	public void onRollbackMove(final File source, final File destination) {
		if (source == null || destination == null || source.equals(destination)
				|| !isActive()) {
			return;
		}
		onRollback(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call)
					throws FunctorInvocationException {
				try {
					FileTools.copyFile(source, destination);
				} catch (IOException e) {
					throw new FunctorInvocationException(e);
				}
				source.delete();
				return null;
			}
		});
	}

	public void recordChange(final File file) throws IOException {
		if (file == null || !isActive()) {
			return;
		}
		final File tempFile = FileTools.createTempFile(file);
		FileTools.copyBinaryFile(file, tempFile);
		onRollback(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call)
					throws FunctorInvocationException {
				try {
					FileTools.copyBinaryFile(tempFile, file);
					tempFile.delete();
				} catch (IOException e) {
					throw new FunctorInvocationException(e);
				}
				return null;
			}
		});
		onCommit(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call)
					throws FunctorInvocationException {
				tempFile.delete();
				return null;
			}
		});
	}

	@Override
	public void rollback() throws ResourceException {
		List<IFunctor> tempUndos = new ArrayList<IFunctor>(rollbackActions);
		Collections.reverse(tempUndos);
		for (IFunctor action : tempUndos) {
			IFunctorCall call = new FunctorCall(this, Args.create());
			try {
				action.perform(call);
			} catch (FunctorInvocationException e) {
				throw new ResourceException(e);
			}
		}
		rollbackActions.clear();
		commitActions.clear();
	}

}
