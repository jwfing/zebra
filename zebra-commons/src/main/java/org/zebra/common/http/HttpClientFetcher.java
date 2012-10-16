package org.zebra.common.http;

import java.io.*;
import java.util.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
//import org.apache.http.conn.params.ConnManagerParams;
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

import org.zebra.common.*;
import org.zebra.common.utils.*;

public class HttpClientFetcher implements Fetcher {
    private static final Logger logger = Logger.getLogger(HttpClientFetcher.class);
    private static ThreadSafeClientConnManager connectionManager = null;
    private static DefaultHttpClient httpclient = null;
    public static final int MAX_DOWNLOAD_SIZE = Configuration.getIntProperty(
            Configuration.PATH_FETCHER_MAXDOWNLOAD_SIZE, 8*1024*1024);
    private static final boolean show404Pages = Configuration.getBooleanProperty(
            Configuration.PATH_FETCHER_SHOW404, false);
    private static final boolean ignoreIfBinary = Configuration.getBooleanProperty(
            Configuration.PATH_FETCHER_IGNOREBINARY, true);
    private static IdleConnectionMonitorThread connectionMonitorThread = null;

    static {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
        paramsBean.setVersion(HttpVersion.HTTP_1_1);
        paramsBean.setContentCharset(GB2312_CHARSET);
        paramsBean.setUseExpectContinue(false);

        params.setParameter(HTTP_USERAGENT, Configuration.getStringProperty(
                Configuration.PATH_FETCHER_USERAGENT,
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Ubuntu/11.10 Chromium/14.0.835.202 Chrome/14.0.835.202 Safari/535.1"));
        params.setIntParameter(HTTP_SOCK_TIMEOUT,
                Configuration.getIntProperty(Configuration.PATH_FETCHER_SOCKET_TIMEOUT, 20000));
        params.setIntParameter(HTTP_CONN_TIMEOUT,
                Configuration.getIntProperty(Configuration.PATH_FETCHER_CONNECTION_TIMEOUT, 60000));
        params.setBooleanParameter(HTTP_HANDLE_REDIRECT, false);

        ConnPerRouteBean connPerRouteBean = new ConnPerRouteBean();
        connPerRouteBean.setDefaultMaxPerRoute(Configuration.getIntProperty(
                Configuration.PATH_FETCHER_MAXCONN_PERHOST, 100));
        /*
         * ConnManagerParams.setMaxConnectionsPerRoute(params,
         * connPerRouteBean); ConnManagerParams.setMaxTotalConnections(params,
         * Configuration
         * .getIntProperty(Configuration.PATH_FETCHER_MAXCONN_TOTAL, 100));
         */
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry
                .register(new Scheme(PROTOCOL_HTTP, PlainSocketFactory.getSocketFactory(), 80));

        if (Configuration.getBooleanProperty(Configuration.PATH_FETCHER_ENABLEHTTPS, false)) {
            schemeRegistry.register(new Scheme(PROTOCOL_HTTPS, SSLSocketFactory.getSocketFactory(),
                    443));
        }

        connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
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

    public static void setProxy(String proxyHost, int proxyPort) {
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    public static void setProxy(String proxyHost, int proxyPort, String username, String password) {
        httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
                new UsernamePasswordCredentials(username, password));
        setProxy(proxyHost, proxyPort);
    }

    public CrawlDocument fetchDocument(UrlInfo url) {
        if (null == url) {
            logger.warn("url info is null");
            return null;
        }
        waitMoment(url);
        CrawlDocument doc = new CrawlDocument();
        doc.setUrlInfo(url);
        int fetchStatus = fetchViaHttpClient(url, doc);
        doc.setFetchStatus(fetchStatus);
        doc.setFetchTime(new Date());
        return doc;
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

    /*
     * 默认不等待，直接连续抓取
     */
    protected boolean waitMoment(UrlInfo url) {
        return true;
    }

    protected int fetchViaHttpClient(UrlInfo url, CrawlDocument page) {
        String toFetchURL = url.getUrl();
        HttpGet get = new HttpGet(toFetchURL);
        HttpEntity entity = null;
        try {
            HttpResponse response = httpclient.execute(get);
            entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                if (statusCode != HttpStatus.SC_NOT_FOUND) {
                    if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                            || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                        Header header = response.getFirstHeader("Location");
                        if (header != null) {
                            String movedToUrl = header.getValue();
                            url.setUrl(movedToUrl);
                        } else {
                            url.setUrl(null);
                        }
                        logger.warn("document is moved. url=" + toFetchURL);
                        return FetchStatus.Moved;
                    }
                    logger.info("Failed: " + response.getStatusLine().toString()
                            + ", while fetching " + toFetchURL);
                } else if (show404Pages) {
                    logger.warn("Not Found: " + toFetchURL);
                }
                return statusCode;
            }

            String uri = get.getURI().toString();
            if (!uri.equals(toFetchURL)) {
                if (!UrlUtil.getCanonicalURL(uri).equals(toFetchURL)) {
                    url.setFinalUrl(uri);
                }
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
                    logger.warn("document is too big. url=" + toFetchURL);
                    return FetchStatus.PageTooBig;
                }

                boolean isBinary = false;

                Header type = entity.getContentType();
                if (type != null) {
                    String typeStr = type.getValue().toLowerCase();
                    // TODO: we can set mime-types to doc features.
                    logger.debug(ProcessorUtil.COMMON_PROP_CONTENTTYPE + ": " + typeStr);
                    page.addFeature(ProcessorUtil.COMMON_PROP_CONTENTTYPE, typeStr);

                    if (typeStr.contains("audio") || typeStr.contains("video")) {
                        isBinary = true;
                        page.setBinary(true);
                        if (ignoreIfBinary) {
                            entity.consumeContent();
                            return FetchStatus.PageIsBinary;
                        }
                    }
                }

                if (page.setContent(entity.getContent(), (int) size, isBinary)) {
                    return FetchStatus.OK;
                } else {
                    entity.consumeContent();
                    logger.warn("failed to read document content. url=" + toFetchURL);
                    return FetchStatus.PageLoadError;
                }
            } else {
                logger.warn("failed to parse response entity. url=" + toFetchURL);
                get.abort();
            }
        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage() + " while fetching "
                    + toFetchURL);
            return FetchStatus.FatalTransportError;
        } catch (IllegalStateException e) {
            // ignoring exceptions that occur because of not registering https
            // and other schemes
            logger.warn("encounter exception. cause:" + e.getMessage() + " while fetching " + url.getUrl());
        } catch (Exception e) {
            if (e.getMessage() == null) {
                logger.warn("Error while fetching " + url.getUrl());
            } else {
                logger.warn(e.getMessage() + " while fetching " + url.getUrl());
            }
        } finally {
            try {
                if (entity != null) {
                    entity.consumeContent();
                } else if (get != null) {
                    get.abort();
                }
            } catch (Exception e) {
                logger.warn("encounter exception while consume http entiry. cause:" + e.getMessage());
            }
        }
        return FetchStatus.UnknownError;
    }
}
