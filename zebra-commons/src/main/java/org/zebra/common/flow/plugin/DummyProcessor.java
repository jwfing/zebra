package org.zebra.common.flow.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.flow.Processor;

public class DummyProcessor implements Processor {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

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
