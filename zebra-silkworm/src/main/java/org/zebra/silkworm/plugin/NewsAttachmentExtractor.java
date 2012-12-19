package org.zebra.silkworm.plugin;

import java.net.URL;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.zebra.common.*;
import org.zebra.common.flow.*;
import org.zebra.common.utils.*;
import org.zebra.common.http.Fetcher;
import org.zebra.common.http.HttpClientFetcher;

public class NewsAttachmentExtractor implements Processor{
	private final Logger logger = Logger.getLogger(NewsAttachmentExtractor.class);

	private static final String defaultEncoding = "GB2312";
	private static final String goodUrlType = "(pdf|doc)";

	private Fetcher fetcher = new HttpClientFetcher();
	private String downloadDir = "";

	public boolean initialize() {
		logger.info("successful initialized " + NewsAttachmentExtractor.class.getName());
		return true;
	}

	public boolean destroy() {
		logger.info("successful destroied " + NewsAttachmentExtractor.class.getName());
		return true;
	}

	public String getName() {
		return this.getClass().getName();
	}

	public Fetcher getFetcher() {
        return fetcher;
    }

    public void setFetcher(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
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
//		UrlInfo currentUrlInfo = doc.getUrlInfo();
		String base = (String)doc.getFeature(ProcessorUtil.COMMON_PROP_BASE);
		if (null == base || base.isEmpty()) {
			base = doc.getUrl();
			base = base.substring(0, base.lastIndexOf('/'));
		}
		URL baseUrl = UrlUtil.genURL(base);
		NodeList links = extractLinkNodes(nodeList);
		List<String> binaryFiles = new ArrayList<String>();
		try {
			for (NodeIterator i = links.elements(); i.hasMoreNodes();)
			{
				Tag tag = (Tag) i.nextNode();
				if (tag.isEndTag()) {
					continue;
				}
	
				UrlInfo urlInfo = getLinkFromTag(tag, baseUrl);
				if ((urlInfo == null) || (urlInfo.getUrl() == null)) {
					continue;
				}
				String urlStr = urlInfo.getUrl().toLowerCase();
				if (!isBinaryPage(urlStr)) {
					continue;
				}
				String type = urlStr.substring(urlStr.lastIndexOf('.'));
				CrawlDocument attachDoc = fetcher.fetchDocument(urlInfo);
				String fileName = StringUtil.computeMD5(urlInfo.getUrl());
				OutputStream os = new FileOutputStream(downloadDir + "/" + fileName + type);
				os.write(attachDoc.getContentBytes());
				os.flush();
				os.close();
				binaryFiles.add(fileName + type);
			}
		} catch (Exception e) {
			this.logger.warn("exception occurred in linkFollow. cause: " + e.getMessage());
		}
		context.setVariable(ProcessorUtil.COMMON_PROP_BINARYLINKS, binaryFiles);

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

	private UrlInfo getLinkFromTag(Tag tag, URL parentUrl) {
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
