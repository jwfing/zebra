package org.zebra.search.crawler.core;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.fetcher.*;
import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.plugin.*;

public class PipelineDriver{
	private static final Logger logger = Logger.getLogger(PipelineDriver.class.getName());
	private CrawlDocumentCollection collection = CrawlDocumentCollection.getInstance();
	private Dispatcher dispatcher = null;
	private int threadNum = 1;
	private List<Thread> threads = new ArrayList<Thread>();
	private class PipelineThread extends Thread {
		public void run() {
			if (null == collection || null == dispatcher) {
				logger.error("collection or dispatcher is null!");
				return;
			}
			while(isAlive()) {
				CrawlDocument doc = collection.poll();
				if (null == doc) {
					logger.debug("have no document in pipeline, sleep 5000ms");
					try {
						sleep(5000);
					} catch (Exception ex) {
						logger.warn("failed to sleep 50ms. cause:" + ex.getMessage());
					}
					continue;
				} else {
					boolean ret = dispatcher.process(doc);
					if (!ret) {
						logger.warn("failed to process doc. url=" + doc.getUrl());
					}
				}
			}
		}
	}

	public void initialize() {
		this.threadNum = Configuration.getIntProperty(Configuration.PATH_PIPELINE_THREADS, 1);
		this.dispatcher = new Dispatcher();
		ProcessorEntry entry = new NewsProcessorEntry();
		this.dispatcher.setEntry(entry);

		CharsetConvertor convertor = new CharsetConvertor();
		convertor.initialize();
		DocumentParser parser = new DocumentParser();
		parser.initialize();
		LinkFollower follower = new LinkFollower();
		follower.initialize();
		UrlPoolWriter urlGenerator = new UrlPoolWriter();
		urlGenerator.initialize();
		DeduperClient deduper = new DeduperClient();
		deduper.initialize();
		NewsElementExtractor extractor = new NewsElementExtractor();
		extractor.initialize();
		DummyProcessor dummy = new DummyProcessor();
		dummy.initialize();
		RulesetFilter filter = new RulesetFilter();
		filter.initialize();

		ProcessorChain seedChain = new ProcessorChain();
//		seedChain.addProcessor(convertor);
		seedChain.addProcessor(parser);
		seedChain.addProcessor(follower);
		seedChain.addProcessor(filter);
		seedChain.addProcessor(deduper);
		seedChain.addProcessor(urlGenerator);
		seedChain.addProcessor(dummy);
		ProcessorChain contentChain = new ProcessorChain();
//		contentChain.addProcessor(convertor);
//		contentChain.addProcessor(parser);
//		contentChain.addProcessor(extractor);
		contentChain.addProcessor(dummy);

		this.dispatcher.addChain(ProcessDirectory.LIST_PAGE, seedChain);
		this.dispatcher.addChain(ProcessDirectory.CONTENT_PAGE, contentChain);
		logger.info("pipeline driver initialized. threadNum=" + this.threadNum);

		// TODO: initialize java-bean defined in service-process-chain.xml
	}
	public void destroy() {
		for (Thread thread : threads) {
			try {
			    thread.join(50);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	public void start() {
		logger.info("begin to start pipeline driver. threadNum=" + this.threadNum);
//		ExecutorService pool = Executors.newFixedThreadPool(this.threadNum);
		Thread thread = null;
		for (int i = 0; i < this.threadNum; ++i) {
			thread = new PipelineThread();
			thread.start();
			threads.add(thread);
		}
//		pool.shutdown();
	}
}
