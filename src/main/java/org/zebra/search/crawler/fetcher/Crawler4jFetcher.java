package org.zebra.search.crawler.fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.zebra.search.crawler.common.*;

public class Crawler4jFetcher implements Fetcher{
	private static final Logger logger = Logger.getLogger(Crawler4jFetcher.class);

	private static ThreadSafeClientConnManager connectionManager;

	private static DefaultHttpClient httpclient;

	private static Object mutex = Crawler4jFetcher.class.toString() + "_MUTEX";

	private static int processedCount = 0;
	private static long startOfPeriod = 0;
	private static long lastFetchTime = 0;

	private static long politenessDelay = Configuration.getIntProperty("fetcher.default_politeness_delay", 200);

	public static final int MAX_DOWNLOAD_SIZE = Configuration.getIntProperty("fetcher.max_download_size", 1048576);

	private static final boolean show404Pages = Configuration.getBooleanProperty("logging.show_404_pages", true);

	private static IdleConnectionMonitorThread connectionMonitorThread = null;

	public static long getPolitenessDelay() {
		return politenessDelay;
	}

	public static void setPolitenessDelay(long politenessDelay) {
		politenessDelay = politenessDelay;
	}

	static {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
		paramsBean.setVersion(HttpVersion.HTTP_1_1);
		paramsBean.setContentCharset("GB2312");
		paramsBean.setUseExpectContinue(false);

		params.setParameter("http.useragent", Configuration.getStringProperty("fetcher.user_agent",
				"crawler4j (http://code.google.com/p/crawler4j/)"));

		params.setIntParameter("http.socket.timeout", Configuration.getIntProperty("fetcher.socket_timeout", 20000));

		params.setIntParameter("http.connection.timeout",
				Configuration.getIntProperty("fetcher.connection_timeout", 30000));

		params.setBooleanParameter("http.protocol.handle-redirects", false);

		ConnPerRouteBean connPerRouteBean = new ConnPerRouteBean();
		connPerRouteBean.setDefaultMaxPerRoute(Configuration.getIntProperty("fetcher.max_connections_per_host", 100));
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRouteBean);
		ConnManagerParams.setMaxTotalConnections(params,
				Configuration.getIntProperty("fetcher.max_total_connections", 100));

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		if (Configuration.getBooleanProperty("fetcher.crawl_https", false)) {
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		}

		connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
		logger.setLevel(Level.INFO);
		httpclient = new DefaultHttpClient(connectionManager, params);
	}

	public synchronized static void startConnectionMonitorThread() {
		if (connectionMonitorThread == null) {
			connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
		}
		connectionMonitorThread.start();
	}

	public synchronized static void stopConnectionMonitorThread() {
		if (connectionMonitorThread != null) {
			connectionManager.shutdown();
			connectionMonitorThread.shutdown();
		}
	}

	public CrawlDocument fetchDocument(UrlInfo url) {
		CrawlDocument page = new CrawlDocument();
		page.setUrlInfo(url);
		String toFetchURL = url.getUrl();
		HttpGet get = null;
		HttpEntity entity = null;
		try {
			get = new HttpGet(toFetchURL);
			synchronized (mutex) {
				long now = (new Date()).getTime();
				if (now - startOfPeriod > 10000) {
					logger.info("Number of pages fetched per second: " + processedCount
							/ ((now - startOfPeriod) / 1000));
					processedCount = 0;
					startOfPeriod = now;
				}
				processedCount++;

				if (now - lastFetchTime < politenessDelay) {
					Thread.sleep(politenessDelay - (now - lastFetchTime));
				}
				lastFetchTime = (new Date()).getTime();
			}
			HttpResponse response = httpclient.execute(get);
			entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				page.setFetchStatus(statusCode);
				if (statusCode != HttpStatus.SC_NOT_FOUND) {
					if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
						Header header = response.getFirstHeader("Location");
						if (header != null) {
							String movedToUrl = header.getValue();
							page.getUrlInfo().setUrl(movedToUrl);
						} else {
							page.getUrlInfo().setUrl(null);
						}
						page.setFetchStatus(statusCode);
						return page;
					}
					logger.info("Failed: " + response.getStatusLine().toString() + ", while fetching " + toFetchURL);
				} else if (show404Pages) {
					logger.info("Not Found: " + toFetchURL);
				}
				return null;
			}

			if (entity != null) {
				long size = entity.getContentLength();
				if (size == -1) {
					Header length = response.getLastHeader("Content-Length");
					if (length == null) {
						length = response.getLastHeader("Content-length");
					}
					if (length != null) {
						size = Integer.parseInt(length.getValue());
					} else {
						size = -1;
					}
				}
				if (size > MAX_DOWNLOAD_SIZE) {
					entity.consumeContent();
					return null;
				}

				boolean isBinary = false;

				Header type = entity.getContentType();
				if (type != null) {
					String typeStr = type.getValue().toLowerCase();
					if (typeStr.contains("image") || typeStr.contains("audio") || typeStr.contains("video")) {
						isBinary = true;
     					return null;
					}
				}

				if (page.setContent(entity.getContent(), (int) size, isBinary)) {
					return page;
				} else {
					return null;
				}
			} else {
				get.abort();
			}
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage() + " while fetching " + toFetchURL);
			return null;
		} catch (IllegalStateException e) {
			// ignoring exceptions that occur because of not registering https
			// and other schemes
		} catch (Exception e) {
			if (e.getMessage() == null) {
				logger.error("Error while fetching " + page.getUrl());
			} else {
				logger.error(e.getMessage() + " while fetching " + page.getUrl());
			}
		} finally {
			try {
				if (entity != null) {
					entity.consumeContent();
				} else if (get != null) {
					get.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

    public List<CrawlDocument> fetchDocuments(List<UrlInfo> urls) {
    	List<CrawlDocument> result = new ArrayList<CrawlDocument>();
    	for (UrlInfo url : urls) {
    		CrawlDocument doc = fetchDocument(url);
    		if (null == doc) {
    			continue;
    		}
    		result.add(doc);
    	}
    	return result;
    }

    public static void setProxy(String proxyHost, int proxyPort) {
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	public static void setProxy(String proxyHost, int proxyPort, String username, String password) {
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(username, password));
		setProxy(proxyHost, proxyPort);
	}
    

}
