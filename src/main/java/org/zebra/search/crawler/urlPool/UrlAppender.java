package org.zebra.search.crawler.urlPool;

import java.util.List;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.*;

public class UrlAppender {
	private static final Logger logger = Logger.getLogger(UrlAppender.class);
	private static UrlAppender instance = null;
	private UrlStorage storage = null;

	public static UrlAppender getInstance() {
		if (null == instance) {
			synchronized(UrlAppender.class) {
				if (null == instance) {
					instance = new UrlAppender();
				}
			}
		}
		return instance;
	}
	private UrlAppender() {
		;
	}

	public UrlStorage getStorage() {
		return storage;
	}

	public void setStorage(UrlStorage storage) {
		logger.info("initialize urlAppender with urlStorage");
		this.storage = storage;
	}

	public boolean appendOnceUrls(List<UrlInfo> urls) {
		if (null != this.storage) {
			return this.storage.addOnceUrls(urls);
		}
		return false;
	}
	public boolean appendRepeatUrls(List<UrlInfo> urls, int level) {
		if (null != this.storage) {
			return this.storage.addRepeatUrls(urls, level);
		}
		return false;
	}
}
