package de.intarsys.tools.enumeration;

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;

public class MapFromEnumItemConverter implements IConverter<EnumItem, Map<String, ?>> {

	@Override
	public Map<String, ?> convert(EnumItem source) throws ConversionException {
		Map<String, Object> map = new HashMap<>();
		map.put("id", source.getId());
		return map;
	}

	@Override
	public Class<? extends EnumItem> getSourceType() {
		return EnumItem.class;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Map> getTargetType() {
		return Map.class;
	}
}
