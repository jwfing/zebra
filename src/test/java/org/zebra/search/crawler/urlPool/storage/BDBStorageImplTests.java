package org.zebra.search.crawler.urlPool.storage;

import junit.framework.TestCase;
import java.util.*;

import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.urlPool.Constants;

public class BDBStorageImplTests extends TestCase{
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

	public void testInit() {
	}
	public void testAddRepeatUrls() {
		boolean ret = this.impl.addRepeatUrls(this.repeatUrls, 9);
		if (!ret) {
			fail();
		}
	}
	public void testAddOnceUrls() {
		boolean ret = this.impl.addOnceUrls(this.onceUrls);
		if (!ret) {
			fail();
		}
	}
	public void testSelect() {
		testAddRepeatUrls();
		testAddOnceUrls();
		List<UrlInfo> urls = this.impl.selectFromRepeatUrls(8, 0, 100);
		if (null != urls && urls.size() > 0) {
			fail();
		}
		urls = this.impl.selectFromOnceUrls(0, 4);
		if (null == urls || urls.size() != 4) {
			fail();
		}
		urls = this.impl.selectFromOnceUrls(0, 40);
		if (null == urls || urls.size() != 10) {
			fail();
		}
	}
	public void testDropUrl() {
		testSelect();
		boolean ret = this.impl.dropUrls(this.repeatUrls, Constants.UrlType.REPEAT);
		if (!ret) {
			fail();
		}
		ret = this.impl.dropUrls(this.repeatUrls, Constants.UrlType.ONCE);
		
		List<UrlInfo> urls = this.impl.selectFromRepeatUrls(9, 0, 20);
		if (urls != null && urls.size() > 0) {
			fail();
		}
		urls = this.impl.selectFromOnceUrls(0, 20);
		if (urls == null || urls.size() <= 0) {
			fail();
		}
		ret = this.impl.dropUrls(this.onceUrls, Constants.UrlType.ONCE);
		if (!ret) {
			fail();
		}
		urls = this.impl.selectFromOnceUrls(0, 20);
		if (urls != null && urls.size() > 0) {
			fail();
		}
	}
}
