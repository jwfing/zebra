package org.zebra.search.crawler.plugin;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.*;

public class NewsElementExtractor implements Processor{
	private final Logger logger = Logger.getLogger(NewsElementExtractor.class);
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
		return true;
	}
}
