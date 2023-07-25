/*
 * Copyright (c) 2012, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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

import java.util.List;

import de.intarsys.tools.event.Event;

/**
 * A simple wizard like user interface implementation.
 * 
 * @param <M>
 * @param <C>
 * @param <T>
 */
public abstract class UIWizard<M, C, T> extends UIContainer<M, C, T> {

	private UIComponent<M, C, T> currentPage;

	protected UIWizard(IUIComponent parent) {
		super(parent);
		initPages();
	}

	protected void activatePage(UIComponent<M, C, T> page) {
		if (page == currentPage) {
			return;
		}
		if (currentPage != null) {
			removePage(currentPage);
		}
		currentPage = page;
		currentPage.createComponent((C) getComponent());
	}

	public UIComponent<M, C, T> getCurrentPage() {
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

	/**
	 * @param page
	 *            The wizard page to check
	 * @return
	 */
	protected boolean isVisible(UIComponent<M, C, T> page) {
		return true;
	}

	public void onNextPressed() {
		activatePage(selectNextPage());
	}

	public void onPreviousPressed() {
		activatePage(selectPreviousPage());
	}

	protected void removePage(UIComponent<M, C, T> currentPage2) {
	}

	protected UIComponent<M, C, T> selectInitialPage() {
		return selectNextPage(-1);
	}

	protected UIComponent<M, C, T> selectNextPage() {
		int index = getComponents().indexOf(currentPage);
		return selectNextPage(index);
	}

	protected UIComponent<M, C, T> selectNextPage(int index) {
		UIComponent<M, C, T> newPage = currentPage;
		index++;
		List<UIComponent<?, C, T>> temp = getComponents();
		while (index < temp.size()) {
			UIComponent<M, C, T> tempPage = (UIComponent<M, C, T>) temp.get(index);
			if (isVisible(tempPage)) {
				return tempPage;
			}
			index++;
		}
		return newPage;
	}

	protected UIComponent<M, C, T> selectPreviousPage() {
		int index = getComponents().indexOf(currentPage);
		return selectPreviousPage(index);
	}

	protected UIComponent<M, C, T> selectPreviousPage(int index) {
		UIComponent<M, C, T> newPage = currentPage;
		index--;
		List<UIComponent<?, C, T>> temp = getComponents();
		while (index >= 0) {
			UIComponent<M, C, T> tempPage = (UIComponent<M, C, T>) temp.get(index);
			if (isVisible(tempPage)) {
				return tempPage;
			}
			index--;
		}
		return newPage;
	}

	protected void setCurrentPage(UIComponent<M, C, T> currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	protected void updateView(Event e) {
		super.updateView(e);
		if (currentPage == null) {
			UIComponent<M, C, T> nextPage = selectInitialPage();
			activatePage(nextPage);
		}
	}
}
