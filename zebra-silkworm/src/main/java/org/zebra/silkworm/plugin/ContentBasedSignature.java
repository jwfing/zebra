package org.zebra.silkworm.plugin;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Processor;

public class ContentBasedSignature implements Processor {
    private final Logger logger = Logger.getLogger(ContentBasedSignature.class);

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
        logger.warn("unsupported-method");
        return false;
    }
}
