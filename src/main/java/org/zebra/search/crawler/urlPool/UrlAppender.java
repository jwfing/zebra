package org.zebra.search.crawler.urlPool;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.*;

public class UrlAppender {
    private static final Logger logger = Logger.getLogger(UrlAppender.class);
    private static UrlAppender instance = null;
    private UrlStorage storage = null;

    public static UrlAppender getInstance() {
        if (null == instance) {
            synchronized (UrlAppender.class) {
                if (null == instance) {
                    instance = new UrlAppender();
                }
            }
        }
        return instance;
    }

    private UrlAppender() {
        ;
    }

    public UrlStorage getStorage() {
        return storage;
    }

    public void setStorage(UrlStorage storage) {
        logger.info("initialize urlAppender with urlStorage");
        this.storage = storage;
    }

    public boolean appendOnceUrls(List<UrlInfo> urls) {
        if (null != this.storage) {
            logger.debug("add " + urls.size() + " once urls");
            return this.storage.addOnceUrls(urls);
        }
        logger.warn("internal error: storage object is empty");
        return false;
    }

    public boolean appendRepeatUrls(List<UrlInfo> urls, int level) {
        if (null != this.storage) {
            logger.debug("add " + urls.size() + " repeated urls with level=" + level);
            return this.storage.addRepeatUrls(urls, level);
        }
        logger.warn("internal error: storage object is empty");
        return false;
    }

    public boolean dropAllUrls(int level, Constants.UrlType type) {
        if (null != this.storage) {
            List<UrlInfo> allUrls = new ArrayList<UrlInfo>();
            if (Constants.UrlType.ONCE == type) {
                allUrls = this.storage.selectFromOnceUrls(0, 10240);
                if (null != allUrls && allUrls.size() > 0) {
                    this.storage.dropUrls(allUrls, type);
                }
            } else {
                if (-1 == level) {
                    // delete all;
                    for (int i = 1; i < 10; i++) {
                        List<UrlInfo> tmp = this.storage.selectFromRepeatUrls(i, 0, 10240);
                        if (null != tmp && tmp.size() > 0) {
                            this.storage.dropUrls(tmp, type);
                        }
                    }
                } else {
                    allUrls = this.storage.selectFromRepeatUrls(level, 0, 10240);
                    if (null != allUrls && allUrls.size() > 0) {
                        this.storage.dropUrls(allUrls, type);
                    }
                }
            }
            return true;
        }
        logger.warn("internal error: storage object is empty");
        return false;
    }
}
