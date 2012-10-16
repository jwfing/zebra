package org.zebra.silkworm.plugin;

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

import org.zebra.common.*;
import org.zebra.common.flow.*;
import org.zebra.common.utils.*;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

public class NewsLinkFollower implements Processor {
    private final Logger logger = Logger.getLogger(NewsLinkFollower.class);
    // private static final String PREFIX_5C22 = "%5c%22";
    // private static final int PREFIX_5C22_LEN = PREFIX_5C22.length();
    // private static final String SUFFIX_PLUS = "+";
    // private static final String SUFFIX_MINUS = "-";
    private static final String HTTP_PROCOTOL = "http://";
    private static final String[] FORBIDDEN_TERMS = { "blog", "thread", "bbs", "forum", "download",
            "javascript", "books", "copyright", "video", "music", "schedule", "picture", "comment",
            "price", "about", "contact", "privacy", "forward", "email", "print"};
    private static final String[] OPTIONAL_TERMS = { "list", "index" };

    private static final String defaultEncoding = "GB2312";
    private static final String goodUrlType = "(html|shtml|htm|mht|shtm|aspx)";

    private boolean rmSidFromUrl = true;
    private String sidPattern = "&sid=\\w+$|\\?sid=\\w+$";
    private boolean deepFollow = false;

    public boolean isDeepFollow() {
        return deepFollow;
    }

    public void setDeepFollow(boolean deepFollow) {
        this.deepFollow = deepFollow;
    }

    public boolean initialize() {
        logger.info("successful initialized " + NewsLinkFollower.class.getName());
        return true;
    }

    public boolean destroy() {
        logger.info("successful destroied " + NewsLinkFollower.class.getName());
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
                SyndFeed feed = input.build(new InputStreamReader(
                        new ByteArrayInputStream(contentString.getBytes())));
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
            base = base.substring(0, base.lastIndexOf('/'));
        }
        URL baseUrl = UrlUtil.genURL(base);
        String origEncoding = defaultEncoding;

        NodeList links = extractLinkNodes(nodeList);
        List<UrlInfo> linkList = new ArrayList<UrlInfo>();
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
                // add by fengjw, delete urls which don't start with http://
                if (!urlInfo.getUrl().startsWith(HTTP_PROCOTOL)) {
                    continue;
                }
                // end add by fengjw

                int parentDepth = 0;
                // get the depth of the doc for followlink exp added by
                // deepFollow
                if (isDeepFollow()
                        && doc.getUrlInfo().getFeature(ProcessorUtil.COMMON_PROP_DEPTH) != null) {
                    parentDepth = Integer.valueOf((String) doc.getUrlInfo().getFeature(
                            ProcessorUtil.COMMON_PROP_DEPTH));
                }

                // go depth 2 level at most, and "index" in url is necessary
                // added by deepFollow
                if (parentDepth > 0
                        || (isDeepFollow() && isListPage(urlInfo.getUrl().toLowerCase()))) {
                    if (!isNewsPage(urlInfo.getUrl().toLowerCase(), (parentDepth == 0))) {
                        continue;
                    }
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_SEEDURL, currentUrlInfo.getUrl());
                    if (parentDepth > 0) {
                        urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, "page");
                    } else {
                        urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, "seed");
                    }
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_DEPTH,
                            String.valueOf(parentDepth + 1));
                    linkList.add(urlInfo);
                } else {
                    if (!isNewsPage(urlInfo.getUrl().toLowerCase(), false)) {
                        continue;
                    }
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_SEEDURL, currentUrlInfo.getUrl());
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, "page");
                    linkList.add(urlInfo);
                }
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
        if (uri.endsWith("/")) {
            return false;
        }
        int lastDotIndex = uri.lastIndexOf(".");
        if ((lastDotIndex <= 0)) {
            return true;
        }

        String ext = uri.substring(lastDotIndex + 1);
        return ext.matches(goodUrlType);
    }

    protected String removeSidFromUrl(String url) {
        if ((url == null) || (url.length() == 0)) {
            return url;
        }
        return url.replaceFirst(this.sidPattern, "");
    }

    protected URL genURL(String url) {
        URL parentUrl = null;
        try {
            parentUrl = new URL(url);
        } catch (MalformedURLException e1) {
            return null;
        }
        return parentUrl;
    }

    private NodeList extractLinkNodes(NodeList nodeList) {
        NodeFilter[] linkFilters = { new TagNameFilter("A"), new TagNameFilter("AREA"),
                new TagNameFilter("FRAME"), new TagNameFilter("IFRAME"),
                new TagNameFilter("LAYER"), new TagNameFilter("ILAYER") };
        OrFilter orFilter = new OrFilter();
        orFilter.setPredicates(linkFilters);
        return nodeList.extractAllNodesThatMatch(orFilter, true);
    }

    private boolean isListPage(String url) {
        if (null == url) {
            return false;
        }
        for (String forbiddenTerm : OPTIONAL_TERMS) {
            if (url.indexOf(forbiddenTerm) != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewsPage(String url, boolean enableList) {
        if (null == url) {
            return false;
        }
        if (url.indexOf("?") >= 0) {
            return false;
        }
        if (url.lastIndexOf('/') == HTTP_PROCOTOL.length() - 1) {
            // url depth equals 1
            return false;
        }
        String restUrl = url.substring(1 + url.indexOf('/', HTTP_PROCOTOL.length()));// skip
                                                                                     // host
                                                                                     // part
        String[] parts = restUrl.split("/");
        if (parts.length > 2) {
            // in order to enable urls as following:
            // http://notebook.pconline.com.cn/price/bj/1106/2441208.html
            // http://www.qhnews.com/index/system/2011/06/14/010387318.shtml
            restUrl = restUrl.substring(parts[0].length() + 1);
        }
        for (String forbiddenTerm : FORBIDDEN_TERMS) {
            if (restUrl.indexOf(forbiddenTerm) != -1) {
                return false;
            }
        }
        if (!enableList) {
            for (String forbiddenTerm : OPTIONAL_TERMS) {
                if (restUrl.indexOf(forbiddenTerm) != -1) {
                    return false;
                }
            }
        }

        return isGoodType(url);
    }

    private UrlInfo getLinkFromTag(Tag tag, URL parentUrl, String origEncoding) {
        String url = null;
        String tagName = tag.getTagName();

        if (tagName.equals("A")) {
            LinkTag linktag = (LinkTag) tag;
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

        url = UrlUtil.getCanonicalURL(url);// normalizeUrl(url, origEncoding);
        UrlInfo urlInfo = new UrlInfo(url);

        return urlInfo;
    }

    /*
     * protected String normalizeUrl(String url, String origEncoding) { if (url
     * == null) { return url; } url = url.replaceAll("\\s+", "+"); url =
     * urlEnc(url, origEncoding); url = urlSpecialDecode(url);
     * 
     * // add by fengjw if (url.endsWith(PREFIX_5C22)) { url = url.substring(0,
     * url.length() - PREFIX_5C22_LEN); } if (url.endsWith(SUFFIX_MINUS)) { url
     * = url.substring(0, url.length() - SUFFIX_MINUS.length()); } if
     * (url.endsWith(SUFFIX_PLUS)) { url = url.substring(0, url.length() -
     * SUFFIX_PLUS.length()); }
     * 
     * int invadIndex = url.indexOf(HTTP_PROCOTOL); if (invadIndex > 0) { url =
     * url.substring(invadIndex); } // end end by fengjw invadIndex =
     * url.indexOf("#"); if (invadIndex > 0) { url = url.substring(0,
     * invadIndex); }
     * 
     * if (this.rmSidFromUrl) { url = removeSidFromUrl(url); } return url; }
     * 
     * private String urlSpecialDecode(String url) { if ((url == null) ||
     * (url.length() == 0)) { return url; } url = url.replaceAll("&amp;", "&");
     * return url; }
     * 
     * private String urlEnc(String u, String origEncoding) { StringBuffer r =
     * new StringBuffer(); try { for (int i = 0; i < u.length(); ++i) { int n =
     * u.codePointAt(i); char c = u.charAt(i);
     * 
     * if ((n >= 123) || (n <= 32) || (n == 92) || ((n >= 91) && (n <= 94)) ||
     * (n == 96) || (n == 34)) r.append(URLEncoder.encode(String.valueOf(c),
     * origEncoding)); else r.append(c); } } catch (Exception e) {
     * this.logger.warn("", e); } return r.toString(); }
     */
}
