package org.zebra.search.crawler.plugin;

import junit.framework.TestCase;
import java.io.*;

import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.fetcher.HttpClientFetcher;
import org.zebra.search.crawler.fetcher.Crawler4jFetcher;

public class CharsetConvertorTests extends TestCase {
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
	}
	public void testNewsPageDownload() {
		UrlInfo url = new UrlInfo("http://ent.163.com/11/0630/08/77PHU9JS000300B1.html");
		CrawlDocument doc = fetcher.fetchDocument(url);
		if (null == doc || null == doc.getContentString()) {
			fail("failed to fetch document");
		}
		try {
		    File file = new File("./detail_dl1.html");
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(doc.getContentBytes());
		    fos.close();
		} catch (Exception ex) {
			fail("failed to output local file.");
		}
		Context context = new Context();
		CharsetConvertor convertor = new CharsetConvertor();
		boolean result = convertor.process(doc, context);
		if (!result) {
			fail("failed to invoke convertor.process");
		}
		try {
		    File file = new File("./detail_dl2.html");
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(doc.getContentBytes());
		    fos.close();
		} catch (Exception ex) {
			fail("failed to output local file.");
		}
	}
}
