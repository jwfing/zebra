package org.zebra.common.flow.plugin;

import org.slf4j.Logger;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.zebra.common.*;
import org.zebra.common.utils.*;
import org.zebra.common.flow.*;
import org.zebra.common.domain.*;
import org.zebra.common.domain.dao.*;

public class OutlinkWriter implements Processor {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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
        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }
        String source = doc.getUrl();
        String tag = doc.getFeature(ProcessorUtil.COMMON_PROP_TAG);
        long now = System.currentTimeMillis() / 1000;
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        if (null != outlinks && outlinks.size() > 0) {
            for (UrlInfo url: outlinks) {
                FollowedLink followedLink = new FollowedLink();
                followedLink.setSeedUrl(source);
                followedLink.setUrl(url.getUrl());
                followedLink.setUrlMd5(StringUtil.computeMD5(url.getUrl()));
                followedLink.setTags(tag);
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
