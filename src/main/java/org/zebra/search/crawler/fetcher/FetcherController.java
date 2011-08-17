package org.zebra.search.crawler.fetcher;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.zebra.search.crawler.allocator.SeedCollection;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Configuration;

public class FetcherController {
	private static final Logger logger = Logger.getLogger(FetcherController.class.getName());

	private static class FetcherThread extends Thread {
		private Fetcher fetcher = null;
		private SeedCollection seedCollection = null;
		private CrawlDocumentCollection docCollection = null;
		public FetcherThread(Fetcher fetcher, SeedCollection seedCollection,
				CrawlDocumentCollection docCollection) {
			this.fetcher = fetcher;
			this.seedCollection = seedCollection;
			this.docCollection = docCollection;
		}
		public void run() {
			List<UrlInfo> urls = null;
			if (null == this.fetcher
				|| null == this.seedCollection
				|| null == this.docCollection) {
				logger.warn("fetcher / seed collection / doc collection is null");
				return;
			}
			while(isAlive()) {
				urls = this.seedCollection.getUrls(10);
				if (null == urls || urls.size() <= 0) {
					try {
						logger.debug("have no url to fetch, sleep 10s");
						sleep(10000);
					} catch (Exception ex) {
						logger.warn("failed to sleep. cause:" + ex.getMessage());
					}
					continue;
				}
				for (UrlInfo url : urls) {
					CrawlDocument doc = this.fetcher.fetchDocument(url);
					if (null == doc) {
						logger.warn("failed to fetch document. url=" + url.getUrl());
					} else {
						logger.debug("success to fetch document. url=" + url.getUrl());
					    this.docCollection.offer(doc);
					}
				}
			}
		}
	}
	private List<FetcherThread> threads = new ArrayList<FetcherThread>();

	public void initialize() {
		HttpClientFetcher.startConnectionMonitorThread();

		int threadNum = Configuration.getIntProperty(Configuration.PATH_FETCHER_NUM, 5);
		if (threadNum <= 0) {
			logger.warn("fetcher thread num is invalid!");
			threadNum = 1;
		}
		SeedCollection seedCollection = SeedCollection.getInstance();
		CrawlDocumentCollection docCollection = CrawlDocumentCollection.getInstance();
		for (int i = 0; i < threadNum; i++) {
			FetcherThread thread = new FetcherThread(new HttpClientFetcher(),
					seedCollection, docCollection);
			thread.start();
			threads.add(thread);
			logger.info("create fetcher thread :" + i);
			System.out.println("create fetcher thread :" + i);
		}
	}
	public void destroy() {
	    for (FetcherThread thread : threads) {
	    	try {
	    		thread.join(1000);
	    	} catch (Exception ex) {
	    		logger.warn("failed to stop fetcher thread, cause:" + ex.getMessage());
	    	}
	    }
	    HttpClientFetcher.stopConnectionMonitorThread();
	}
}
