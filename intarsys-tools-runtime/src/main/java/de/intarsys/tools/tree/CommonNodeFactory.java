package de.intarsys.tools.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import de.intarsys.tools.attribute.Attribute;

/**
 * The common implementation of a {@link CommonNode} factory.
 * <p>
 * This implementation ensures uniqueness of an object->node association within
 * the parent node. This means within a given parent context, the node returned
 * for a given object is always the same.
 * <p>
 * While all factories have their own way of creating a node based on the POJO,
 * they all share the ability to "fine tune" the node creation in a certain
 * context (role).
 * <p>
 * For fine tuning node creation, just register some other node factories using
 * "registerFactory". Upon creation of a node for this specific role, all
 * registered factories are searched if there is a more specific one for the
 * creation based on the objects's class.
 * 
 * @param <N>
 */
public abstract class CommonNodeFactory<N extends CommonNode> {

	private Map<Class<?>, CommonNodeFactory<?>> children;

	private final Map<Object, CommonNode> nodeMap = new WeakHashMap<>();

	private final Attribute attrNodeMap = new Attribute("nodeMap");

	protected CommonNodeFactory() {
		super();
	}

	public abstract N createNode(CommonNode parent, Object object);

	public synchronized CommonNodeFactory<?> lookupFactory(Object object) {
		if (children == null) {
			return this;
		}
		Class<?> clazz = object.getClass();
		while (clazz != null) {
			CommonNodeFactory<?> factory = children.get(clazz);
			if (factory != null) {
				return factory;
			}
			clazz = clazz.getSuperclass();
		}
		return this;
	}

	public synchronized CommonNode lookupNode(CommonNode parent, Object object) {
		if (parent == null) {
			return nodeMap.get(object);
		}
		// we make a lookup per role (attrNodeMap is instance scope)
		// do not move this to CommonNode
		Map<Object, CommonNode> tempNodeMap = (Map<Object, CommonNode>) parent.getAttribute(attrNodeMap);
		if (tempNodeMap == null) {
			return null;
		}
		return tempNodeMap.get(object);
	}

	public synchronized void registerFactory(Class<?> clazz, CommonNodeFactory<?> factory) {
		if (children == null) {
			children = new HashMap<>();
		}
		children.put(clazz, factory);
	}

	public synchronized void registerNode(CommonNode parent, CommonNode node) {
		if (parent == null) {
			nodeMap.put(node.getObject(), node);
		} else {
			// we make a registration per role (attrNodeMap is instance scope)
			// do not move this to CommonNode
			Map<Object, CommonNode> tempNodeMap = (Map<Object, CommonNode>) parent.getAttribute(attrNodeMap);
			if (tempNodeMap == null) {
				tempNodeMap = new WeakHashMap<>();
				parent.setAttribute(attrNodeMap, tempNodeMap);
			}
			tempNodeMap.put(node.getObject(), node);
		}
	}

}
