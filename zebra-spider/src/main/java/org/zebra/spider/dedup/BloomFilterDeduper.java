package org.zebra.spider.dedup;

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.UrlInfo;
import org.zebra.common.AtomicCounter;

public class BloomFilterDeduper implements Deduper {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    // on test, when size is 1250000 bits, the 53000th record confict
    public final static int DEFAULT_RATE = 1250000 / 50000;

    private int size = 0;
    private AtomicCounter storeCounter = new AtomicCounter();
    private Lock writeLock = new ReentrantLock();
    private BloomFilter bloomFilter = null;

    public BloomFilterDeduper(int expectedElementSize) {
        this.size = expectedElementSize;
        this.bloomFilter = new BloomFilter(expectedElementSize * DEFAULT_RATE, expectedElementSize);
    }

    public boolean isFull() {
        return this.storeCounter.get() >= this.size;
    }

    public void clear() {
        this.writeLock.lock();
        this.storeCounter.set(0);
        this.bloomFilter = new BloomFilter(this.size * DEFAULT_RATE, this.size);;
        this.writeLock.unlock();
    }

    protected boolean has(String url) {
        if (null == url || url.isEmpty()) {
            return false;
        }
        if (this.storeCounter.get() <= 0) {
            return false;
        }
        return this.bloomFilter.contains(url);
    }

    protected void store(String key) {
        if (null == key || key.length() <= 0) {
            return;
        }

        this.writeLock.lock();
        this.bloomFilter.add(key);
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
            long storeNum = this.storeCounter.get();
            dos.writeLong(storeNum);
            dos.writeInt(this.size);
            dos.writeInt(this.bloomFilter.getDumpSize());
            dos.writeBytes(this.bloomFilter.toBinary());
            fos.flush();
            fos.close();
            logger.info("[CHECKPOINT DUMP] count=" + storeNum + ", size=" + this.size
                    + ", dumpSize=" + this.bloomFilter.getDumpSize());
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
            long counter = dis.readLong();
            this.storeCounter.set(counter);
            this.size = dis.readInt();
            int dumpSize = dis.readInt();
            byte[] binary = new byte[dumpSize];
            dis.readFully(binary);
            this.bloomFilter.reload(binary);
            fis.close();
            logger.info("[CHECKPOINT LOAD] count=" + counter + ", size=" + this.size
                    + ", dumpSize=" + dumpSize);
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
