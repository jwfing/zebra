package org.zebra.search.crawler.plugin;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.util.UrlUtil;
import org.zebra.search.crawler.util.ProcessorUtil;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

public class LinkFollower implements Processor {
    private final Logger logger = Logger.getLogger(LinkFollower.class);
    private static final String HTTP_PROCOTOL = "http://";
    private static final String[] FORBIDDEN_TERMS = { "bbs", "forum", "download",
            "javascript", "copyright", "video", "schedule", "picture", "comment",
            "price", "about" };

    private static final String defaultEncoding = "GB2312";
    private static final String goodUrlType = "(html|shtml|htm|mht|shtm|aspx)";

    private boolean deepFollow = true;

    public boolean isDeepFollow() {
        return deepFollow;
    }

    public void setDeepFollow(boolean deepFollow) {
        this.deepFollow = deepFollow;
    }

    public boolean initialize() {
        logger.info("successful initialized " + LinkFollower.class.getName());
        return true;
    }

    public boolean destroy() {
        logger.info("successful destroied " + LinkFollower.class.getName());
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    private boolean processRSS(CrawlDocument doc, Context context) {
        byte[] content = doc.getContentBytes();
        List<UrlInfo> linkList = new ArrayList<UrlInfo>();
        try {
            SyndFeedInput input = new SyndFeedInput();
            // skip BOM char in prolog.
            int i = 0;
            while (content[i] != '<' && i < content.length) {
                i++;
            }
            if (i < content.length - 1) {
                String contentString = new String(content, i, content.length - i, "utf-8");
                contentString = contentString.trim();
                SyndFeed feed = input.build(new InputStreamReader(new ByteArrayInputStream(
                        contentString.getBytes())));
                List<SyndEntry> entries = feed.getEntries();
                for (SyndEntry entry : entries) {
                    UrlInfo urlInfo = new UrlInfo(entry.getLink());
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_SEEDURL, doc.getUrl());
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, "page");
                    linkList.add(urlInfo);
                }
            }
        } catch (Exception ex) {
            logger.warn("exception occurred. cause:{}", ex);
        }
        context.setVariable(ProcessorUtil.COMMON_PROP_OUTLINKS, linkList);
        logger.info("link follow. docUrl=" + doc.getUrl() + ", outlinks=" + linkList.size());
        return true;
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter");
            return false;
        }

        String contentType = doc.getFeature(ProcessorUtil.COMMON_PROP_CONTENTTYPE);
        if (contentType.contains("xml") || contentType.contains("rss")) {
            return processRSS(doc, context);
        }

        NodeList nodeList = (NodeList) context.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
        if (nodeList == null) {
            logger.debug("the node list is null");
            return true;
        }

        UrlInfo currentUrlInfo = doc.getUrlInfo();
        String base = (String) doc.getFeature(ProcessorUtil.COMMON_PROP_BASE);
        if (null == base || base.isEmpty()) {
            base = doc.getUrl();
        }
        URL baseUrl = UrlUtil.genURL(base);
        String origEncoding = defaultEncoding;
        String flag = (String)currentUrlInfo.getFeature(ProcessorUtil.COMMON_PROP_FLAG);
        boolean disableFollow = false;
        if (null != flag && flag.equalsIgnoreCase("page")) {
            Boolean disableFeature = (Boolean)currentUrlInfo.getFeature(ProcessorUtil.COMMON_PROP_DISABLEFOLLOW);
            if (null != disableFeature) {
                disableFollow = disableFeature.booleanValue();
            }
        }
        
        List<UrlInfo> linkList = new ArrayList<UrlInfo>();
        if (disableFollow) {
            context.setVariable(ProcessorUtil.COMMON_PROP_OUTLINKS, linkList);
            return true;
        }

        NodeList links = extractLinkNodes(nodeList);
        try {
            for (NodeIterator i = links.elements(); i.hasMoreNodes();) {
                Tag tag = (Tag) i.nextNode();
                if (tag.isEndTag()) {
                    continue;
                }

                UrlInfo urlInfo = getLinkFromTag(tag, baseUrl, origEncoding);
                if ((urlInfo == null) || (urlInfo.getUrl() == null)) {
                    continue;
                }
                if (urlInfo.getUrl().equalsIgnoreCase(currentUrlInfo.getUrl())) {
                    // url equals the one of seed
                    continue;
                }
                if (!isGoodType(urlInfo.getUrl())) {
                    continue;
                }
                urlInfo.addFeature(ProcessorUtil.COMMON_PROP_SEEDURL, currentUrlInfo.getUrl());
                urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, "page");
                if (null != flag && flag.equalsIgnoreCase("page")) {
                    // one level follow
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_DISABLEFOLLOW, new Boolean(true));
                }
                linkList.add(urlInfo);
            }
        } catch (Exception e) {
            this.logger.warn("exception occurred in linkFollow. cause: " + e.getMessage());
        }

        context.setVariable(ProcessorUtil.COMMON_PROP_OUTLINKS, linkList);
        logger.info("link follow. docUrl=" + doc.getUrl() + ", outlinks=" + linkList.size());

        return true;
    }

    protected boolean isGoodType(String uri) {
        if (null == uri) {
            return false;
        }
        if (!uri.startsWith(HTTP_PROCOTOL)) {
            return false;
        }
        for (String forbiddenTerm : FORBIDDEN_TERMS) {
            if (uri.indexOf(forbiddenTerm) != -1) {
                return false;
            }
        }
        String[] parts = uri.split("/");
        String last = parts[parts.length - 1];
        int lastDotIndex = last.lastIndexOf(".");
        if ((lastDotIndex <= 0)) {
            return true;
        }
        String ext = last.substring(lastDotIndex + 1);
        return ext.matches(goodUrlType);
    }

    private NodeList extractLinkNodes(NodeList nodeList) {
        NodeFilter[] linkFilters = { new TagNameFilter("A"), new TagNameFilter("AREA"),
                new TagNameFilter("FRAME"), new TagNameFilter("IFRAME"),
                new TagNameFilter("LAYER"), new TagNameFilter("ILAYER") };
        OrFilter orFilter = new OrFilter();
        orFilter.setPredicates(linkFilters);
        return nodeList.extractAllNodesThatMatch(orFilter, true);
    }

    private UrlInfo getLinkFromTag(Tag tag, URL parentUrl, String origEncoding) {
        String url = null;
        String tagName = tag.getTagName();

        if (tagName.equals("A")) {
            url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("href"));
        } else if (tagName.equals("AREA")) {
            url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("href"));
        } else if (tagName.equals("FRAME")) {
            url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("src"));
        } else if (tagName.equals("IFRAME")) {
            url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("src"));
        } else if (tagName.equals("LAYER")) {
            url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("src"));
        } else if (tagName.equals("ILAYER")) {
            url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("src"));
        }

        url = UrlUtil.getCanonicalURL(url);
        UrlInfo urlInfo = new UrlInfo(url);

        return urlInfo;
    }
}
