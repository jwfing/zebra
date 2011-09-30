package org.zebra.search.crawler.plugin.extractor;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Node;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

public class TitleExtractor {
	private final Logger logger = Logger.getLogger(TitleExtractor.class);

    public String extract(CrawlDocument doc, Context context) {
    	if (null == doc || null == context) {
    		return "";
    	}
		NodeList nodeList = (NodeList) context.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			logger.debug("the node list is null");
			return "";
		}
		NodeFilter[] linkFilters = { new TagNameFilter("H1"),
				new TagNameFilter("h1") };
		OrFilter orFilter = new OrFilter();
		orFilter.setPredicates(linkFilters);
		NodeList candidates = nodeList.extractAllNodesThatMatch(orFilter, true);
		if (null == candidates || candidates.size() < 1) {
			return "";
		}
		for (int i = 0; i < candidates.size(); i++) {
			Node node = candidates.elementAt(i);
			if (null != node && !node.toPlainTextString().isEmpty()) {
				return node.toPlainTextString();
			}
		}

    	return "";
    }

}
