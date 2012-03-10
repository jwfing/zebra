package org.zebra.search.crawler.plugin.extractor;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

public class HTMLMetaExtractor {
    private final Logger logger = Logger.getLogger(HTMLMetaExtractor.class);

    public Map<String, String> extract(CrawlDocument doc, Context context) {
        Map<String, String> result = new HashMap<String, String>();
        if (null == doc || null == context) {
            return result;
        }
        NodeList nodeList = (NodeList) context.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
        if (nodeList == null) {
            logger.debug("the node list is null");
            return result;
        }
        NodeFilter fileter = new TagNameFilter(ProcessorUtil.COMMON_PROP_TITLE);
        NodeList candidates = nodeList.extractAllNodesThatMatch(fileter, true);
        if (candidates != null && candidates.size() > 0) {
            String title = candidates.elementAt(0).getFirstChild().getText();
            result.put(ProcessorUtil.COMMON_PROP_TITLE, title);
        } else {
            result.put(ProcessorUtil.COMMON_PROP_TITLE, "");
        }
        fileter = new TagNameFilter(ProcessorUtil.COMMON_PROP_DESCRIPTION);
        candidates = nodeList.extractAllNodesThatMatch(fileter, true);
        if (candidates != null && candidates.size() > 0) {
            String description = candidates.elementAt(0).getText();
            result.put(ProcessorUtil.COMMON_PROP_DESCRIPTION, description);
        } else {
            result.put(ProcessorUtil.COMMON_PROP_DESCRIPTION, "");
        }
        return result;
    }
}
