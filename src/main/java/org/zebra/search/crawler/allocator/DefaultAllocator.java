package org.zebra.search.crawler.allocator;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.Configuration;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.urlPool.UrlSelector;

public class DefaultAllocator implements Allocator {
    private static final Logger logger = Logger.getLogger(DefaultAllocator.class.getName());

    private UrlSelector selector = null;
    private SeedCollection collection = null;
    private int onceScanInterval = 1;
    private List<Scanner> scanners = null;

    private class Scanner extends Thread {
        private int level = -1;
        private int intervalMinutes = 0;
        private boolean isRepeat = false;

        public Scanner(int level, int intervalMinutes, boolean isRepeat) {
            this.level = level;
            this.intervalMinutes = intervalMinutes;
            this.isRepeat = isRepeat;
        }

        public void run() {
            boolean ret = false;
            while (isAlive()) {
                if (selector != null && collection != null) {
                    List<UrlInfo> urls = null;
                    if (isRepeat) {
                        urls = selector.retrieveRepeatUrls(this.level, 0, 1024);
                    } else {
                        urls = selector.retrieveOnceUrls(1024, 0, true);
                    }
                    if (null == urls || urls.size() == 0) {
                        logger.info("got nothing with level:" + this.level);
                    } else {
                        logger.info("retrieve " + urls.size() + " urls with level=" + this.level
                                + " repeat=" + this.isRepeat);
                        ret = collection.putUrls(urls);
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

    public void initialize() {
        int scannerThreadnum = Configuration.getIntProperty(Configuration.PATH_ALLOC_SCAN_NUM, 1);
        scanners = new ArrayList<Scanner>();
        int level = 0;
        int interval = 0;
        if (scannerThreadnum <= 0) {
            logger.warn("invalid configuration for crawler.alloc.scans=" + scannerThreadnum);
        }
        for (int i = 0; i < scannerThreadnum; i++) {
            level = Configuration.getIntProperty(Configuration.PATH_ALLOC_SCAN_THREAD + i
                    + Configuration.CONST_LEVEL_SUFFIX, 1);
            interval = Configuration.getIntProperty(Configuration.PATH_ALLOC_SCAN_THREAD + i
                    + Configuration.CONST_INTERVAL_SUFFIX, 3);
            if (level < 0 || interval <= 0) {
                logger.warn("invalid configuration for crawler.alloc.scan, item index:" + i
                        + ", level:" + level + ", interval:" + interval);
                continue;
            }
            Scanner scanner = new Scanner(level, interval, true);
            scanners.add(scanner);
            logger.info("initialize scaner thread. level=" + level + ", interval=" + interval);
        }
        interval = Configuration.getIntProperty(Configuration.PATH_ALLOC_SCAN_ONCE_INTERVAL, 3);
        if (interval <= 0) {
            logger.warn("invalid configuration for crawler.alloc.once.scan, interval:" + interval);
        } else {
            Scanner scanner = new Scanner(0, interval, false);
            scanners.add(scanner);
        }
        for (Scanner scaner : scanners) {
            scaner.start();
        }
    }

    public void destory() {
        if (null == scanners) {
            return;
        }
        for (Scanner scaner : scanners) {
            try {
                scaner.join(3000);
            } catch (Exception ex) {
                logger.warn("exception encountered, cause:" + ex.getMessage());
            }
        }
    }

    public List<UrlInfo> getUrls(int max) {
        if (null != this.collection) {
            return this.collection.getUrls(max);
        }
        return null;
    }

    public UrlSelector getSelector() {
        return selector;
    }

    public void setSelector(UrlSelector selector) {
        this.selector = selector;
    }

    public SeedCollection getCollection() {
        return collection;
    }

    public void setCollection(SeedCollection collection) {
        this.collection = collection;
    }

    public int getOnceScanInterval() {
        return onceScanInterval;
    }

    public void setOnceScanInterval(int onceScanInterval) {
        this.onceScanInterval = onceScanInterval;
    }
}
