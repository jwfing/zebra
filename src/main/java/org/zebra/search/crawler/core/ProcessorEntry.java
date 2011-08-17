package org.zebra.search.crawler.core;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;

public interface ProcessorEntry {
    public boolean initialize();
    public boolean destroy();
    public ProcessDirectory process(CrawlDocument doc, Context context);
}
