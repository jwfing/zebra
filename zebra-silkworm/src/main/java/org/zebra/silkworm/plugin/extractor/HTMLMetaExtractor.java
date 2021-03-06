package org.zebra.silkworm.plugin.extractor;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;

import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.utils.ProcessorUtil;

public class HTMLMetaExtractor {
    private final Logger logger = Logger.getLogger(HTMLMetaExtractor.class);
    private TitleExtractor titleExtractor = new TitleExtractor();

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
        String title = null;
        NodeFilter fileter = new TagNameFilter(ProcessorUtil.COMMON_PROP_TITLE);
        NodeList candidates = nodeList.extractAllNodesThatMatch(fileter, true);
        if (candidates != null && candidates.size() > 0) {
            title = candidates.elementAt(0).getFirstChild().getText();
        } else {
            title = "";
        }
        result.put(ProcessorUtil.COMMON_PROP_TITLE, title);
        fileter = new AndFilter(new TagNameFilter("meta"), new HasAttributeFilter("name",
                "description"));
        candidates = nodeList.extractAllNodesThatMatch(fileter, true);
        if (candidates != null && candidates.size() > 0) {
            Node node = candidates.elementAt(0);
            if (node instanceof TagNode) {
                String description = ((TagNode) node).getAttribute("content");
                result.put(ProcessorUtil.COMMON_PROP_DESCRIPTION, description);
            }
        } else {
            result.put(ProcessorUtil.COMMON_PROP_DESCRIPTION, "");
        }
        return result;
    }
}
