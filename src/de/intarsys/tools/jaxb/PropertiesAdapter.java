package de.intarsys.tools.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PropertiesAdapter extends
		XmlAdapter<Properties, Map<String, String>> {

	@Override
	public Properties marshal(Map<String, String> map) throws Exception {
		Properties properties = new Properties();
		return properties;
	}

	@Override
	public Map<String, String> unmarshal(Properties properties)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		for (Property property : properties.getProperties()) {
			map.put(property.getName(), property.getValue());
		}
		return map;
	}

}
