package org.zebra.spider.plugin;

import java.util.List;
import java.util.ArrayList;
import java.net.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zebra.common.Configuration;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.FetchStatus;
import org.zebra.common.UrlInfo;
import org.zebra.common.AtomicCounter;
import org.zebra.common.flow.Processor;
import org.zebra.common.metrics.Metrics;
import org.zebra.common.metrics.MetricsReporter;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.spider.Constants;

public class NewLinkTriger implements Processor, MetricsReporter {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private HttpClient httpClient = null;
    private final static String metaPoint = Configuration.getStringProperty(
            Constants.META_ENDPOINT_URL, "http://localhost:9900/meta?rt=url&u=");
    private final static boolean urlencoding = Configuration.getBooleanProperty(
            Constants.META_ENDPOINT_URLENCODING, false);
    private AtomicCounter totalCounter = new AtomicCounter();
    private AtomicCounter failedCounter = new AtomicCounter();

    public NewLinkTriger() {
        Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
        SchemeRegistry sr = new SchemeRegistry();
        sr.register(http);
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(sr);
        cm.setMaxTotal(16);
        cm.setDefaultMaxPerRoute(32);
        httpClient = new DefaultHttpClient(cm);
        
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 60000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 60000);
        // disable Nagle's algorithm, in order to decrease network latency and
        // increase performance
        HttpConnectionParams.setTcpNoDelay(httpClient.getParams(), true);
        HttpProtocolParams.setUserAgent(httpClient.getParams(), "Mozilla/5.0 zebra-agent");
    }

    @Override
    public boolean destroy() {
        logger.info("destroyed " + getName());
        return true;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean initialize() {
        logger.info("initialized " + getName());
        return true;
    }

    public List<Metrics> stat() {
        List<Metrics> stats = new ArrayList<Metrics>();
        stats.add(new Metrics("totalTriger", new Long(totalCounter.get()).toString()));
        stats.add(new Metrics("trigerFailed", new Long(failedCounter.get()).toString()));
        return stats;
    }

    @Override
    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter. doc / context is null");
            return false;
        }
        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        for (UrlInfo link : outlinks) {
            String url = link.getUrl();
            if (urlencoding) {
                try {
                    url = URLEncoder.encode(link.getUrl(), "utf-8");
                } catch (Exception ex) {
                    url = link.getUrl();
                    logger.warn("encountered exception while urlencoding. url=" + url);
                }
            }
            totalCounter.incrementAndGet();
            HttpGet httpget = null;
            HttpEntity entity = null;
            try {
                httpget = new HttpGet(metaPoint + url);
                HttpResponse response = this.httpClient.execute(httpget);
                entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK != statusCode) {
                    logger.warn("status code(" + statusCode + ") is invalid. url=" + url);
                }
            } catch (Exception ex) {
                logger.warn("encountered exception. url=" + url + " cause: " + ex.getMessage());
                failedCounter.incrementAndGet();
            } finally {
                try {
                    if (null != entity) {
                        entity.getContent().close();
                    }
                    if (null != httpget) {
                        httpget.abort();
                    }
                } catch (Exception ex) {
                    logger.warn("encounter exception. " + ex);
                }
            }
        }
        return true;
    }

}
