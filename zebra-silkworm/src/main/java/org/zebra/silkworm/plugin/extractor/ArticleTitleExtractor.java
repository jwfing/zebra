package org.zebra.silkworm.plugin.extractor;

import org.zebra.silkworm.plugin.ConfigurableTemplate;
import org.zebra.silkworm.plugin.ConfigurableTemplate.RuleItem;

public class ArticleTitleExtractor extends SubNodeExtractor {
	public ArticleTitleExtractor() {
		ConfigurableTemplate template = new ConfigurableTemplate();
		boolean result = template.load("./conf/title-template");
		setRules(template.getRules());
		if (!result) {
			System.out.println("warning: title-template is invalid!!");
		}
	}
}
