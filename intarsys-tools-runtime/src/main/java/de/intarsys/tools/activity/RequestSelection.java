package de.intarsys.tools.activity;

import java.util.List;

import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.presentation.IPresentationHandler;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.valueholder.ObjectHolder;

public class RequestSelection<R, P extends IActivity<?>> extends Requester<R, P> {

	private static IValueHolder<Boolean> ShowAdvanced = new ObjectHolder<>(false);

	public static <T, P extends IActivity<?>> RequestSelection<T, P> createSelection(P parent, IMessage title,
			List<T> values, T defaultValue) {
		RequestSelection result = new RequestSelection(parent);
		result.setTitle(title);
		result.setValues(values);
		result.setInitialSelection(defaultValue);
		return result;
	}

	public static <T, P extends IActivity<?>> RequestSelection<T, P> createSelection(P parent, IMessage title,
			List<T> values, T defaultValue, IMessage toggleMessage, boolean toggleValue) {
		RequestSelection result = new RequestSelection(parent);
		result.setTitle(title);
		result.setToggleMessage(toggleMessage);
		result.setToggleValue(toggleValue);
		result.setValues(values);
		result.setInitialSelection(defaultValue);
		return result;
	}

	public static IValueHolder<Boolean> getShowAdvanced() {
		return ShowAdvanced;
	}

	public static boolean isShowAdvanced() {
		return ShowAdvanced.get();
	}

	public static void setShowAdvanced(IValueHolder<Boolean> pShowAdvanced) {
		ShowAdvanced = pShowAdvanced;
	}

	private R initialSelection;

	private R selection;

	private List<R> values;

	private boolean showCategories;

	private IPresentationHandler presentationProvider;

	public RequestSelection(P parent) {
		super(parent);
	}

	@Override
	protected R getDefaultResult() {
		return getSelection();
	}

	public R getInitialSelection() {
		return initialSelection;
	}

	public IPresentationHandler getPresentationProvider() {
		return presentationProvider;
	}

	public R getSelection() {
		return selection;
	}

	public List<R> getValues() {
		return values;
	}

	public boolean isShowCategories() {
		return showCategories;
	}

	public void setInitialSelection(R initialSelection) {
		this.initialSelection = initialSelection;
	}

	public void setPresentationProvider(IPresentationHandler presentationProvider) {
		this.presentationProvider = presentationProvider;
	}

	public void setSelection(R selection) {
		this.selection = selection;
	}

	public void setShowCategories(boolean showCategories) {
		this.showCategories = showCategories;
	}

	public void setValues(List<R> values) {
		this.values = values;
	}
}
