package org.zebra.spider.plugin;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.zebra.common.*;
import org.zebra.common.utils.*;
import org.zebra.common.flow.*;

public class RulesetFilter implements Processor {
    private final Logger logger = Logger.getLogger(RulesetFilter.class);

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
        if (null != doc && null != context) {
            String docHost = UrlUtil.getHostFromUrl(doc.getUrl());
            List<UrlInfo> outlinks = (List<UrlInfo>) context
                    .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
            if (null != outlinks && outlinks.size() > 0) {
                List<UrlInfo> reallinks = new ArrayList<UrlInfo>();
                for (UrlInfo item : outlinks) {
                    String itemHost = UrlUtil.getHostFromUrl(item.getUrl());
                    if (docHost.equalsIgnoreCase(itemHost)) {
                        reallinks.add(item);
                    }
                }
                context.setVariable(ProcessorUtil.COMMON_PROP_OUTLINKS, reallinks);
                logger.info("discard new link from " + outlinks.size() + " to " + reallinks.size()
                        + " for document=" + doc.getUrl());
            }
        }
        return true;
    }
}
