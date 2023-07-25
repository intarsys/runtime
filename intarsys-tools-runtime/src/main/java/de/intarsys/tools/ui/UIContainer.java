package de.intarsys.tools.ui;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IElement;

/**
 * Abstraction of an {@link UIComponent} that contains other {@link UIComponent}
 * instances.
 * 
 * @param <M>
 * @param <C>
 * @param <T>
 */
public abstract class UIContainer<M, C, T> extends UIComponent<M, C, T> {

	private final List<UIComponent<?, C, T>> components = new ArrayList<>();

	protected UIContainer(IUIComponent<?, C, T> parent) {
		super(parent);
	}

	protected void addComponent(UIComponent<?, C, T> component) {
		components.add(component);
	}

	@Override
	protected void basicDispose() {
		for (UIComponent component : components) {
			component.dispose();
		}
		super.basicDispose();
	}

	@Override
	protected void basicSetValue(M value) {
		super.basicSetValue(value);
		for (UIComponent component : components) {
			if (component.isInheritValue()) {
				component.setValue(value);
			}
		}
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		for (UIComponent component : components) {
			(component).configure(element);
		}
	}

	public List<UIComponent<?, C, T>> getComponents() {
		return new ArrayList<>(components);
	}

}
