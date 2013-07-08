/**
 * 
 */
package de.intarsys.tools.reflect;

import java.util.Map;


/**
 * The relation is implemented in a {@link Map}.
 * 
 */
public class MapRelationHandler extends RelationHandlerAdapter {

	@Override
	public Object[] get(Object owner) {
		if (owner == null) {
			return new Object[0];
		}
		return ((Map) owner).entrySet().toArray();
	}

	@Override
	public Object insert(Object owner, Object value) {
		if (owner == null) {
			return null;
		}
		Map.Entry<String, String> entry = (Map.Entry<String, String>) value;
		((Map) owner).put(entry.getKey(), entry.getValue());
		// this is not truly the object inserted...
		return entry;
	}

	@Override
	public Object remove(Object owner, Object value) {
		if (owner == null) {
			return null;
		}
		Map.Entry<String, String> entry = (Map.Entry<String, String>) value;
		((Map) owner).remove(entry.getKey());
		return entry;
	}

	@Override
	public Object update(Object owner, Object value, Object newValue) {
		if (owner == null) {
			return null;
		}
		Map.Entry<String, String> entry = (Map.Entry<String, String>) value;
		Map.Entry<String, String> newEntry = (Map.Entry<String, String>) newValue;
		if (entry.getKey().equals(newEntry.getKey())) {
			entry.setValue(newEntry.getValue());
			return entry;
		} else {
			((Map) owner).remove(entry.getKey());
			((Map) owner).put(newEntry.getKey(), newEntry.getValue());
			return newEntry;
		}
	}
}