package org.zebra.search.crawler.plugin;

import java.io.*;
import junit.framework.TestCase;

import org.zebra.search.crawler.common.*;

public class PluginTestBase extends TestCase {
	protected FileInputStream fis = null;
	protected String url = "http://ent.163.com/11/0630/08/77PHU9JS000300B1.html";
	protected CrawlDocument doc = null;
	protected String localFile = "";
	public PluginTestBase(String file) {
		this.localFile = file;
	}
	protected void setUp() throws Exception {
		super.setUp();
		File file = new File(this.localFile);
		fis = new FileInputStream(file);
		doc = new CrawlDocument();
		doc.setContent(fis, (int)file.length(), false);
		doc.setFetchStatus(FetchStatus.OK);
		doc.setUrlInfo(new UrlInfo(url));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (null != fis) {
			fis.close();
			fis = null;
		}
		doc = null;
	}

}
