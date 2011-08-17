package org.zebra.search.crawler.plugin;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Processor;

public class DummyProcessor implements Processor {
	private final Logger logger = Logger.getLogger(DummyProcessor.class);

	public boolean initialize() {
		return true;
	}

	public boolean destroy() {
		return true;
	}
	public String getName() {
		return this.getClass().getName();
	}

	public boolean process(CrawlDocument doc, Context context) {
		if (null != doc) {
		    logger.info("process doc. url=" + doc.getUrl());
		}
		return true;
	}
}
