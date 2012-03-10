package org.zebra.search.crawler.plugin;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.Tag;
import org.htmlparser.util.ParserException;

import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.util.ProcessorUtil;

public class DocumentParser implements Processor {
    private final Logger logger = Logger.getLogger(DocumentParser.class);
    private static final String defaultEncoding = "GB2312";

    public boolean initialize() {
        return true;
    }

    public boolean destroy() {
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter");
            return false;
        }

        // TODO: we can filter some doc with specificed mime-types
        String content = doc.getContentString();
        if (null == content || content.isEmpty()) {
            logger.warn("failed to fetch doc, url=" + doc.getUrl());
            return false;
        }
        Page page = new Page(content, defaultEncoding);
        page.setBaseUrl(doc.getUrl());

        Lexer lexer = new Lexer(page);
        Parser parser = new Parser(lexer);
        try {
            NodeList nodeList = parser.parse(null);
            if (nodeList != null) {
                parseHeadBase(doc, nodeList);
                context.setVariable(ProcessorUtil.COMMON_PROP_NODELIST, nodeList);
                logger.debug("parser doc, url=" + doc.getUrl());
            }
        } catch (ParserException e) {
            logger.warn("failed to parse document. url=" + doc.getUrl() + ", cause="
                    + e.getMessage());
            return false;
        }

        return true;
    }

    private void parseHeadBase(CrawlDocument doc, NodeList node) {
        if (null == doc || null == node) {
            return;
        }
        NodeFilter[] headFilters = { new TagNameFilter("HEAD") };
        OrFilter orFilter = new OrFilter();
        orFilter.setPredicates(headFilters);
        NodeList headNodes = node.extractAllNodesThatMatch(orFilter, true);
        if (null != headNodes && headNodes.size() > 0) {
            NodeList headChildrens = headNodes.elementAt(0).getChildren();
            if (null == headChildrens) {
                return;
            }
            NodeFilter[] baseFilters = { new TagNameFilter("BASE") };
            OrFilter orFilter2 = new OrFilter();
            orFilter2.setPredicates(baseFilters);
            NodeList baseNodes = headChildrens.extractAllNodesThatMatch(orFilter2, true);
            if (null != baseNodes && baseNodes.size() > 0) {
                try {
                    for (NodeIterator i = baseNodes.elements(); i.hasMoreNodes();) {
                        Tag tag = (Tag) i.nextNode();
                        if (tag.isEndTag()) {
                            continue;
                        }
                        String base = tag.getAttribute("href");
                        if (null != base && !base.isEmpty()) {
                            logger.debug("BASENODE FOUND. docUrl=" + doc.getUrlInfo().getUrl()
                                    + ", base=" + base);
                            doc.addFeature("base", base);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    this.logger.warn("exception occurred in head/base parser. cause: "
                            + ex.getMessage());
                }
            }
        }
    }

}
