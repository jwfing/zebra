package org.zebra.common.flow.plugin;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.slf4j.LoggerFactory;
import org.zebra.common.*;
import org.zebra.common.flow.*;
import org.zebra.common.utils.ProcessorUtil;

public class DocumentParser implements Processor {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private static final String defaultEncoding = "utf-8";

    public boolean initialize() {
        return true;
    }

    public boolean destroy() {
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    private String getCharsetFromHtml(Document doc) {
        Elements elements = doc.select("meta[http-equiv=Content-Type]");
        if (null != elements && elements.size() > 0) {
            // <meta http-equiv="Content-Type"
            // content="text/html; charset=ISO-8859-1">
            String contentType = elements.first().attr("content");
            String result = "";
            String[] parts = contentType.split(";");
            for (String part: parts) {
                if (part.isEmpty()) {
                    continue;
                }
                part = part.toLowerCase();
                int pos = part.indexOf("charset=");
                if (pos > -1) {
                    result = part.substring(pos + "charset=".length());
                    break;
                }
            }
            if (!result.isEmpty()) {
                return result;
            }
        }
        elements = doc.select("meta[charset]");
        if (null == elements || elements.size() < 1) {
            return defaultEncoding;
        }
        String charset = elements.first().attr("charset");
        if (null == charset || charset.isEmpty()) {
            return defaultEncoding;
        } else {
            return charset;
        }
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter");
            return false;
        }
        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }

        String charset = defaultEncoding;
        try {
            Document jsoupDoc = Jsoup.parse(new ByteArrayInputStream(doc.getContentBytes()), null, "");
            charset = getCharsetFromHtml(jsoupDoc);
            Charset.forName(charset);
        } catch (Exception ex) {
            charset = defaultEncoding;
        }
        doc.addFeature(ProcessorUtil.COMMON_PROP_ENCODING, charset);
        try {
            Page page = new Page(new ByteArrayInputStream(doc.getContentBytes()), charset);
            page.setBaseUrl(doc.getUrl());

            Lexer lexer = new Lexer(page);
            Parser parser = new Parser(lexer);
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
        } catch (Exception ex) {
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
