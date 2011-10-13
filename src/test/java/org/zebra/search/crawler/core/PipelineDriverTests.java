package org.zebra.search.crawler.core;

import java.util.List;

import org.springframework.context.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.Processor;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.fetcher.HttpClientFetcher;
import org.zebra.search.crawler.plugin.DocumentParser;
import org.zebra.search.crawler.plugin.LinkFollower;
import org.zebra.search.crawler.util.ProcessorUtil;

import junit.framework.TestCase;

public class PipelineDriverTests extends TestCase {
	private HttpClientFetcher fetcher = new HttpClientFetcher();
	private static boolean initialized = false;
	protected String url = "http://money.163.com/special/002534M5/review.html";

	protected void setUp() throws Exception {
		super.setUp();
		if (!initialized) {
			HttpClientFetcher.startConnectionMonitorThread();
			initialized = true;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConfig() {
		PipelineDriver driver = new PipelineDriver();
		driver.initialize();
	}

	public void testDOMParser() {
		UrlInfo urlInfo = new UrlInfo(url);
		CrawlDocument doc = fetcher.fetchDocument(urlInfo);
		if (null == doc || null == doc.getContentString()) {
			fail("failed to fetch document");
		}
		DocumentParser parser = new DocumentParser();
		parser.initialize();
		LinkFollower follower = new LinkFollower();
		follower.initialize();

		Context context = new Context();
		boolean result = parser.process(doc, context);
		if (!result) {
			fail("failed to parse document");
		}
		result = follower.process(doc, context);
		if (!result) {
			fail("failed to follow links");
		}
		List<UrlInfo> outlinks = (List<UrlInfo>) context
				.getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
		for (UrlInfo link : outlinks) {
			System.out.println(link.getUrl());
		}
	}

}
