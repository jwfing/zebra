package org.zebra.search.crawler.common;

import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.fetcher.HttpClientFetcher;

public class CrawlDocument {
	private static final Logger logger = Logger.getLogger(CrawlDocument.class);
	private static final String DEFAULT_CHARSET = "GB18030";

//	private String contentString = "";
	private UrlInfo urlInfo = null;
	private int fetchStatus = FetchStatus.OK;
	private Date fetchTime = null;
	private boolean isBinary = false;
	private byte[] contentBytes = null;
	private Context context = null;

	public void setFetchTime(Date date) {
		this.fetchTime = date;
	}
	public Date getFetchTime() {
		return this.fetchTime;
	}

	public String getContentString() {
		if (null == contentBytes) {
			return null;
		}
		try {
		    return new String(contentBytes, DEFAULT_CHARSET);
		} catch (Exception ex) {
			return null;
		}
	}
	public void setContentString(String contentString) {
		this.contentBytes = contentString.getBytes();
	}
	public byte[] getContentBytes() {
		return this.contentBytes;
	}
	public boolean setContent(InputStream is, int size, boolean isBinary) {
		if (null == is) {
			return false;
		}
		if (size <= 0) {
			size = HttpClientFetcher.MAX_DOWNLOAD_SIZE;
		}
		boolean result = true;
		this.contentBytes = new byte[size];
		for (byte tmpItem : this.contentBytes) {
			tmpItem = 0;
		}
	    try {
	    	int readSize = 0;
	    	int tmpSize = 0;
	    	while(readSize < size) {
	    		tmpSize = is.read(contentBytes, readSize, size - readSize);
	    		if (tmpSize < 0) {
	    			break;
	    		}
	    		readSize += tmpSize;
	    	}
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    	result = false;
	    } finally {
	    	try {
	    	    is.close();
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    	}
	    }
		this.isBinary = isBinary;
		return result;
	}

	public UrlInfo getUrlInfo() {
		return urlInfo;
	}
	public String getUrl() {
		if (null == this.urlInfo) {
			return "";
		}
		return this.urlInfo.getUrl();
	}
	public void setUrlInfo(UrlInfo urlInfo) {
		this.urlInfo = urlInfo;
	}

	public boolean addFeature(String key, String value) {
		if (null == this.urlInfo) {
			return false;
		}
		this.urlInfo.addFeature(key, value);
		return true;
	}
	public String getFeature(String key) {
		if (null == this.urlInfo) {
			return null;
		}
		return (String)this.urlInfo.getFeature(key);
	}

	public int getFetchStatus() {
		return fetchStatus;
	}
	public void setFetchStatus(int fetchStatus) {
		this.fetchStatus = fetchStatus;
	}

	public boolean isBinary() {
		return isBinary;
	}
	public void setBinary(boolean isBinary) {
		this.isBinary = isBinary;
	}

	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
}
