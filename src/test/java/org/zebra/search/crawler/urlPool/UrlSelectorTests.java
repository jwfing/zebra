package org.zebra.search.crawler.urlPool;

import java.util.ArrayList;
import java.util.List;

import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.urlPool.storage.BDBStorageImpl;

import junit.framework.TestCase;

public class UrlSelectorTests extends TestCase {
	private BDBStorageImpl impl = null;
	private List<UrlInfo> repeatUrls = null;
	private List<UrlInfo> onceUrls = null;
	protected void setUp() throws Exception {
		super.setUp();
		impl = new BDBStorageImpl();
		impl.initialize();
		this.repeatUrls = new ArrayList<UrlInfo>();
		for (int i = 0; i < 10; i++) {
			UrlInfo info = new UrlInfo("http://news.163.com.repeat." + i);
			info.addFeature("key", i);
			this.repeatUrls.add(info);
		}
		this.onceUrls = new ArrayList<UrlInfo>();
		for (int i = 0; i < 10; i++) {
			UrlInfo info = new UrlInfo("http://news.163.com.once." + i);
			info.addFeature("key", i);
			this.onceUrls.add(info);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.repeatUrls = null;
		this.onceUrls = null;
		impl.destroy();
		impl = null;
	}

	public void testRetrieveOnceUrls() {
		UrlSelector selector = new UrlSelector();
		List<UrlInfo> result = selector.retrieveOnceUrls(100, 0, false);
		if (null != result) {
			fail("failed to retrieve once urls for un-initialized selector");
		}
		impl.addOnceUrls(this.onceUrls);
		selector.setStorage(this.impl);
		result = selector.retrieveOnceUrls(100, 0, false);
		if (null == result || result.size() != 10) {
			fail("failed to retrieve once urls first-time");
		}
		result = selector.retrieveOnceUrls(6, 0, true);
		if (null == result || result.size() != 6) {
			fail("failed to retrieve once urls second-time(delete flag: true)");
		}
		result = selector.retrieveOnceUrls(100, 0, true);
		if (null == result || result.size() != 4) {
			fail("failed to retrieve once urls third-time(delete flag: true)");
		}
	}
	public void testRetrieveRepeatUrls() {
		;
	}
}
