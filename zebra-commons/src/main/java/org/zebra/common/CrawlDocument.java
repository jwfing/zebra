package org.zebra.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.http.HttpClientFetcher;

// document crawled by httpclient.
public class CrawlDocument {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private static final String DEFAULT_CHARSET = "UTF-8";

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
            return new String(contentBytes);
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

    private byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        int totalRead = 0;
        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                totalRead += nRead;
                if (totalRead > HttpClientFetcher.MAX_DOWNLOAD_SIZE) {
                    return null;
                }
            }
            buffer.flush();
            return buffer.toByteArray();
        } finally {
            is.close();
        }
    }

    public boolean setContent(InputStream is, int size, boolean isBinary) {
        if (null == is) {
            return false;
        }
        this.isBinary = isBinary;
        try {
            this.contentBytes = readStream(is);
        } catch (IOException ex) {
            this.contentBytes = null;
        }
        return this.contentBytes != null;
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
        return (String) this.urlInfo.getFeature(key);
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
