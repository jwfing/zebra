package org.zebra.search.crawler.deduper;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.fetcher.Fetcher;
import org.zebra.search.crawler.util.StringUtil;

public class HashDeduper implements Deduper {
    private static final Logger logger = Logger.getLogger(HashDeduper.class);

    private ConcurrentHashMap<Long, Boolean> _dedupIDs = new ConcurrentHashMap<Long, Boolean>(
            1024 * 1024 * 2);// max size 2M

    public Map<String, Boolean> dedup(List<UrlInfo> urls) {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        String urlStr = "";
        Long hashValue = new Long(0l);
        for (UrlInfo url : urls) {
            urlStr = url.getUrl();
            hashValue = new Long(StringUtil.FNVHash(urlStr));
            if (this._dedupIDs.containsKey(hashValue)) {
                result.put(urlStr, true);
            } else {
                result.put(urlStr, false);
                this._dedupIDs.putIfAbsent(hashValue, true);
            }
        }
        return result;
    }

    public List<Boolean> juegeDeduped(List<UrlInfo> urls) {
        List<Boolean> result = new ArrayList<Boolean>();
        String urlStr = "";
        Long hashValue = new Long(0l);
        for (UrlInfo url : urls) {
            urlStr = url.getUrl();
            hashValue = new Long(StringUtil.FNVHash(urlStr));
            if (this._dedupIDs.containsKey(hashValue)) {
                result.add(true);
            } else {
                result.add(false);
            }
        }
        return result;
    }

    public boolean deleteInvalidUrl(List<UrlInfo> urls) {
        logger.warn("no implements this method");
        return false;
    }

    public boolean checkpoint(String fileName) {
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            DataOutput dos = new DataOutputStream(fos);
            Set<Entry<Long, Boolean>> entries = _dedupIDs.entrySet();
            int total = entries.size();
            dos.writeInt(total);
            for (Entry<Long, Boolean> entry : entries) {
                dos.writeLong(entry.getKey());
                dos.writeBoolean(entry.getValue());
            }
            logger.info("successfully dump to file " + fileName);
            return true;
        } catch (FileNotFoundException ex) {
            logger.warn("file not found. filename=" + fileName);
        } catch (IOException ex) {
            logger.warn("failed to write to file(" + fileName + "), cause:" + ex.getMessage());
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ;
                }
            }
        }
        return false;
    }

    public boolean reload(String fileName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            DataInput dis = new DataInputStream(fis);
            int total = dis.readInt();
            Long hashValue;
            Boolean dedupResult = new Boolean(true);
            for (int i = 0; i < total; i++) {
                hashValue = dis.readLong();
                dedupResult = dis.readBoolean();
                this._dedupIDs.put(hashValue, dedupResult);
            }
            logger.info("initialized from file(" + fileName + ") with total(" + total
                    + ") existed URLs");
            return true;
        } catch (FileNotFoundException ex) {
            logger.warn("file not found. filename=" + fileName);
        } catch (IOException ex) {
            logger.warn("failed to read from file(" + fileName + "), cause:" + ex.getMessage());
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    ;
                }
            }
        }
        return false;
    }

    public boolean isFull() {
        return false;
    }

    public void clear() {
        this._dedupIDs.clear();
    }
}
