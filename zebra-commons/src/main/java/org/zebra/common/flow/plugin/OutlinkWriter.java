package org.zebra.common.flow.plugin;

import org.apache.log4j.Logger;
import java.util.List;

import org.zebra.common.*;
import org.zebra.common.utils.*;
import org.zebra.common.flow.*;
import org.zebra.common.domain.*;
import org.zebra.common.domain.dao.*;

public class OutlinkWriter implements Processor {
    private static final Logger logger = Logger.getLogger(OutlinkWriter.class);
    private FollowedLinkDao linkDao = null;

    public boolean initialize() {
        return true;
    }

    public boolean destroy() {
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public FollowedLinkDao getLinkDao() {
        return linkDao;
    }

    public void setLinkDao(FollowedLinkDao linkDao) {
        this.linkDao = linkDao;
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context || null == this.linkDao) {
            return false;
        }
        String source = doc.getUrl();
        long now = System.currentTimeMillis() / 1000;
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        if (null != outlinks && outlinks.size() > 0) {
            for (UrlInfo url: outlinks) {
                FollowedLink followedLink = new FollowedLink();
                followedLink.setSeedUrl(source);
                followedLink.setUrl(url.getUrl());
                followedLink.setUrlMd5(StringUtil.computeMD5(url.getUrl()));
                followedLink.setTimeCreated(now);
                try {
                    this.linkDao.save(followedLink);
                } catch (Exception ex) {
                    ;
                }
            }
            logger.info("write new links to urlPool. size=" + outlinks.size());
        }
        return true;
    }
}
