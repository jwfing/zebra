package org.zebra.search.crawler.common;

public interface Processor {
	public boolean initialize();
	public boolean destroy();
	public String getName();
    public boolean process(CrawlDocument doc, Context context);
}
