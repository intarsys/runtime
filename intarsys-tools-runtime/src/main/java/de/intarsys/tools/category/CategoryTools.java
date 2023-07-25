package de.intarsys.tools.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import de.intarsys.tools.presentation.IPresentationHandler;
import de.intarsys.tools.presentation.PresentationHandler;

public final class CategoryTools {

	public static final ICategory ALL = new GenericCategory();

	public static List<ICategory> getCategories(List values, Predicate accept) {
		List<ICategory> categories = new ArrayList<>();
		for (Object object : values) {
			if (accept != null && !accept.test(object)) {
				continue;
			}
			ICategory tempCategory;
			tempCategory = getCategory(object);
			if (!categories.contains(tempCategory)) {
				categories.add(tempCategory);
			}
		}
		return categories;
	}

	public static List<ICategory> getCategoriesSorted(List values, Predicate accept) {
		List<ICategory> categories = getCategories(values, accept);
		final IPresentationHandler provider = new PresentationHandler() {
			@Override
			protected String basicGetLabel(Object object) {
				return ((ICategory) object).getId();
			}
		};
		Collections.sort(categories, new Comparator<ICategory>() {
			@Override
			public int compare(ICategory o1, ICategory o2) {
				return provider.getLabel(o1).compareTo(provider.getLabel(o2));
			}
		});
		return categories;
	}

	public static ICategory getCategory(Object object) {
		ICategory tempCategory;
		if (object instanceof ICategorySupport) {
			tempCategory = ((ICategorySupport) object).getCategory();
			if (tempCategory == null) {
				tempCategory = CategoryRegistry.get().lookupCategory(ICategory.OTHER);
			}
		} else {
			tempCategory = CategoryRegistry.get().lookupCategory(ICategory.OTHER);
		}
		return tempCategory;
	}

	public static Map<ICategory, List> getCategoryMap(List values, Predicate accept) {
		Map<ICategory, List> result = new HashMap();
		for (Object object : values) {
			if (accept != null && !accept.test(object)) {
				continue;
			}
			ICategory category = CategoryTools.getCategory(object);
			List elements = result.computeIfAbsent(category, (ignore) -> new ArrayList());
			elements.add(object);
		}
		return result;
	}

	public static List getValuesMatching(List pValues, Predicate accept, ICategory category) {
		List objects = new ArrayList();
		if (category != null) {
			for (Object object : pValues) {
				if (accept != null && !accept.test(object)) {
					continue;
				}
				ICategory tempCategory = getCategory(object);
				if (category == ALL || category.equals(tempCategory)) {
					objects.add(object);
				}
			}
		}
		return objects;
	}

	private CategoryTools() {
	}

}
