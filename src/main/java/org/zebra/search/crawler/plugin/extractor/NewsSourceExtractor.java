package org.zebra.search.crawler.plugin.extractor;

import org.apache.log4j.Logger;
import org.htmlparser.util.NodeList;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

public class NewsSourceExtractor {
	private final Logger logger = Logger.getLogger(NewsSourceExtractor.class);
    public String extract(CrawlDocument doc, Context context) {
    	if (null == doc || null == context) {
    		return "";
    	}
		NodeList nodeList = (NodeList) context.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			logger.debug("the node list is null");
			return "";
		}

    	return "";
    }

}
