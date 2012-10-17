package org.zebra.common.http;

import java.io.*;
import java.util.*;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.*;
import org.zebra.common.utils.*;

public class HttpClientFetcher implements Fetcher {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private static DefaultHttpClient httpclient = null;
    public static final int MAX_DOWNLOAD_SIZE = Configuration.getIntProperty(
            Configuration.PATH_FETCHER_MAXDOWNLOAD_SIZE, 4*1024*1024);
    private static final boolean show404Pages = Configuration.getBooleanProperty(
            Configuration.PATH_FETCHER_SHOW404, false);
    private static final boolean ignoreIfBinary = Configuration.getBooleanProperty(
            Configuration.PATH_FETCHER_IGNOREBINARY, true);
    private static IdleConnectionMonitorThread connectionMonitorThread = null;

    static {
        java.security.Security.setProperty("networkaddress.cache.ttl", "86400");

        Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
        // always trust
        Scheme https =
                new Scheme("https", 443, new SSLSocketFactory(SslContextFactory.getClientContext()));

        SchemeRegistry sr = new SchemeRegistry();
        sr.register(http);
        sr.register(https);

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(sr);
        cm.setMaxTotal(Configuration.getIntProperty(Configuration.PATH_FETCHER_TOTAL_CONN, 512));
        cm.setDefaultMaxPerRoute(Configuration.getIntProperty(
                Configuration.PATH_FETCHER_MAXCONN_PERHOST, 100));
        httpclient = new DefaultHttpClient(cm);
        
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
                Configuration.getIntProperty(Configuration.PATH_FETCHER_CONNECTION_TIMEOUT, 60000));
        HttpConnectionParams.setSoTimeout(httpclient.getParams(),
                Configuration.getIntProperty(Configuration.PATH_FETCHER_SOCKET_TIMEOUT, 20000));
        // disable Nagle's algorithm, in order to decrease network latency and
        // increase performance
        HttpConnectionParams.setTcpNoDelay(httpclient.getParams(), true);
        HttpProtocolParams.setUserAgent(httpclient.getParams(),
                Configuration.getStringProperty(Configuration.PATH_FETCHER_USERAGENT,
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Ubuntu/11.10 Chromium/14.0.835.202 Chrome/14.0.835.202 Safari/535.1"));
    }

    public synchronized static void startConnectionMonitorThread() {
        if (connectionMonitorThread == null) {
            connectionMonitorThread = new IdleConnectionMonitorThread(
                    (ThreadSafeClientConnManager)httpclient.getConnectionManager());
        }
        connectionMonitorThread.start();
    }

    public synchronized static void stopConnectionMonitorThread() {
        if (connectionMonitorThread != null) {
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
            get.addHeader("Accept",
                    "text/html,text/css,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8");
            get.addHeader("Accept-Language", "en, zh, zh-CN;q=0.8");
            get.addHeader("Accept-Charset", "utf-8, utf-16, GBK, *;q=0.1");
            get.addHeader("Accept-Encoding", "deflate, gzip");
            get.addHeader("Connection", "Keep-Alive");
            get.addHeader("Referer", toFetchURL);
            HttpResponse response = httpclient.execute(get);
            entity = response.getEntity();
            org.apache.http.Header ceheader = entity.getContentEncoding();
            if (null != ceheader) {
                for (HeaderElement element : ceheader.getElements()) {
                    if (element.getName().equalsIgnoreCase("gzip")) {
                        entity = new GzipDecompressingEntity(response.getEntity());
                        response.setEntity(entity);
                        break;
                    } else if (element.getName().equalsIgnoreCase("deflate")) {
                        entity = new DeflateDecompressingEntity(response.getEntity());
                        response.setEntity(entity);
                        break;
                    }
                }
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
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
                if (size > MAX_DOWNLOAD_SIZE) {
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
                            return FetchStatus.PageIsBinary;
                        }
                    }
                }

                if (page.setContent(entity.getContent(), (int) size, isBinary)) {
                    return FetchStatus.OK;
                } else {
                    logger.warn("failed to read document content. url=" + toFetchURL);
                    return FetchStatus.PageLoadError;
                }
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
                    entity.getContent().close();
                }
                if (get != null) {
                    get.abort();
                }
            } catch (Exception e) {
                ;
            }
        }
        return FetchStatus.UnknownError;
    }
}
