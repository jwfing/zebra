package org.zebra.silkworm.plugin.extractor;

import org.zebra.silkworm.plugin.ConfigurableTemplate;
import org.zebra.silkworm.plugin.ConfigurableTemplate.RuleItem;

public class PublishTimeExtractor extends SubNodeExtractor {
	public PublishTimeExtractor() {
		ConfigurableTemplate template = new ConfigurableTemplate();
		boolean result = template.load("./conf/time-template");
		setRules(template.getRules());
		if (!result) {
			System.out.println("warning: publisher-template is invalid!!");
		}
	}
}
