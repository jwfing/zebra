package org.zebra.spider;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;
import org.zebra.common.Configuration;
import org.zebra.common.UrlInfo;
import org.zebra.common.domain.dao.*;
import org.zebra.common.domain.*;

@Component
public class DefaultAllocator implements Allocator {
    private static final Logger logger = Logger.getLogger(DefaultAllocator.class.getName());

    private SeedDao seedDao = null;
    private SeedCollection collection = null;
    private Scanner scanner = null;

    private class Scanner extends Thread {
        private int intervalMinutes = 0;

        public Scanner(int intervalMinutes) {
            this.intervalMinutes = intervalMinutes;
        }

        public void run() {
            boolean ret = false;
            while (isAlive()) {
                if (seedDao != null && collection != null) {
                    List<Seed> urls = new ArrayList<Seed>();
                    long now = System.currentTimeMillis() / 1000;
                    int offset = 0;
                    List<Seed> seeds = seedDao.getSeeds(now, offset, 1000);
                    while (null != seeds && seeds.size() > 0) {
                        urls.addAll(seeds);
                        offset += 1000;
                        seeds = seedDao.getSeeds(now, offset, 1000);
                    }
                    if (null == urls || urls.size() == 0) {
                        logger.info("got nothing...");
                    } else {
                        logger.info("retrieve " + urls.size() + " seeds");
                        ret = collection.putSeeds(urls);
                        if (!ret) {
                            logger.warn("failed to add urls to collection. urlsize=" + urls.size());
                        }
                    }
                } else {
                    logger.warn("in seed scanner thread: selector or collection is null");
                    return;
                }
                try {
                    sleep(this.intervalMinutes * 60000);
                } catch (Exception ex) {
                    logger.warn("exception encountered. cause:" + ex.getMessage());
                }
            }
        }
    }

    public void setSeedDao(SeedDao dao) {
        this.seedDao = dao;
    }

    public SeedDao getSeedDao() {
        return this.seedDao;
    }

    public boolean initialize() {
        int interval = Configuration.getIntProperty(Constants.PATH_ALLOC_SCAN_INTERVAL, 3);
        scanner = new Scanner(interval);
        scanner.start();
        return true;
    }

    public void destory() {
        if (null == scanner) {
            return;
        }
        try {
            scanner.join(3000);
        } catch (Exception ex) {
            logger.warn("exception encountered, cause:" + ex.getMessage());
        }
    }

    public List<Seed> getUrls(int max) {
        if (null != this.collection) {
            return this.collection.getSeeds(max);
        }
        return null;
    }

    public SeedCollection getCollection() {
        return collection;
    }

    public void setCollection(SeedCollection collection) {
        this.collection = collection;
    }
}
