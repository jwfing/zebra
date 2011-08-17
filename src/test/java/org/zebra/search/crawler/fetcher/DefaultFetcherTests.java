package org.zebra.search.crawler.fetcher;

import junit.framework.TestCase;
import java.io.*;

import org.zebra.search.crawler.common.*;

public class DefaultFetcherTests extends TestCase {
	private HttpClientFetcher fetcher = new HttpClientFetcher();
	private static boolean initiazlied = false;
	protected void setUp() throws Exception {
		super.setUp();
		if (!initiazlied) {
			HttpClientFetcher.startConnectionMonitorThread();
		    initiazlied = true;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
//		fetcher.stopConnectionMonitorThread();
	}

	public void testGeneralDownload() {
//		DefaultFetcher fetcher = new DefaultFetcher();
		UrlInfo url = new UrlInfo("http://news.163.com");
		CrawlDocument doc = fetcher.fetchDocument(url);
		if (null == doc || null == doc.getContentString()) {
			fail("failed to fetch document");
		}
		System.out.println("content-length:" + doc.getContentString().length());
	}
	public void testListPageDownload() {
//		DefaultFetcher fetcher = new DefaultFetcher();
		UrlInfo url = new UrlInfo("http://ent.163.com/special/00031HA4/moviecomments.html");
		CrawlDocument doc = fetcher.fetchDocument(url);
		if (null == doc || null == doc.getContentString()) {
			fail("failed to fetch document");
		}
		System.out.println("content-length:" + doc.getContentString().length());
		try {
		    File file = new File("./movie.html");
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(doc.getContentBytes());
		    fos.close();
		} catch (Exception ex) {
			fail("failed to output local file.");
		}
	}
	public void testNewsPageDownload() {
//		DefaultFetcher fetcher = new DefaultFetcher();
		UrlInfo url = new UrlInfo("http://ent.163.com/11/0630/08/77PHU9JS000300B1.html");
		CrawlDocument doc = fetcher.fetchDocument(url);
		if (null == doc || null == doc.getContentString()) {
			fail("failed to fetch document");
		}
		System.out.println("content-length:" + doc.getContentString().length());
		try {
		    File file = new File("./detail.html");
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(doc.getContentBytes());
		    fos.close();
		} catch (Exception ex) {
			fail("failed to output local file.");
		}
	}
	public void testBinaryDownload() {
//		DefaultFetcher fetcher = new DefaultFetcher();
		UrlInfo url = new UrlInfo("http://file.finance.sina.com.cn/211.154.219.97:9494/MRGG/SBGG/2011/2011-7/2011-07-22/752474.PDF");
		CrawlDocument doc = fetcher.fetchDocument(url);
		if (null == doc || null == doc.getContentBytes()) {
			fail("failed to fetch document");
		}
		System.out.println("content-length:" + doc.getContentBytes().length);
		try {
		    File file = new File("./data.pdf");
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(doc.getContentBytes());
		    fos.close();
		} catch (Exception ex) {
			fail("failed to output local file.");
		}
	}
}
