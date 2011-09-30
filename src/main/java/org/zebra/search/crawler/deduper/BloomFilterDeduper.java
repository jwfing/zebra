package org.zebra.search.crawler.deduper;

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.onelab.filter.Key;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.util.AtomicCounter;

public class BloomFilterDeduper implements Deduper{
	private static final Logger logger = Logger.getLogger(BloomFilterDeduper.class);

	// on test, when size is 1250000 bits, the 53000th record confict
    public final static int DEFAULT_RATE = 1250000/50000;
    
    private int spaceRate = DEFAULT_RATE;
    private int size = 0;
    private int hashNum = 10;
    private AtomicCounter storeCounter = new AtomicCounter();
    private Lock writeLock = null;
    private org.onelab.filter.BloomFilter bloomFilter = null;
    public BloomFilterDeduper(int size) {
    	this.size = size;
    	this.writeLock = new ReentrantLock();
    }

    public boolean isFull() {
        return this.storeCounter.get() >= this.size;
    }
    
    public void clear() {
    	this.writeLock.lock();
    	this.storeCounter.set(0);
        this.bloomFilter = null;
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

    protected void constructInerBloomfilter() {
    	this.writeLock.lock();
        int actualNum = this.spaceRate * this.size;
        if(actualNum <= 0) {
            actualNum = Integer.MAX_VALUE - 1;
        }
    	this.bloomFilter = new org.onelab.filter.BloomFilter(actualNum, this.hashNum);
    	this.writeLock.unlock();
    }

    protected void store(String key) {
        if(null == key || key.length() <= 0) {
            return;
        }

        this.writeLock.lock();
    	if(this.storeCounter.get() <= 0) {
    		constructInerBloomfilter();
    	}

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
    	    int storeNum = this.storeCounter.get();
    	    fos.write(storeNum);
    	    fos.write(this.size);
    	    if (storeNum > 0) {
//    	    	BitSet bs = null;
    	    }
    	    result = true;
    	} catch (FileNotFoundException ex) {
    		;
    	} catch (IOException ex) {
    		;
    	} finally {
    		this.writeLock.unlock();
    	}
    	return result;
    }
    public boolean reload(String fileName) {
    	if (null == fileName || fileName.isEmpty()) {
    		return false;
    	}
    	File file = new File(fileName);
    	return false;
    }
}
