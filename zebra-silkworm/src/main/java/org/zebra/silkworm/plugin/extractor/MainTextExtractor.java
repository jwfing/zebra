package org.zebra.silkworm.plugin.extractor;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.htmlparser.Node;  
import org.htmlparser.NodeFilter;  
import org.htmlparser.Parser;  
import org.htmlparser.filters.NodeClassFilter;  
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;  
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;  
import org.htmlparser.util.ParserException;  
import org.htmlparser.visitors.HtmlPage;  

import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.silkworm.plugin.ConfigurableTemplate;

public class MainTextExtractor extends SubNodeExtractor{
	public MainTextExtractor() {
		ConfigurableTemplate template = new ConfigurableTemplate();
		boolean result = template.load("templates/maintext-template");
		setRules(template.getRules());
		if (!result) {
			System.out.println("warning: maintext-template is invalid!!");
		}
	}
}
