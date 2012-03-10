package org.zebra.search.crawler.plugin;

import org.apache.log4j.Logger;
import java.util.List;

import org.zebra.search.crawler.urlPool.*;
import org.zebra.search.crawler.util.ProcessorUtil;
import org.zebra.search.crawler.common.*;

public class UrlPoolWriter implements Processor {
    private static final Logger logger = Logger.getLogger(UrlPoolWriter.class);
    private UrlAppender appender = UrlAppender.getInstance();

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
        if (null == doc || null == context) {
            return false;
        }
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        if (null != outlinks && outlinks.size() > 0) {
            if (null != this.appender) {
                this.appender.appendOnceUrls(outlinks);
                logger.info("write new links to urlPool. size=" + outlinks.size());
            } else {
                logger.warn("url appender is null");
            }
        }
        return true;
    }
}
