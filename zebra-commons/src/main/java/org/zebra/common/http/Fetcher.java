package org.zebra.common.http;

import java.util.*;

import org.zebra.common.CrawlDocument;
import org.zebra.common.UrlInfo;

public interface Fetcher {
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String GB2312_CHARSET = "GB2312";
	public static final String GBK_CHARSET = "GBK";
	public static final String PROTOCOL_HTTP = "http";
	public static final String PROTOCOL_HTTPS = "https";
	public static final String HTTP_USERAGENT = "http.useragent";
	public static final String HTTP_SOCK_TIMEOUT = "http.socket.timeout";
	public static final String HTTP_CONN_TIMEOUT = "http.connection.timeout";
	public static final String HTTP_HANDLE_REDIRECT = "http.protocol.handle-redirects";
    public CrawlDocument fetchDocument(UrlInfo url);
    public List<CrawlDocument> fetchDocuments(List<UrlInfo> urls);
}
