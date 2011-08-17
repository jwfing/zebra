package org.zebra.search.crawler.deduper;

import java.util.*;
import java.util.Map.Entry;

import junit.framework.TestCase;
import org.zebra.search.crawler.common.*;

public class DeduperTests extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testHashDeduper() {
		HashDeduper deduper = new HashDeduper();
		List<UrlInfo> urls = new ArrayList<UrlInfo>();
		List<UrlInfo> url2 = new ArrayList<UrlInfo>();
		for (int i  = 0; i < 10; i++) {
			urls.add(new UrlInfo("http://org.test.cn/list" + i));
			url2.add(new UrlInfo("http://org.test.cn/other/list" + i));
		}
		Map<String, Boolean> result = deduper.dedup(urls);
		if (null == result || result.size() != urls.size()) {
			fail("dedup result is empty");
		}
		Set<Entry<String, Boolean> > entries = result.entrySet();
		Iterator<Entry<String, Boolean> > iter = entries.iterator();
		while(iter.hasNext()) {
			if (iter.next().getValue()) {
				fail("dedup result is error");
			}
		}
		result = deduper.dedup(urls);
		if (null == result || result.size() != urls.size()) {
			fail("dedup result is empty");
		}
		entries = result.entrySet();
		iter = entries.iterator();
		while(iter.hasNext()) {
			if (!iter.next().getValue()) {
				fail("dedup result is error");
			}
		}
		
		List<Boolean> justDedupResult = deduper.juegeDeduped(url2);
		if (null == justDedupResult || justDedupResult.size() != url2.size()) {
			fail("dedup.juegeDeduped result is empty");
		}
		for (Boolean tmpResult : justDedupResult) {
			if (tmpResult) {
				fail("dedup.juegeDeduped result is error");
			}
		}
		
		justDedupResult = deduper.juegeDeduped(url2);
		if (null == justDedupResult || justDedupResult.size() != url2.size()) {
			fail("dedup.juegeDeduped result is empty");
		}
		for (Boolean tmpResult : justDedupResult) {
			if (tmpResult) {
				fail("dedup.juegeDeduped result is error");
			}
		}
	}
}
