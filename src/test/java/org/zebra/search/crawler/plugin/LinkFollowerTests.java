package org.zebra.search.crawler.plugin;

import java.util.List;

import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.util.ProcessorUtil;

public class LinkFollowerTests extends PluginTestBase {
	public LinkFollowerTests() {
		super("./vCB.html");
	}

	public void testContentPage() {
		Context context = new Context();
		LinkFollower follower = new LinkFollower();
		DocumentParser parser = new DocumentParser();
		boolean result = parser.process(doc, context);
		if (!result) {
			fail("failed to process htmlparser");
		}
		result = follower.process(doc, context);
		if (!result) {
			fail("failed to process linkfollow");
		}
		List<UrlInfo> outlinks = (List<UrlInfo>)context.getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
		System.out.println("outlinks size:" + outlinks.size());
		System.out.println("outlinks sample:" + ((UrlInfo)outlinks.get(0)).getUrl());
		
    	for (UrlInfo link : outlinks) {
			System.out.println(link.getUrl());
		}
	}
	public void testReportDocument() {
		Context context = new Context();
		NewsReportExtractor follower = new NewsReportExtractor();
		DocumentParser parser = new DocumentParser();
		boolean result = parser.process(doc, context);
		if (!result) {
			fail("failed to process htmlparser");
		}
		result = follower.process(doc, context);
		if (!result) {
			fail("failed to process linkfollow");
		}
		List<UrlInfo> outlinks = (List<UrlInfo>)context.getVariable(ProcessorUtil.COMMON_PROP_BINARYLINKS);
		System.out.println("outlinks size:" + outlinks.size());
		System.out.println("outlinks sample:" + ((UrlInfo)outlinks.get(0)).getUrl());
		
    	for (UrlInfo link : outlinks) {
			System.out.println(link.getUrl());
		}
	}
}
