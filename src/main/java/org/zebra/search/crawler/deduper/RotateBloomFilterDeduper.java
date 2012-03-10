package org.zebra.search.crawler.deduper;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.UrlInfo;

public class RotateBloomFilterDeduper implements Deduper {
    private static final Logger logger = Logger.getLogger(RotateBloomFilterDeduper.class);
    private final static int DEFAULT_ROTATE_NUM = 4;
    private int storeSize = 0;
    private int rotateNum = 0;
    private BloomFilterDeduper[] dedupers;

    public Map<String, Boolean> dedup(List<UrlInfo> urls) {
        return null;
    }

    public List<Boolean> juegeDeduped(List<UrlInfo> urls) {
        return null;
    }

    public boolean deleteInvalidUrl(List<UrlInfo> urls) {
        return false;
    }

    public boolean isFull() {
        return true;
    }

    public void clear() {
        ;
    }

    public boolean checkpoint(String fileName) {
        return false;
    }

    public boolean reload(String fileName) {
        return false;
    }

}
