package org.zebra.search.crawler.plugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Processor;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.util.ProcessorUtil;
import org.zebra.search.crawler.util.UrlUtil;

public class NewsReportExtractor implements Processor{
	private final Logger logger = Logger.getLogger(NewsReportExtractor.class);

	private static final String defaultEncoding = "GB2312";
	private static final String goodUrlType = "(pdf|doc)";

	public boolean initialize() {
		logger.info("successful initialized " + NewsReportExtractor.class.getName());
		return true;
	}

	public boolean destroy() {
		logger.info("successful destroied " + NewsReportExtractor.class.getName());
		return true;
	}

	public String getName() {
		return this.getClass().getName();
	}

	private NodeList extractLinkNodes(NodeList nodeList) {
		NodeFilter[] linkFilters = {new TagNameFilter("A")};
		OrFilter orFilter = new OrFilter();
		orFilter.setPredicates(linkFilters);
		return nodeList.extractAllNodesThatMatch(orFilter, true);
	}

	public boolean process(CrawlDocument doc, Context context) {
		if (null == doc || null == context) {
			logger.warn("invalid parameter.");
		    return false;
		}
		NodeList nodeList = (NodeList) context.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			logger.debug("the node list is null");
			return true;
		}
		UrlInfo currentUrlInfo = doc.getUrlInfo();
		String base = (String)doc.getFeature(ProcessorUtil.COMMON_PROP_BASE);
		if (null == base || base.isEmpty()) {
			base = doc.getUrl();
			base = base.substring(0, base.lastIndexOf('/'));
		}
		URL baseUrl = UrlUtil.genURL(base);
		String origEncoding = defaultEncoding;

		NodeList links = extractLinkNodes(nodeList);
		List<UrlInfo> linkList = new ArrayList<UrlInfo>();
		try {
			for (NodeIterator i = links.elements(); i.hasMoreNodes();)
			{
				Tag tag = (Tag) i.nextNode();
				if (tag.isEndTag()) {
					continue;
				}
	
				UrlInfo urlInfo = getLinkFromTag(tag, baseUrl, origEncoding);
				if ((urlInfo == null) || (urlInfo.getUrl() == null)) {
					continue;
				}
				if (!isBinaryPage(urlInfo.getUrl().toLowerCase())) {
					continue;
				}
				urlInfo.addFeature(ProcessorUtil.COMMON_PROP_SEEDURL,
						currentUrlInfo.getUrl());
				urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG,
						"binary");
				linkList.add(urlInfo);
			}
		} catch (Exception e) {
			this.logger.warn("exception occurred in linkFollow. cause: " + e.getMessage());
		}

		context.setVariable(ProcessorUtil.COMMON_PROP_BINARYLINKS, linkList);
		logger.info("binary-link follow. docUrl=" + doc.getUrl() + ", outlinks=" + linkList.size());
		return true;
	}

	private boolean isBinaryPage(String url) {
		if (null == url || url.isEmpty()) {
			return false;
		}
		int lastDot = url.lastIndexOf('.');
		if (lastDot <= 0) {
			return false;
		}
		String suffix = url.substring(lastDot + 1);
		return suffix.toLowerCase().matches(goodUrlType);
	}

	private UrlInfo getLinkFromTag(Tag tag, URL parentUrl, String origEncoding) {
		String url = null;
		String tagName = tag.getTagName();

		if (tagName.equals("A")) {
			url = UrlUtil.getAbsoluteUrl(parentUrl, tag.getAttribute("href"));
		}

		url = UrlUtil.getCanonicalURL(url);
		UrlInfo urlInfo = new UrlInfo(url);

		return urlInfo;
	}
}
