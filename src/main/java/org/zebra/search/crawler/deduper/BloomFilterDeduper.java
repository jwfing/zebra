package org.zebra.search.crawler.deduper;

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.onelab.filter.Key;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.util.AtomicCounter;

public class BloomFilterDeduper implements Deduper {
    private static final Logger logger = Logger.getLogger(BloomFilterDeduper.class);

    // on test, when size is 1250000 bits, the 53000th record confict
    public final static int DEFAULT_RATE = 1250000 / 50000;
    private int spaceRate = DEFAULT_RATE;

    private int size = 0;
    private int hashNum = 10;
    private AtomicCounter storeCounter = new AtomicCounter();
    private Lock writeLock = new ReentrantLock();
    private org.onelab.filter.BloomFilter bloomFilter = null;

    public BloomFilterDeduper(int size) {
        this.size = size;
        this.bloomFilter = new org.onelab.filter.BloomFilter(this.size, this.hashNum);
    }

    public boolean isFull() {
        return this.storeCounter.get() >= this.size;
    }

    public void clear() {
        this.writeLock.lock();
        this.storeCounter.set(0);
        this.bloomFilter = new org.onelab.filter.BloomFilter(this.size, this.hashNum);;
        this.writeLock.unlock();
    }

    protected boolean has(String url) {
        if (null == url || url.isEmpty()) {
            return false;
        }
        if (this.storeCounter.get() <= 0) {
            return false;
        }
        Key key = new Key(url.getBytes());
        return this.bloomFilter.membershipTest(key);
    }

    protected void store(String key) {
        if (null == key || key.length() <= 0) {
            return;
        }

        this.writeLock.lock();
        Key inerKey = new Key(key.getBytes());
        this.bloomFilter.add(inerKey);
        this.writeLock.unlock();

        this.storeCounter.incrementAndGet();
    }

    protected boolean dedup(String url) {
        if (has(url)) {
            return true;
        }
        store(url);
        return false;
    }

    public Map<String, Boolean> dedup(List<UrlInfo> urls) {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        for (UrlInfo url : urls) {
            boolean tmp = dedup(url.getUrl());
            result.put(url.getUrl(), tmp);
        }
        return result;
    }

    public List<Boolean> juegeDeduped(List<UrlInfo> urls) {
        List<Boolean> result = new ArrayList<Boolean>();
        for (UrlInfo url : urls) {
            boolean tmp = has(url.getUrl());
            result.add(tmp);
        }
        return result;
    }

    public boolean deleteInvalidUrl(List<UrlInfo> urls) {
        logger.warn("impossible path.");
        return false;
    }

    public boolean checkpoint(String fileName) {
        if (null == fileName || fileName.isEmpty()) {
            return false;
        }
        boolean result = false;
        File file = new File(fileName);
        this.writeLock.lock();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            DataOutput dos = new DataOutputStream(fos);
            int storeNum = this.storeCounter.get();
            dos.writeInt(storeNum);
            dos.writeInt(this.size);
            this.bloomFilter.write(dos);
            fos.flush();
            fos.close();
            result = true;
        } catch (FileNotFoundException ex) {
            logger.warn("failed to write file. cause:" + ex.getMessage());
        } catch (IOException ex) {
            logger.warn("failed to write file. cause:" + ex.getMessage());
        } finally {
            this.writeLock.unlock();
        }
        return result;
    }

    public boolean reload(String fileName) {
        if (null == fileName || fileName.isEmpty()) {
            return false;
        }
        this.writeLock.lock();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            DataInput dis = new DataInputStream(fis);
            int counter = dis.readInt();
            this.size = dis.readInt();
            this.storeCounter.set(counter);
            this.bloomFilter.readFields(dis);
            fis.close();
            return true;
        } catch (FileNotFoundException ex) {
            logger.warn("failed to read file. cause:" + ex.getMessage());
        } catch (IOException ex) {
            logger.warn("failed to read file. cause:" + ex.getMessage());
        } finally {
            this.writeLock.unlock();
        }
        return false;
    }
}
