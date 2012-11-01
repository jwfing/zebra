package org.zebra.spider.plugin;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.*;
import org.zebra.common.utils.*;
import org.zebra.common.flow.*;

public class RulesetFilter implements Processor {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    protected static final String STRICT_NONE = "none";
    protected static final String STRICT_DOMAIN = "domain";
    protected static final String STRICT_HOST = "host";

    public boolean initialize() {
        return true;
    }

    public boolean destroy() {
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }
    
    private boolean isInvalidUrl(String linkUrl) {
        if (null == linkUrl || linkUrl.isEmpty()) {
            return true;
        }
        if (linkUrl.endsWith(".gif") || linkUrl.endsWith(".jpg") || linkUrl.endsWith(".png")
            || linkUrl.endsWith(".bmp") || linkUrl.endsWith(".jpeg")) {
            return true;
        }
        return false;
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            return false;
        }
        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }

        String trustRank = (String)context.getVariable(ProcessorUtil.COMMON_PROP_TRUSTRANK);
        if (ProcessorUtil.TRUSTRANK_PROP_HIGH.equalsIgnoreCase(trustRank)) {
            return true;
        }

        String seedStrict = doc.getFeature(ProcessorUtil.COMMON_PROP_STRICT);
        String strictRule = STRICT_NONE;
        if (null == seedStrict || seedStrict.isEmpty()) {
            strictRule = STRICT_NONE;
        } else {
            strictRule = seedStrict.trim().toLowerCase();
        }
        String docHost = UrlUtil.getHostFromUrl(doc.getUrl());
        String docDomain = UrlUtil.getDomainFromHost(docHost);
        Pattern pattern = null;
        if (strictRule.equals(STRICT_NONE) || strictRule.equals(STRICT_HOST) || strictRule.equals(STRICT_DOMAIN)) {
            pattern = null;
        } else {
            pattern = Pattern.compile(strictRule);
        }
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        if (null != outlinks && outlinks.size() > 0) {
            List<UrlInfo> reallinks = new ArrayList<UrlInfo>();
            for (UrlInfo item : outlinks) {
                String linkUrl = item.getUrl().toLowerCase();
                if (isInvalidUrl(linkUrl)) {
                    continue;
                }
                if (null != pattern) {
                    Matcher matcher = pattern.matcher(item.getUrl());
                    if (matcher.matches()) {
                        reallinks.add(item);
                    } else {
                        continue;
                    }
                } else {
                    String itemHost = UrlUtil.getHostFromUrl(item.getUrl());
                    String itemDomain = UrlUtil.getDomainFromHost(itemHost);
                    
                    if (strictRule.equals(STRICT_HOST) && docHost.equalsIgnoreCase(itemHost)) {
                        reallinks.add(item);
                    } else if (strictRule.equals(STRICT_DOMAIN) && docDomain.equals(itemDomain)) {
                        reallinks.add(item);
                    } else if (strictRule.equals(STRICT_NONE)) {
                        reallinks.add(item);
                    }
                }
            }
            context.setVariable(ProcessorUtil.COMMON_PROP_OUTLINKS, reallinks);
            logger.info("discard new link from " + outlinks.size() + " to " + reallinks.size()
                    + " for document=" + doc.getUrl());
        }
        return true;
    }
}
