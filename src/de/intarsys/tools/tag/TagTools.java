package de.intarsys.tools.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.infoset.IElement;

/**
 * A tool class for implementing a generic String based tagging feature.
 */
public class TagTools {

	static private final Attribute ATTR_TAGS = new Attribute("tags"); //$NON-NLS-1$

	static private final Tag[] EMPTY = new Tag[0];

	private static final Map<Object, List<Tag>> tagsMap = new WeakHashMap<Object, List<Tag>>();

	/**
	 * Add a tag to an object.
	 * 
	 * @param object
	 *            The object to be tagged
	 * @param key
	 *            The tag key
	 * @param value
	 *            The tag value
	 */
	static public void addTag(Object object, String key, String value) {
		List<Tag> tags = basicGetTags(object);
		tags.add(new Tag(key, value));
	}

	/**
	 * Add a tag to an object.
	 * 
	 * @param object
	 *            The object to be tagged
	 * @param tag
	 *            The new tag
	 */
	static public void addTag(Object object, Tag tag) {
		if (tag == null) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		tags.add(tag);
	}

	static public void addTags(Object object, List<Tag> pTags) {
		if (pTags == null) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		tags.addAll(pTags);
	}

	static public void addTags(Object object, Map<String, String> pTags) {
		if (pTags == null) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		for (Map.Entry<String, String> entry : pTags.entrySet()) {
			tags.add(new Tag(entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * Add a tag string to object. The string is parsed to enumerate all new
	 * tags to add.
	 * 
	 * @param object
	 *            The object to be tagged
	 * @param tagString
	 *            A tag string of the form "key1=value1;key2=value2..."
	 */
	static public void addTags(Object object, String tagString) {
		List<Tag> tags = basicGetTags(object);
		tags.addAll(Arrays.asList(Tag.create(tagString)));
	}

	static public void addTags(Object object, Tag[] pTags) {
		if (pTags == null) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		tags.addAll(Arrays.asList(pTags));
	}

	protected static List<Tag> basicGetTags(Object object) {
		if (object instanceof ITagSupport) {
			return ((ITagSupport) object).getTags();
		} else if (object instanceof IAttributeSupport) {
			List<Tag> tags = (List<Tag>) ((IAttributeSupport) object)
					.getAttribute(ATTR_TAGS);
			if (tags == null) {
				tags = new ArrayList<Tag>();
				((IAttributeSupport) object).setAttribute(ATTR_TAGS, tags);
			}
			return tags;
		} else {
			List<Tag> tags = tagsMap.get(object);
			if (tags == null) {
				tags = new ArrayList<Tag>();
				tagsMap.put(object, tags);
			}
			return tags;
		}
	}

	protected static List<Tag> basicLookupTags(Object object) {
		if (object instanceof ITagSupport) {
			return ((ITagSupport) object).getTags();
		} else if (object instanceof IAttributeSupport) {
			List<Tag> tags = (List<Tag>) ((IAttributeSupport) object)
					.getAttribute(ATTR_TAGS);
			return tags;
		} else {
			List<Tag> tags = tagsMap.get(object);
			return tags;
		}
	}

	/**
	 * Configure tags for target.
	 * 
	 * @param target
	 * @param pElement
	 */
	static public void configureTags(Object target, IElement pElement) {
		IElement elTags = pElement.element("tags");
		if (elTags != null) {
			Iterator<IElement> itTags = elTags.elementIterator("tag");
			while (itTags.hasNext()) {
				IElement elTag = itTags.next();
				String key = elTag.attributeValue("key", "");
				String value = elTag.attributeValue("value", "true");
				addTag(target, key, value);
			}
		}
	}

	/**
	 * Get the first tag matching "key".
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @return The first matching tag or <code>null</code>
	 */
	static public Tag getTag(Object object, String key) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return null;
		}
		for (Iterator it = tags.iterator(); it.hasNext();) {
			Tag tag = (Tag) it.next();
			if (key.equals(tag.getKey())) {
				return tag;
			}
		}
		return null;
	}

	/**
	 * Get all tags for object.
	 * 
	 * @param object
	 *            The tagged object
	 * @return Get all tags for object.
	 */
	static public Tag[] getTags(Object object) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return EMPTY;
		}
		return tags.toArray(new Tag[tags.size()]);
	}

	/**
	 * Get all tags matching "key".
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @return All matching tags
	 */
	static public Tag[] getTags(Object object, String key) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return EMPTY;
		}
		List<Tag> result = new ArrayList<Tag>();
		for (Iterator it = tags.iterator(); it.hasNext();) {
			Tag tag = (Tag) it.next();
			if (key.equals(tag.getKey())) {
				result.add(tag);
			}
		}
		return result.toArray(new Tag[result.size()]);
	}

	/**
	 * Get the value of the first tag matching "key".
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @param defaultValue
	 *            TODO
	 * @return The first matching tag or <code>null</code>
	 */
	static public String getTagValue(Object object, String key,
			String defaultValue) {
		Tag tag = getTag(object, key);
		if (tag == null) {
			return defaultValue;
		}
		return tag.getValue();
	}

	/**
	 * Get all tags values for object.
	 * 
	 * @param object
	 *            The tagged object
	 * @return Get all tags values for object.
	 */
	static public String[] getTagValues(Object object) {
		Tag[] tags = getTags(object);
		String[] values = new String[tags.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = tags[i].getValue();
		}
		return values;
	}

	/**
	 * Get all tag values for tags matching "key".
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @return all tag values for tags matching "key".
	 */
	static public String[] getTagValues(Object object, String key) {
		Tag[] tags = getTags(object, key);
		String[] values = new String[tags.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = tags[i].getValue();
		}
		return values;
	}

	/**
	 * <code>true</code> if object is tagged with "key"
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @return <code>true</code> if object is tagged with "key"
	 */
	static public boolean hasTag(Object object, String key) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return false;
		}
		for (Iterator it = tags.iterator(); it.hasNext();) {
			Tag tag = (Tag) it.next();
			if (key.equals(tag.getKey())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if object has tags
	 * 
	 * @param object
	 *            The tagged object
	 * @return <code>true</code> if object has tags
	 */
	static public boolean hasTags(Object object) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return false;
		}
		return !tags.isEmpty();
	}

	/**
	 * Remove all tags for key.
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value for the tags to be removed
	 */
	static public void removeTags(Object object, String key) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return;
		}
		for (Iterator it = tags.iterator(); it.hasNext();) {
			Tag tag = (Tag) it.next();
			if (key.equals(tag.getKey())) {
				it.remove();
			}
		}
	}

	/**
	 * Replace the previous association of tags for key in object with value.
	 * 
	 * @param object
	 *            The tagged object.
	 * @param key
	 *            The key for the tag to be replaced
	 * @param value
	 *            The new value
	 */
	static public void setTag(Object object, String key, String value) {
		removeTags(object, key);
		List<Tag> tags = basicGetTags(object);
		tags.add(new Tag(key, value));
	}

}
