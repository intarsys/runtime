package de.intarsys.tools.expression;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class TestTemplateEvaluator extends TestCase {

	public void testTranslate() {
		String template;
		String translated;
		Map<String, String> map;
		//
		map = new HashMap<String, String>();
		template = "";
		translated = TemplateEvaluator.translate(template, map);
		assertTrue("".equals(translated));
		//
		map = new HashMap<String, String>();
		template = "foo";
		translated = TemplateEvaluator.translate(template, map);
		assertTrue("foo".equals(translated));
		//
		map = new HashMap<String, String>();
		template = "${foo}";
		translated = TemplateEvaluator.translate(template, map);
		assertTrue("${foo}".equals(translated));
		//
		map = new HashMap<String, String>();
		map.put("foo", "bar");
		template = "${foo}";
		translated = TemplateEvaluator.translate(template, map);
		assertTrue("${bar}".equals(translated));
		//
		map = new HashMap<String, String>();
		map.put("foo", "bar");
		template = "${foo} ${gnu}";
		translated = TemplateEvaluator.translate(template, map);
		assertTrue("${bar} ${gnu}".equals(translated));
		//
		map = new HashMap<String, String>();
		map.put("foo", "bar");
		template = "${foo:a} ${gnu:b}";
		translated = TemplateEvaluator.translate(template, map);
		assertTrue("${bar:a} ${gnu:b}".equals(translated));
	}

}
