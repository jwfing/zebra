package org.zebra.search.crawler.deduper;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.fetcher.Fetcher;
import org.zebra.search.crawler.util.StringUtil;

public class HashDeduper implements Deduper {
	private static final Logger logger = Logger.getLogger(HashDeduper.class);

	private ConcurrentHashMap<Long, Boolean> _dedupIDs
	    = new ConcurrentHashMap<Long, Boolean>(1024*1024);// max size 1M

	public Map<String, Boolean> dedup(List<UrlInfo> urls) {
    	Map<String, Boolean> result = new HashMap<String, Boolean>();
		String urlStr = "";
		Long hashValue = new Long(0l);
    	for (UrlInfo url : urls) {
    		urlStr = url.getUrl();
    		hashValue = new Long(StringUtil.FNVHash(urlStr));
    		if (this._dedupIDs.containsKey(hashValue)) {
    			result.put(urlStr, true);
    		} else {
    			result.put(urlStr, false);
    			this._dedupIDs.putIfAbsent(hashValue, true);
    		}
    	}
    	return result;
    }
    public List<Boolean> juegeDeduped(List<UrlInfo> urls) {
    	List<Boolean> result = new ArrayList<Boolean>();
		String urlStr = "";
		Long hashValue = new Long(0l);
    	for (UrlInfo url : urls) {
    		urlStr = url.getUrl();
    		hashValue = new Long(StringUtil.FNVHash(urlStr));
    		if (this._dedupIDs.containsKey(hashValue)) {
    			result.add(true);
    		} else {
    			result.add(false);
    		}
    	}
    	return result;
    }
    public boolean deleteInvalidUrl(List<UrlInfo> urls) {
    	logger.warn("no implements this method");
    	return false;
    }
    public boolean checkpoint(String fileName) {
    	logger.warn("no implements this method");
    	return false;
    }
    public boolean reload(String fileName) {
    	logger.warn("no implements this method");
    	return false;
    }
    public boolean isFull() {
    	return false;
    }
    public void clear() {
    	;
    }
}
