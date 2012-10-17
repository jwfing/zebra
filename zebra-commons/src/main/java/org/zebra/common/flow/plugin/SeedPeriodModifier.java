package org.zebra.common.flow.plugin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.UrlInfo;
import org.zebra.common.flow.Processor;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.common.utils.StringUtil;
import org.zebra.common.domain.dao.SeedDao;
import org.zebra.common.domain.Seed;

public class SeedPeriodModifier implements Processor {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private static final long MAX_PERIOD = 68400 * 30;  // a month
    private static final long MIN_PERIOD = 300;         // 5 minutes
    private SeedDao seedDao;

    @Override
    public boolean destroy() {
        return false;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean initialize() {
        return true;
    }

    public SeedDao getSeedDao() {
        return seedDao;
    }

    public void setSeedDao(SeedDao seedDao) {
        this.seedDao = seedDao;
    }

    @Override
    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context || null == this.seedDao) {
            logger.warn("invalid parameter.");
            return false;
        }
        String source = doc.getUrl();
        String urlMd5 = StringUtil.computeMD5(source);
        Seed seed = this.seedDao.loadByUrlmd5(urlMd5);
        long now = System.currentTimeMillis() / 1000;
        long newPeriod = 0;
        long nextFetch = 0;
        long oldPeriod = seed.getUpdatePeriod();
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        if (null == outlinks || outlinks.size() < 1) {
            newPeriod = oldPeriod * 2;
            if (newPeriod > MAX_PERIOD) {
                newPeriod = MAX_PERIOD;
            }
        } else {
            newPeriod = oldPeriod / 2;
            if (newPeriod < MIN_PERIOD) {
                newPeriod = MIN_PERIOD;
            }
        }
        nextFetch = seed.getNextFetch() + newPeriod - oldPeriod;
        if (nextFetch < now) {
            nextFetch = now + MIN_PERIOD;
        }
        seed.setNextFetch(nextFetch);
        seed.setUpdatePeriod(newPeriod);
        this.seedDao.update(seed);
        logger.info("change seed url=" + doc.getUrl() + " newPeriod="
                    + newPeriod + " nextFetch=" + nextFetch);
        return true;
    }

}
