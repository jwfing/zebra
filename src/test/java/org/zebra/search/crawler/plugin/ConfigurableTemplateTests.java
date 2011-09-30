package org.zebra.search.crawler.plugin;

import java.util.*;

import org.zebra.search.crawler.plugin.ConfigurableTemplate.RuleItem;

import junit.framework.TestCase;

public class ConfigurableTemplateTests extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTimeTemplate() {
		ConfigurableTemplate template = new ConfigurableTemplate();
		template.load("testdata/time-template");
		List<ConfigurableTemplate.RuleItem> result = template.getRules();
		System.out.println(result.size());
		for(ConfigurableTemplate.RuleItem item : result) {
			System.out.println("regUrl\t"+item.getRegUrl());
			System.out.println("tag \t"+item.getTag());
			System.out.println("beginPattern \t"+item.getBeginPattern());
			System.out.println("endPattern \t"+item.getEndPattern());
			Map<String, String> attrs = item.getAttrs();
			
			for (Map.Entry<String, String> attr : attrs.entrySet()) {
				System.out.println("attr \t\t" + attr.getKey());
				System.out.println("attr \t\t" + attr.getValue());
			}
		}
	}
}
