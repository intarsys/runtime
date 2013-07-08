/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.ui;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;

/**
 * A simple wizard like user interface implementation.
 * 
 * @param <M>
 * @param <C>
 * @param <T>
 */
abstract public class UIWizard<M, C, T> extends UIComponent<M, C, T> {

	private List<IUIComponent<M, C, T>> pages = new ArrayList<IUIComponent<M, C, T>>();

	private IUIComponent<M, C, T> currentPage;

	public UIWizard(IUIComponent parent) {
		super(parent);
		initPages();
	}

	protected void activatePage(IUIComponent<M, C, T> page) {
		if (page == currentPage) {
			return;
		}
		if (currentPage != null) {
			removePage(currentPage);
		}
		currentPage = page;
		currentPage.createComponent((C) getComponent());
	}

	protected void addPage(IUIComponent<M, C, T> page) {
		pages.add(page);
	}

	@Override
	public void configure(IElement element)
			throws ConfigurationException {
		super.configure(element);
		for (IUIComponent page : pages) {
			if (page instanceof IElementConfigurable) {
				((IElementConfigurable) page).configure(element);
			}
		}
	}

	@Override
	public void dispose() {
		for (IUIComponent page : pages) {
			page.dispose();
		}
		super.dispose();
	}

	public IUIComponent<M, C, T> getCurrentPage() {
		return currentPage;
	}

	protected void initPages() {
	}

	public boolean isNextEnabled() {
		return selectNextPage() != currentPage;
	}

	public boolean isPreviousEnabled() {
		return selectPreviousPage() != currentPage;
	}

	protected boolean isVisible(IUIComponent<M, C, T> page) {
		return true;
	}

	public void onNextPressed() {
		activatePage(selectNextPage());
	}

	public void onPreviousPressed() {
		activatePage(selectPreviousPage());
	}

	protected void removePage(IUIComponent<M, C, T> currentPage2) {
	}

	protected IUIComponent<M, C, T> selectInitialPage() {
		return selectNextPage(-1);
	}

	protected IUIComponent<M, C, T> selectNextPage() {
		int index = pages.indexOf(currentPage);
		return selectNextPage(index);
	}

	protected IUIComponent<M, C, T> selectNextPage(int index) {
		IUIComponent<M, C, T> newPage = currentPage;
		index++;
		while (index < pages.size()) {
			IUIComponent<M, C, T> tempPage = pages.get(index);
			if (isVisible(tempPage)) {
				return tempPage;
			}
			index++;
		}
		return newPage;
	}

	protected IUIComponent<M, C, T> selectPreviousPage() {
		int index = pages.indexOf(currentPage);
		return selectPreviousPage(index);
	}

	protected IUIComponent<M, C, T> selectPreviousPage(int index) {
		IUIComponent<M, C, T> newPage = currentPage;
		index--;
		while (index >= 0) {
			IUIComponent<M, C, T> tempPage = pages.get(index);
			if (isVisible(tempPage)) {
				return tempPage;
			}
			index--;
		}
		return newPage;
	}

	protected void setCurrentPage(IUIComponent<M, C, T> currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	protected void updateView(Event e) {
		super.updateView(e);
		if (currentPage == null) {
			IUIComponent<M, C, T> nextPage = selectInitialPage();
			activatePage(nextPage);
		}
	}
}
