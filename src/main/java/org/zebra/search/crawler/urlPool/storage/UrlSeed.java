package org.zebra.search.crawler.urlPool.storage;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.zebra.search.crawler.common.UrlInfo;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public final class UrlSeed implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String FEATURES_SEPERATOR = "\t";
	private static final String KV_SEPERATOR = "=";
	
	@PrimaryKey
	private String url = "";
	private int level = 0;
	private String features = "";

	public UrlSeed(String url, int level, String features) {
		this.url = url;
		this.level = level;
		this.features = features;
	}
	public UrlSeed() {
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UrlSeed url2 = (UrlSeed) o;
		if (url == null) {
			return false;
		}
		return url.equals(url2.getUrl());
	}

	public static UrlSeed generateInstanceFromUrlInfo(UrlInfo url, int level) {
		if (null == url) {
			return null;
		}
		UrlSeed seed = new UrlSeed();
		seed.setUrl(url.getUrl());
		seed.setLevel(level);

		Map<String, Object> features = url.getFeatures();
		if (null != features) {
	        Set<Entry<String, Object> > entries = features.entrySet();
	        StringBuffer sb = new StringBuffer();
	        for (Entry<String, Object> entry : entries) {
	        	sb.append(entry.getKey() + KV_SEPERATOR + entry.getValue().toString() + FEATURES_SEPERATOR);
	        }
	        String featureString = sb.toString();
	        if (!featureString.isEmpty()) {
	            featureString = featureString.substring(0, featureString.lastIndexOf(FEATURES_SEPERATOR));
	        }
	        seed.setFeatures(featureString);
		} else {
			seed.setFeatures("");
		}

        return seed;
	}
	public static UrlInfo convert2Info(UrlSeed seed) {
		if (null == seed) {
			return null;
		}

		UrlInfo info = new UrlInfo(seed.getUrl());
		info.setFinalUrl(seed.getUrl());

		String features = seed.getFeatures();
		String[] kvs = features.split(FEATURES_SEPERATOR);
		int pos = 0;
		String key = null;
		String value = null;
		for (String kv : kvs) {
			pos = kv.indexOf(KV_SEPERATOR);
			key = kv.substring(0, pos);
			value = kv.substring(pos + 1);
			info.addFeature(key, value);
		}

		return info;
	}
}
