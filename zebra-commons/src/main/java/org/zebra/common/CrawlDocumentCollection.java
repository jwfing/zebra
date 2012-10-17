package org.zebra.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.metrics.*;

import java.util.concurrent.*;
import java.util.*;

public class CrawlDocumentCollection implements MetricsReporter{
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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

	public List<Metrics> stat() {
	    List<Metrics> stats = new ArrayList<Metrics>();
	    stats.add(new Metrics("crawlDocCount", new Integer(this.queue.size()).toString()));
	    return stats;
	}

	public boolean offer(CrawlDocument doc) {
	    if (this.queue.size() > 1024) {
	        try {
	            logger.debug("too many document need to process, sleep 10s");
	            Thread.sleep(10000);
	        } catch (Exception ex) {
	            ;
	        }
	    }
		return this.queue.offer(doc);
	}

	public CrawlDocument poll() {
		return this.queue.poll();
	}
}
