package de.intarsys.tools.tag;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.TagResolver;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reader.ReaderTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;
import de.intarsys.tools.zones.Zone;

/**
 * A tool class for implementing a generic String based tagging feature.
 */
public class TagTools {

	private static final ILogger Log = LogTools.getLogger(TagTools.class);

	public static final String RESOLVER_NS_TAGS = "tags";

	private static final Attribute ATTR_TAGS = new Attribute(RESOLVER_NS_TAGS); // $NON-NLS-1$

	private static final List<Tag> EMPTY = Collections.emptyList();

	private static final Map<Object, List<Tag>> TAGS_MAP = new WeakHashMap<>();

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
	public static void addTag(Object object, String key, String value) {
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
	public static void addTag(Object object, Tag tag) {
		if (tag == null) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		tags.add(tag);
	}

	public static void addTags(Object object, List<Tag> pTags) {
		if (pTags == null) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		tags.addAll(pTags);
	}

	public static void addTags(Object object, Map<String, String> pTags) {
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
	public static void addTags(Object object, String tagString) {
		if (StringTools.isEmpty(tagString)) {
			return;
		}
		List<Tag> tags = basicGetTags(object);
		Reader r = new StringReader(tagString);
		try {
			while (true) {
				Map.Entry<String, String> entry = ReaderTools.readEntry(r, ';');
				if (entry == null) {
					break;
				}
				if (entry.getKey() != null) {
					tags.add(new Tag(entry.getKey(), entry.getValue()));
				}
			}
		} catch (IOException e) {
			// no io exception from string reader
		}
	}

	public static void addTags(Object object, Tag... pTags) {
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
			List<Tag> tags = (List<Tag>) ((IAttributeSupport) object).getAttribute(ATTR_TAGS);
			if (tags == null) {
				tags = new ArrayList<>();
				((IAttributeSupport) object).setAttribute(ATTR_TAGS, tags);
			}
			return tags;
		} else {
			List<Tag> tags = TAGS_MAP.computeIfAbsent(object, key -> new ArrayList<>());
			return tags;
		}
	}

	protected static List<Tag> basicLookupTags(Object object) {
		if (object instanceof ITagSupport) {
			return ((ITagSupport) object).getTags();
		} else if (object instanceof IAttributeSupport) {
			List<Tag> tags = (List<Tag>) ((IAttributeSupport) object).getAttribute(ATTR_TAGS);
			return tags;
		} else {
			List<Tag> tags = TAGS_MAP.get(object);
			return tags;
		}
	}

	/**
	 * Configure tags for target.
	 * 
	 * @param target
	 * @param pElement
	 */
	public static void configureTags(Object target, IElement pElement) {
		IElement elTags = pElement.element(RESOLVER_NS_TAGS);
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
	 * Create an {@link IStringEvaluator} that can resolve expressions from the target's tags. The tags are detached
	 * from the target.
	 * 
	 * @param target
	 * @return
	 */
	public static IStringEvaluator createDetachedResolver(Object target) {
		return new TagResolver(new TagList(basicLookupTags(target)));
	}

	/**
	 * Create an {@link IStringEvaluator} that can resolve expressions from the target's tags. The tags are detached
	 * from the target, the result is wrapped in the resolver namespace "tags".
	 * 
	 * @param target
	 * @return
	 */
	public static IStringEvaluator createDetachedResolverTags(Object target) {
		return MapResolver.createStrict(RESOLVER_NS_TAGS, createDetachedResolver(target));
	}

	/**
	 * Create an {@link IStringEvaluator} that can resolve expressions from the target's tags.
	 * 
	 * @param target
	 * @return
	 */
	public static IStringEvaluator createResolver(Object target) {
		return new TagResolver(target);
	}

	/**
	 * Create an {@link IStringEvaluator} that can resolve expressions from the target's tags wrapped in the "tags"
	 * namespace.
	 * 
	 * @param target
	 * @return
	 */
	public static IStringEvaluator createResolverTags(Object target) {
		return MapResolver.createStrict(RESOLVER_NS_TAGS, createResolver(target));
	}

	/**
	 * Expand args in-place using the tags associated with target.
	 * 
	 * This expands expressions in the "tags" namespace only.
	 * 
	 * @param target
	 * @param args
	 */
	public static void expandArgs(Object target, IArgs args) {
		TagResolver tagResolver = new TagResolver(target);
		MapResolver mapResolver = MapResolver.createStrict(RESOLVER_NS_TAGS, tagResolver);
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorateLenient(mapResolver);
		ArgTools.expandDeep(args, evaluator);
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
	public static Tag getTag(Object object, String key) {
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
	public static List<Tag> getTagList(Object object) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return EMPTY;
		}
		return new ArrayList<>(tags);
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
	public static List<Tag> getTagList(Object object, String key) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return EMPTY;
		}
		List<Tag> result = new ArrayList<>();
		for (Iterator it = tags.iterator(); it.hasNext();) {
			Tag tag = (Tag) it.next();
			if (key.equals(tag.getKey())) {
				result.add(tag);
			}
		}
		return result;
	}

	/**
	 * Get a meta value for the first tag matching "key".
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @param metaKey
	 *            The meta key value that is searched
	 * @param defaultValue
	 *            The default value to use
	 */
	public static Object getTagMeta(Object object, String key, String metaKey, Object defaultValue) {
		Tag tag = getTag(object, key);
		return getTagMeta(tag, metaKey, defaultValue);
	}

	/**
	 * Get a meta value for tag.
	 * 
	 * @param tag
	 *            The tag
	 * @param metaKey
	 *            The meta key value that is searched
	 * @param defaultValue
	 *            The default value to use
	 */
	public static Object getTagMeta(Tag tag, String metaKey, Object defaultValue) {
		if (tag == null) {
			return defaultValue;
		}
		Object metaValue = tag.getProperty(metaKey);
		if (metaValue == null) {
			return defaultValue;
		}
		return metaValue;
	}

	/**
	 * Get the value of the first tag matching "key".
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value that is searched
	 * @param defaultValue
	 * @return The first matching tag or <code>null</code>
	 */
	public static String getTagValue(Object object, String key, String defaultValue) {
		Tag tag = getTag(object, key);
		if (tag == null) {
			return defaultValue;
		}
		return tag.getValue();
	}

	/**
	 * Get all tag values for object.
	 * 
	 * @param object
	 *            The tagged object
	 * @return Get all tags values for object.
	 */
	public static List<String> getTagValues(Object object) {
		return getTagList(object).stream().map(tag -> tag.getValue()).toList();
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
	public static List<String> getTagValues(Object object, String key) {
		return getTagList(object, key).stream().map(tag -> tag.getValue()).toList();
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
	public static boolean hasTag(Object object, String key) {
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
	public static boolean hasTags(Object object) {
		List<Tag> tags = basicLookupTags(object);
		if (tags == null) {
			return false;
		}
		return !tags.isEmpty();
	}

	/**
	 * Check if a given object has a specific set of tags.
	 * 
	 * @param object
	 *            The tagged object.
	 * @param tags
	 *            The list of tags required for a positive result.
	 * @return True, if the object contains all tags, else otherwise.
	 */
	public static boolean hasTags(Object object, List<Tag> tags) {
		for (Tag tag : tags) {
			boolean tagFound = false;
			List<Tag> objectTags = TagTools.getTagList(object, tag.getKey());
			for (Tag objectTag : objectTags) {
				if (tag.equals(objectTag)) {
					tagFound = true;
					break;
				}
			}
			if (!tagFound) {
				return false;
			}
		}
		return true;
	}

	public static Tag parseTag(String definition) throws IOException {
		String key = "";
		String value = "";
		Reader r = new StringReader(definition);
		Map.Entry<String, String> entry = ReaderTools.readEntry(r, ';');
		if (entry == null) {
			return null;
		}
		if (entry.getKey() == null) {
			return null;
		}
		key = entry.getKey();
		if (entry.getValue() != null) {
			value = entry.getValue();
		}
		return new Tag(key, value);
	}

	public static List<Tag> parseTags(String definition) throws IOException {
		List<Tag> result = new ArrayList<>();
		Reader r = new StringReader(definition);
		while (true) {
			Map.Entry<String, String> entry = ReaderTools.readEntry(r, ';');
			if (entry == null) {
				break;
			}
			if (entry.getKey() != null) {
				result.add(new Tag(entry.getKey(), entry.getValue()));
			}
		}
		return result;
	}

	/**
	 * Remove all tags for key.
	 * 
	 * @param object
	 *            The tagged object
	 * @param key
	 *            The key value for the tags to be removed
	 */
	public static void removeTags(Object object, String key) {
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
	public static void setTag(Object object, String key, String value) {
		removeTags(object, key);
		List<Tag> tags = basicGetTags(object);
		tags.add(new Tag(key, value));
	}

	public static IArgs toArgs(List<?> tags, String prefix) {
		IArgs resultArgs = Args.create();
		for (Object tagObject : tags) {
			Tag tag = null;
			if (tagObject instanceof Tag) {
				tag = (Tag) tagObject;
			} else if (tagObject instanceof String) {
				try {
					tag = TagTools.parseTag((String) tagObject);
				} catch (IOException e) {
					Log.warn("tag cannot be parsed {}", tagObject);
					continue;
				}
			}
			if (tag == null) {
				continue;
			}
			String key = tag.getKey();
			String value = tag.getValue();
			// check if tag key starts with prefix
			if (key.startsWith(prefix)) {
				ArgTools.putPath(resultArgs, key.substring(prefix.length()), value);
			}
		}
		return resultArgs;
	}

	public static IArgs toArgs(Object target, String prefix) {
		List<?> tags = TagTools.getTagList(target == null ? Zone.getCurrent() : target);
		return toArgs(tags, prefix);
	}

	private TagTools() {
	}

}
