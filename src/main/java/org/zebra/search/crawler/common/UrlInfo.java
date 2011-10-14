package org.zebra.search.crawler.common;

import java.util.*;

public class UrlInfo {
	public static final String FLAG = "";
	public static final String BASE = "";

	private String url = null;
    private String finalUrl = null;
    private Map<String, Object> features = null;
    public UrlInfo(String url) {
    	this.url = url;
    	this.features = new HashMap<String, Object>();
    }
    public String getUrl() {
    	return this.url;
    }
    public void setUrl(String url) {
    	this.url = url;
    }
    public String getFinalUrl() {
		return finalUrl;
	}
	public void setFinalUrl(String finalUrl) {
		this.finalUrl = finalUrl;
	}
	public void addFeature(String key, Object value) {
    	this.features.put(key, value);
    }
    public Object getFeature(String key) {
    	if (this.features.containsKey(key)) {
    		return this.features.get(key);
    	} else {
    		return null;
    	}
    }
    public Map<String, Object> getFeatures() {
    	return this.features;
    }
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(this.url);
    	Set<Map.Entry<String, Object> > entries = this.features.entrySet();
    	
    	for (Map.Entry<String, Object> entry : entries) {
    		sb.append("\t" + entry.getKey() + "=" + entry.getValue().toString());
    	}
    	return sb.toString();
    }
}
