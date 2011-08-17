package org.zebra.search.crawler.fetcher;

import org.apache.log4j.Logger;
import java.util.concurrent.*;

import org.zebra.search.crawler.common.*;

public class CrawlDocumentCollection {
	private static final Logger logger = Logger.getLogger(CrawlDocumentCollection.class.getName());
	private static CrawlDocumentCollection instance = null;
	private ConcurrentLinkedQueue<CrawlDocument> queue = new ConcurrentLinkedQueue<CrawlDocument>();

	public static CrawlDocumentCollection getInstance() {
		if (null == instance) {
			synchronized(CrawlDocumentCollection.class) {
				if (null == instance) {
					instance = new CrawlDocumentCollection();
				}
			}
		}
		return instance;
	}
	private CrawlDocumentCollection() {
	}

	public boolean offer(CrawlDocument doc) {
		return this.queue.offer(doc);
	}
	public CrawlDocument poll() {
		return this.queue.poll();
	}
}
